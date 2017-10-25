/*
MIT License

Copyright (c) 2016 Nosakhare Belvi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */

package com.example.hugo.contadordeestoque

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.android.gms.vision.Frame
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import org.jetbrains.anko.*
import org.jetbrains.anko.db.select
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapterListaProdutos : ArrayAdapter<String>
    private lateinit var todosProdutos : ArrayAdapter<String>
    private lateinit var nomeProduto : AutoCompleteTextView
    private lateinit var quantidade : EditText
    private val quantidadesProdutos = Hashtable<String,Int>()
    private lateinit var fotoTirada : ImageView
    private lateinit var textoCodigo : TextView
    private lateinit var opcoesMenuAdapter : ArrayAdapter<String>
    private val client = AsyncHttpClient()
    private lateinit var menu : Spinner
    private lateinit var lista : ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PopularSQLite(this@MainActivity)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_action_bar)
        opcoesMenuAdapter = ArrayAdapter(this@MainActivity, R.layout.list_layout)
        menu = supportActionBar?.customView?.findViewById<View>(R.id.spinner) as Spinner
        opcoesMenuAdapter.add("")
        opcoesMenuAdapter.add("Configurações")
        opcoesMenuAdapter.add("Finalizar")
        menu.adapter = opcoesMenuAdapter
        menu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position)
                {
                    1->
                    {
                        val intent = Intent(this@MainActivity, ConfigActivity::class.java)
                        startActivity(intent)
                    }
                    2->
                    {
                        alert(R.string.finalizar_aviso_title){
                            customView {
                                verticalLayout {
                                    textView {
                                        textResource = R.string.finalizar_aviso_msg
                                    }.lparams{
                                        marginStart = dip(25)
                                    }
                                }
                            }
                            yesButton {
                                //finalizar
                                val params = RequestParams()
                                client.post("http://",params,object: JsonHttpResponseHandler(){

                                })
                            }
                            noButton {
                                menu.setSelection(0)
                            }
                            onCancelled { menu.setSelection(0) }
                        }.show()
                    }
                }
            }

        }
        adapterListaProdutos = ArrayAdapter(this@MainActivity, R.layout.list_layout)
        todosProdutos = ArrayAdapter(this@MainActivity, R.layout.list_layout)
        val sqlite = AcessoSQLite(this@MainActivity)
        sqlite.use { select("produtos").exec {
            while(this.moveToNext())
            {
                todosProdutos.add(this.getString(1))
            }
            } }
            verticalLayout {
                val fab = FloatingActionButton(this@MainActivity)
                val imagemVazio = imageView {
                    imageResource = R.drawable.question_mark
                }
                val texto_vazio1 = textView {
                    textResource = R.string.lista_vazio
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                val texto_vazio2 = textView {
                    textResource = R.string.lista_vazio_msg
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                lista = listView {
                    adapter = adapterListaProdutos
                    visibility = View.GONE
                    onItemClick { _, view, _, _ ->
                        view as TextView
                        alert(R.string.contagem_dialog_title) {
                            customView {
                                verticalLayout {
                                    linearLayout {
                                        orientation = LinearLayout.HORIZONTAL
                                        textView {
                                            textResource = R.string.nome_produto_contado_label
                                        }
                                        textView {
                                            text = view.text
                                        }
                                    }
                                    linearLayout {
                                        orientation = LinearLayout.HORIZONTAL
                                        textView {
                                            textResource = R.string.qtd_produto_contado_label
                                        }
                                        textView {
                                            text = quantidadesProdutos[view.text.toString()].toString()
                                        }
                                    }
                                }
                            }
                            okButton {  }
                            negativeButton(R.string.del_btn){
                                alert(R.string.remover_aviso_title) {
                                    messageResource = R.string.remover_aviso_msg
                                    yesButton {
                                        adapterListaProdutos.remove(view.text.toString())
                                        adapterListaProdutos.notifyDataSetChanged()
                                        quantidadesProdutos.remove(view.text.toString())
                                    }
                                    noButton {  }
                                }.show()
                            }
                        }.show()
                    }
                }.lparams {
                    height = dip(400)
                    width = matchParent
                }.applyRecursively { view ->
                    view.layout(dip(5),dip(5),0,0)
                }
                addView(fab)
                val lp = LinearLayout.LayoutParams(dip(50), dip(50))
                lp.gravity = Gravity.END
                lp.topMargin = -dip(10) + dip(200)
                lp.rightMargin = dip(10)
                fab.layoutParams = lp
                fab.scaleType = ImageView.ScaleType.CENTER
                fab.image = getDrawable(R.drawable.ic_add_black_24dp)
                fab.size = FloatingActionButton.SIZE_NORMAL
                fab.onClick {
                    alert(R.string.dados_produto_dialog_title){
                        customView {
                            verticalLayout {
                                nomeProduto = autoCompleteTextView {
                                    setAdapter(todosProdutos)
                                    hintResource = R.string.nome_produto_hint
                                }
                                quantidade = editText{
                                    inputType = InputType.TYPE_CLASS_NUMBER
                                    hintResource = R.string.qtd_produto_hint
                                }
                                linearLayout {
                                    orientation = LinearLayout.HORIZONTAL
                                    textView {
                                        textResource = R.string.tirar_foto_label
                                    }.lparams {
                                        gravity = Gravity.CENTER_VERTICAL
                                    }
                                    imageButton {
                                        imageResource = R.drawable.ic_camera_alt_black_24dp
                                        onClick {
                                            alert(R.string.codigo_foto){
                                                customView {
                                                    verticalLayout {
                                                        val intentPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                                        if(intentPicture.resolveActivity(packageManager)!= null)
                                                        {
                                                            startActivityForResult(intentPicture, 1)
                                                        }
                                                        fotoTirada = imageView {}
                                                        textoCodigo = textView {}
                                                    }
                                                }
                                                okButton {  }
                                            }.show()
                                        }
                                    }.lparams{
                                        setMargins(dip(60),0,0,0)
                                        gravity = Gravity.END
                                    }
                                }

                            }
                        }
                        yesButton {
                            if(adapterListaProdutos.getPosition(nomeProduto.text.toString()) == -1)
                            {
                                if(todosProdutos.getPosition(nomeProduto.text.toString()) == -1)
                                {
                                    alert(R.string.prod_nao_cad_title)
                                    {
                                        messageResource = R.string.prod_nao_cad_msg
                                        yesButton {
                                            adapterListaProdutos.add(nomeProduto.text.toString())
                                            adapterListaProdutos.notifyDataSetChanged()
                                            quantidadesProdutos.put(nomeProduto.text.toString(),quantidade.text.toString().toInt())
                                        }
                                        noButton {  }
                                    }.show()
                                }
                                else
                                {
                                    adapterListaProdutos.add(nomeProduto.text.toString())
                                    adapterListaProdutos.notifyDataSetChanged()
                                    quantidadesProdutos.put(nomeProduto.text.toString(),quantidade.text.toString().toInt())
                                }
                            }
                            else
                            {
                                toast(R.string.produto_ja_add_aviso)
                            }
                            if(lista.visibility == View.GONE)
                            {
                                lista.visibility = View.VISIBLE
                                imagemVazio.visibility = View.GONE
                                texto_vazio1.visibility = View.GONE
                                texto_vazio2.visibility = View.GONE
                                lp.topMargin = -dip(10)
                                fab.layoutParams = lp
                            }
                        }
                        noButton {  }
                    }.show()
                }
            }
        }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when
        {
            requestCode == 1 && resultCode == Activity.RESULT_OK->
            {
                val bitmap = data!!.extras["data"] as Bitmap
                fotoTirada.imageBitmap = bitmap
                val frame = Frame.Builder().setBitmap(bitmap).build()
                val barcodes = DetectorCodigo(applicationContext).getQRCodeDetector().detect(frame)
                try
                {
                    val thiscode = barcodes.valueAt(0)
                    textoCodigo.text = thiscode.rawValue
                }catch (e: ArrayIndexOutOfBoundsException)
                {
                    e.printStackTrace()
                    toast(R.string.codigo_erro)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onResume() {
        menu.setSelection(0)
        super.onResume()
    }
}

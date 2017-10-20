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
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.widget.*
import com.google.android.gms.vision.Frame
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PopularSQLite(this@MainActivity)
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
                //val inputLayout = TextInputLayout(ui.ctx)
                listView {
                    adapter = adapterListaProdutos
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
                        }.show()
                    }
                }.lparams {
                    height = dip(400)
                    width = matchParent
                }.applyRecursively { view ->
                    view.layout(dip(5),dip(5),0,0)
                }
                button {
                    textResource = R.string.adicionar_btn
                    onClick {
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
                                    imageButton {
                                        imageResource = R.drawable.ic_camera_alt_black_24dp
                                        onClick {
                                            alert{
                                                customView {
                                                    verticalLayout {
                                                        val intentPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                                        if(intentPicture.resolveActivity(packageManager)!= null)
                                                        {
                                                            startActivityForResult(intentPicture, 1)
                                                        }
                                                        fotoTirada = imageView {

                                                        }

                                                        textoCodigo = textView {

                                                        }
                                                    }
                                                }
                                            }.show()
                                        }
                                    }
                                }
                            }
                            yesButton {
                                if(adapterListaProdutos.getPosition(nomeProduto.text.toString()) == -1)
                                {
                                    adapterListaProdutos.add(nomeProduto.text.toString())
                                    adapterListaProdutos.notifyDataSetChanged()
                                    quantidadesProdutos.put(nomeProduto.text.toString(),quantidade.text.toString().toInt())
                                }
                                else
                                {
                                    toast(R.string.produto_ja_add_aviso)
                                }
                            }
                            noButton {  }
                        }.show()
                    }
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
                    toast("QR não encontrado")
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

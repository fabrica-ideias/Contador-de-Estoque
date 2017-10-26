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
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.gms.vision.Frame
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import org.jetbrains.anko.*
import org.jetbrains.anko.db.select

class MainActivity : AppCompatActivity() {
    private lateinit var opcoesMenuAdapter : ArrayAdapter<String>
    private val client = AsyncHttpClient()
    private lateinit var menu : Spinner
    private lateinit var ui : MainActivityUI
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PopularSQLite(this@MainActivity)
        ui = MainActivityUI()
        ui.setContentView(this)
        val sqlite = AcessoSQLite(this@MainActivity)
        sqlite.use { select("produtos").exec {
            val listaProdutos = ArrayList<String>()
            while(this.moveToNext())
            {
                listaProdutos.add(this.getString(1))
            }
            ui.setTodosProdutos(listaProdutos)
        } }
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

        }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when
        {
            requestCode == 1 && resultCode == Activity.RESULT_OK->
            {
                val bitmap = data!!.extras["data"] as Bitmap
                ui.setFotoTirada(bitmap)
                val frame = Frame.Builder().setBitmap(bitmap).build()
                val barcodes = DetectorCodigo(applicationContext).getQRCodeDetector().detect(frame)
                try
                {
                    val thiscode = barcodes.valueAt(0)
                    ui.setValorDecodificado(thiscode.rawValue)
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
        PopularSQLite(this@MainActivity)
        menu.setSelection(0)
        super.onResume()
    }

    override fun onBackPressed() {
        alert{
            titleResource = R.string.confirm_sair_title
            messageResource = R.string.confirm_sair_msg
            yesButton {
                super.onBackPressed()
            }
            noButton {  }
        }.show()
    }
}

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

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.hugo.lessapedidos.GetScreenMetrics
import com.google.zxing.integration.android.IntentIntegrator
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import org.jetbrains.anko.*
import org.jetbrains.anko.db.select
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var opcoesMenuAdapter : ArrayAdapter<String>
    private val client = AsyncHttpClient()
    private lateinit var menu : Spinner
    private lateinit var ui : MainActivityUI
    private lateinit var prefs : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = MainActivityUI()
        ui.setContentView(this)
        val sincronizar = {
            PopularSQLite(this@MainActivity)
            val sqlite = AcessoSQLite(this@MainActivity)
            sqlite.use { select("produtos").exec {
                val listaProdutos = ArrayList<String>()
                val produtosUnidades = Hashtable<String,String>()
                while(this.moveToNext())
                {
                    listaProdutos.add(this.getString(1))
                    produtosUnidades.put(this.getString(1), this.getString(3))
                }
                ui.setTodosProdutos(listaProdutos)
                ui.setProdutosUnidades(produtosUnidades)
            } }
        }
        prefs = defaultSharedPreferences
        if(prefs.getString("ip_server","") == "")
        {
            alert {
                titleResource = R.string.conf_alerta
                messageResource = R.string.conf_alerta_msg
                okButton {
                    val intent = Intent(this@MainActivity, ConfigActivity::class.java)
                    startActivity(intent)
                }
            }.show()
        }
        else
            sincronizar()
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_action_bar)
        opcoesMenuAdapter = ArrayAdapter(this@MainActivity, R.layout.list_layout)
        menu = supportActionBar?.customView?.findViewById<View>(R.id.spinner) as Spinner
        opcoesMenuAdapter.add("")
        opcoesMenuAdapter.add("Configurações")
        opcoesMenuAdapter.add("Sincronizar")
        opcoesMenuAdapter.add("Finalizar")
        menu.adapter = opcoesMenuAdapter
        menu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
                        sincronizar()
                    }
                    3->
                    {
                        alert(R.string.finalizar_aviso_title){
                            customView {
                                verticalLayout {
                                    textView {
                                        textResource = R.string.finalizar_aviso_msg
                                        textSize = GetScreenMetrics(resources.displayMetrics.density).getFontSizeDialogText()
                                    }.lparams{
                                        marginStart = dip(25)
                                    }
                                }
                            }
                            yesButton {
                                val params = RequestParams()
                                client.post("http://${prefs.getString("ip_server","")}/contabilizar",params,object: JsonHttpResponseHandler(){

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
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        when
        {
            result != null ->
            {
                if(result.contents == null)
                {
                    toast("Cancelado")
                }
                else
                {
                    ui.setValorDecodificado(result.contents)
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

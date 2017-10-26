package com.example.hugo.contadordeestoque

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.jetbrains.anko.*
import org.jetbrains.anko.db.insert
import org.json.JSONArray
import org.json.JSONObject

/**
* Created by hugo on 19/10/17.
*/
class PopularSQLite(private val c: Context) {
    private val httpClient = AsyncHttpClient()
    private val sqlite = AcessoSQLite(c)
    private var prefs : SharedPreferences = c.defaultSharedPreferences
    init {
        httpClient.get("http://${prefs.getString("ip_server","")}:5000/produtos_estoque", object : JsonHttpResponseHandler(){
            val dialog = c.progressDialog("Aguarde") {}
            override fun onStart() {
                dialog.show()
                super.onStart()
            }
            override fun onProgress(bytesWritten: Long, totalSize: Long) {
                dialog.max = totalSize.toInt()
                dialog.progress = bytesWritten.toInt()
                super.onProgress(bytesWritten, totalSize)
            }
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                Log.d("resposta","json object")
                dialog.dismiss()
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>, timeline: JSONArray) {
                dialog.dismiss()
                val dbdialog = c.indeterminateProgressDialog("Aguarde") {
                    setMessage("Populando Banco Interno")
                }
                dbdialog.show()
                doAsync {
                    for(i in 0 until timeline.length())
                    {
                        val codigo = timeline.getJSONObject(i).getString("PRO_PKN_CODIGO")[0].toInt()
                        val nome = timeline.getJSONObject(i).getString("PRO_A_DESCRICAO_REDUZIDA")
                        val quantidade = timeline.getJSONObject(i).getDouble("EST_N_FISCAL_SALDO_01")
                        sqlite.use { insert("produtos", "id" to codigo, "nome" to nome, "qtd" to quantidade) }
                    }
                }
                dbdialog.dismiss()
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                dialog.dismiss()
                c.alert(R.string.con_err_aviso_title) {
                    messageResource = R.string.con_err_aviso
                    okButton {  }
                }.show()
                super.onFailure(statusCode, headers, throwable, errorResponse)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONArray?) {
                dialog.dismiss()
                c.alert(R.string.con_err_aviso_title) {
                    messageResource = R.string.con_err_aviso
                    okButton {  }
                }.show()
                super.onFailure(statusCode, headers, throwable, errorResponse)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                dialog.dismiss()
                c.alert(R.string.con_err_aviso_title) {
                    messageResource = R.string.con_err_aviso
                    okButton {  }
                }.show()
                super.onFailure(statusCode, headers, responseString, throwable)
            }
        })
    }
}
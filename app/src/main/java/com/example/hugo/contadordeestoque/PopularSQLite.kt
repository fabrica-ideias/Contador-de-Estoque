package com.example.hugo.contadordeestoque

import android.content.Context
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.jetbrains.anko.db.insert
import org.json.JSONArray
import org.json.JSONObject

/**
* Created by hugo on 19/10/17.
*/
class PopularSQLite(private val c: Context) {
    private val http_client = AsyncHttpClient()
    private val sqlite = AcessoSQLite(c)
    init {
        http_client.get("http://192.168.15.7:5000/produtos_estoque", object : JsonHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                Log.d("resposta","json object")
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>, timeline: JSONArray) {
              for(i in 0 until timeline.length())
              {
                  val codigo = timeline.getJSONObject(i).getString("PRO_PKN_CODIGO")[0].toInt()
                  val nome = timeline.getJSONObject(i).getString("PRO_A_DESCRICAO_REDUZIDA")
                  val quantidade = timeline.getJSONObject(i).getDouble("EST_N_FISCAL_SALDO_01")
                  sqlite.use { insert("produtos", "id" to codigo, "nome" to nome, "qtd" to quantidade) }
              }
            }
        })
    }
}
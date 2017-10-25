package com.example.hugo.contadordeestoque

import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuItem
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class ConfigActivity : AppCompatActivity() {
    private lateinit var prefs : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = defaultSharedPreferences
        editor = prefs.edit()
        val textInput = TextInputLayout(this@ConfigActivity)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        verticalLayout {
            linearLayout {
                orientation = LinearLayout.VERTICAL
                val ip = editText {
                    hintResource = R.string.ip_server_hint
                    setText(prefs.getString("ip_server",""))
                    setSelection(text.length)
                }.lparams{
                    width = resources.displayMetrics.widthPixels
                    setPadding(0,dip(10),0,0)
                }
                removeView(ip)
                textInput.addView(ip)
                addView(textInput)
                button {
                    textResource = R.string.salvar_cfg_btn
                    onClick {
                        editor.putString("ip_server",ip.text.toString())
                        editor.commit()
                        finish()
                    }
                }.lparams{
                    setMargins(dip(50),dip(340), dip(50),0)
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}

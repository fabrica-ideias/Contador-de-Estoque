package com.example.hugo.contadordeestoque

import android.content.SharedPreferences
import android.support.design.widget.TextInputLayout
import android.view.Gravity
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
* Created by hugo on 26/10/17.
*/
class ConfigActivityUI : AnkoComponent<ConfigActivity> {
    private lateinit var prefs : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    override fun createView(ui: AnkoContext<ConfigActivity>) = ui.apply {
        val textInput = TextInputLayout(ui.ctx)
        prefs = ui.ctx.defaultSharedPreferences
        editor = prefs.edit()
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
                        editor.apply()
                        ui.owner.finish()
                    }
                }.lparams{
                    setMargins(dip(50),dip(340), dip(50),0)
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            }
        }
    }.view
}
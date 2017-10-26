package com.example.hugo.contadordeestoque

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import org.jetbrains.anko.setContentView

class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ui = ConfigActivityUI()
        ui.setContentView(this)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}

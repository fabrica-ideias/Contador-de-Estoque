package com.example.hugo.contadordeestoque

import android.content.Context
import android.content.res.Configuration
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import org.jetbrains.anko.dip
/**
* Created by hugo on 31/10/17.
*/
class PosicionarObjeto(private val view: View, context: Context, screenMetrics: Float) {
    val obterLayoutParams = {
        val lp = view.layoutParams as LinearLayout.LayoutParams
        when(view)
        {
            is Button->
            {

            }
            is FloatingActionButton->
            {
                lp.gravity = Gravity.END
                when(screenMetrics)
                {
                    0.75f->  //ldpi
                    {

                    }
                    1f->    //mdpi
                    {

                    }
                    1.5f->  //hdpi
                    {
                        val screenSize = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
                        Log.d("screen size", screenSize.toString())
                        when(screenSize)
                        {
                            Configuration.SCREENLAYOUT_SIZE_NORMAL->
                            {
                                Log.d("tamanho", "normal")
                                lp.topMargin = -context.dip(10) + context.dip(200)
                                lp.rightMargin = context.dip(10)
                            }
                            Configuration.SCREENLAYOUT_SIZE_LARGE ->
                            {
                                Log.d("tamanho", "grande")
                                lp.topMargin = -context.dip(10) + context.dip(200)
                                lp.rightMargin = context.dip(10)
                            }
                            Configuration.SCREENLAYOUT_SIZE_SMALL->
                            {
                                Log.d("tamanho", "pequeno")
                                lp.topMargin = -context.dip(10) + context.dip(200)
                                lp.rightMargin = context.dip(10)
                            }
                        }
                    }
                    2f->    //xhdpi
                    {

                    }
                    3f->    //xxhdpi
                    {

                    }
                    4f->    //xxxhdpi
                    {

                    }
                    else->
                    {

                    }
                }
            }
        }
        lp
    }
}
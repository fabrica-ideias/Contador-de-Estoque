package com.example.hugo.lessapedidos

import android.util.Log

/**
* Created by hugo on 05/10/17.
*/
class GetScreenMetrics(private val metrics: Float) {
    val getFontSizeDialogTitle = {
        when(metrics)
        {
            0.75f->  //ldpi
            {
                12f
            }
            1f->    //mdpi
            {
                16f
            }
            1.5f->  //hdpi
            {
                18f
            }
            2f->    //xhdpi
            {
                24f
            }
            3f->    //xxhdpi
            {
                28f
            }
            4f->    //xxxhdpi
            {
                34f
            }
            else->
                14f
        }
    }

    val getFontSizeDialogText = {
        when(metrics)
        {
            0.75f->  //ldpi
            {
                8f
            }
            1f->    //mdpi
            {
                12f
            }
            1.5f->  //hdpi
            {
                14f
            }
            2f->    //xhdpi
            {
                18f
            }
            3f->    //xxhdpi
            {
                24f
            }
            4f->    //xxxhdpi
            {
                30f
            }
            else->
                6f
        }
    }

    val getLogoSize = {
        Log.d("metrics", metrics.toString())
        when(metrics)
        {
            0.75f->  //ldpi
            {
                38
            }
            1f->    //mdpi
            {
                75
            }
            1.5f->  //hdpi
            {
                150
            }
            2f->    //xhdpi
            {
                225
            }
            3f->    //xxhdpi
            {
                338
            }
            4f->    //xxxhdpi
            {
                507
            }
            else->
                0
        }
    }
    }
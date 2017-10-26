package com.example.hugo.lessapedidos

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
        when(metrics)
        {
            0.75f->  //ldpi
            {
                127
            }
            1f->    //mdpi
            {
                169
            }
            1.5f->  //hdpi
            {
                225
            }
            2f->    //xhdpi
            {
                282
            }
            3f->    //xxhdpi
            {
                352
            }
            4f->    //xxxhdpi
            {
                440
            }
            else->
                0
        }
    }
    }
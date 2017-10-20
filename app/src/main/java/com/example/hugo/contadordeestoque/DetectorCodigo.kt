package com.example.hugo.contadordeestoque

import android.content.Context
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

/**
* Created by hugo on 20/10/17.
*/
class DetectorCodigo(private val c: Context) {
    val getQRCodeDetector = {
        BarcodeDetector.Builder(c).setBarcodeFormats(Barcode.ALL_FORMATS).build()
    }
}
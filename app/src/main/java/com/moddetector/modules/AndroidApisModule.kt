package com.moddetector.modules

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore

class AndroidApisModule(val context: Context) {
    private val CAM_FILE_PATH = context.cacheDir.absolutePath + "/camImg"

    fun cropBitmap(uri: Uri?, x: Int, y: Int, width:Int, height: Int) :Bitmap {
        val originalBitmap =  if(uri != null)MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        else BitmapFactory.decodeFile(CAM_FILE_PATH)

        return   Bitmap.createBitmap(
            originalBitmap,
            x,
            y,
            width,
            height)
    }
}
package com.moddetector.modules.azure

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.moddetector.data.Face
import com.moddetector.helpers.RetrofitUtils
import com.moddetector.ui.mod.ScreenState
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


class AINetworkModule @Inject constructor(val context: Context) {
    private val AZURE_BASE_URL = "https://westcentralus.api.cognitive.microsoft.com/"
    // private val AZURE_BASE_URL = "http://10.0.2.2:5000/"
    private val KEY_1 = ENTER YOUR KEY HERE
    private val faceAttributes = "headPose,smile,emotion"
    private var apis: AzureApis
    var detectionState: ScreenState = ScreenState.Loading()
    var imageUUID: String? = null

    init {
        val rerofit = RetrofitUtils.buildRetrofitClient(AZURE_BASE_URL, context)
        apis = rerofit.create(AzureApis::class.java)
    }

    fun detectFaces(uri: Uri?): Single<List<Face>> {

        val file = if (uri != null) File(getPath(uri)) else File(context.cacheDir, "camImg")

        val requestBody = RequestBody
            .create(MediaType.parse("application/octet-stream"), file)

        return apis.getFaceDetailss(KEY_1, true, true, faceAttributes, requestBody)

    }

    private fun getPath(uri: Uri): String? {
        val filePath = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor = context.contentResolver.query(
            uri, filePath,
            null, null, null
        )
        c.moveToFirst()
        val columnIndex = c.getColumnIndex(filePath[0])
        val path = c.getString(columnIndex)
        c.close()
        return path
    }


}
package com.moddetector.modules.azure

import com.moddetector.data.Face
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AzureApis {

    //@Headers("Content-Type", "application/json")
    @POST("/face/v1.0/detect")
    fun getFaceDetailss(@Header("Ocp-Apim-Subscription-Key") key: String,
                        @Query("returnFaceId") isReturnFaceId: Boolean?,
                        @Query("returnFaceLandmarks")isReturnFaceLandmarks: Boolean,
                        @Query("returnFaceAttributes")faceAttributes: String,
                        @Body image: RequestBody
    ): Single<List<Face>>
}
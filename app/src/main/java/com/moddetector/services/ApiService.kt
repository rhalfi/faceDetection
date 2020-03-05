package com.moddetector.services

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.moddetector.data.Face
import com.moddetector.modules.AndroidApisModule
import com.moddetector.modules.azure.AINetworkModule
import com.moddetector.ui.mod.ModFragment
import com.moddetector.ui.mod.ScreenState
import dagger.android.AndroidInjection
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

const val BROADCAST_ACTION = "com.moddetector.services.FACE_RESULTS"


class ApiService(name: String) : IntentService(name) {
    constructor() : this("api service") {
    }


    companion object {
        val TAG = ApiService::class.java.simpleName
    }

    @Inject
    lateinit var aiNetworkModule: AINetworkModule

    @Inject
    lateinit var androidApisModule: AndroidApisModule

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        val uriString = intent?.getStringExtra(ModFragment.IMG_URI_KEY)
        val uri = if (!TextUtils.isEmpty(uriString)) Uri.parse(uriString) else null
        val imageUUID = intent?.getStringExtra(ModFragment.IMG_UUID) ?: ""
        aiNetworkModule.detectionState = ScreenState.Loading()
        notifyServiceState(imageUUID)
        aiNetworkModule.detectFaces(uri).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
            .subscribe(object : SingleObserver<List<Face>> {
                override fun onSuccess(faces: List<Face>) {
                    Log.d(TAG, "recived response ${faces.size}")
                    if (faces == null || faces.isEmpty()) {

                        aiNetworkModule.detectionState = ScreenState.ErrorNoFaceFound()
                        notifyServiceState(imageUUID)
                        return
                    }
                    val croppedBmp = androidApisModule.cropBitmap(
                        uri,
                        faces[0].faceRectangle.left,
                        faces[0].faceRectangle.top,
                        faces[0].faceRectangle.width,
                        faces[0].faceRectangle.height
                    )

                    aiNetworkModule.detectionState =
                        ScreenState.FaceDetectionReady(croppedBmp, faces[0])
                    notifyServiceState(imageUUID)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "getFaceDetailss error ${e.localizedMessage}")
                    aiNetworkModule.detectionState = ScreenState.ErrorInAPI()
                    notifyServiceState(imageUUID)
                }
            })
    }

    fun notifyServiceState(imageUUID: String) {
        aiNetworkModule.imageUUID = imageUUID
        val localIntent = Intent(BROADCAST_ACTION)
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
    }
}
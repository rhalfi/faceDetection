package com.moddetector.ui.mod

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.moddetector.data.Face
import com.moddetector.modules.AndroidApisModule
import com.moddetector.modules.azure.AINetworkModule
import com.moddetector.mvvm.BaseViewModel
import com.moddetector.mvvm.IView
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class ModViewModel @Inject constructor(private val aiNetworkModule: AINetworkModule
                                       , private val androidApisModule: AndroidApisModule) : BaseViewModel<IView>() {
    companion object{
        private val TAG = ModViewModel::class.java.simpleName
    }

    val screenState: LiveData<ScreenState>
        get() = _screenState
    private val _screenState: MutableLiveData<ScreenState> = MutableLiveData()

    fun updateScreenState(imageId: String) {
        if(imageId.equals(aiNetworkModule.imageUUID)) {
            _screenState.value = aiNetworkModule.detectionState
        }
    }
    fun checkImageMod(uri:Uri?) {
        aiNetworkModule.detectFaces(uri)  .subscribeOn(Schedulers.io()).
            observeOn(Schedulers.io()).
            subscribe(object: SingleObserver<List<Face>> {
                override fun onSuccess(faces: List<Face>) {
                    Log.d(TAG, "recived response ${faces.size}")
                    if(faces == null || faces.isEmpty()) {
                        _screenState.postValue(ScreenState.ErrorNoFaceFound())
                        return
                    }
                    val croppedBmp = androidApisModule.cropBitmap(
                        uri,
                        faces[0].faceRectangle.left,
                        faces[0].faceRectangle.top,
                        faces[0].faceRectangle.width,
                        faces[0].faceRectangle.height)

                    _screenState.postValue(ScreenState.FaceDetectionReady(croppedBmp, faces[0]))

                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "getFaceDetailss error ${e.localizedMessage}")
                    _screenState.postValue((ScreenState.ErrorInAPI()))
                }
            })
    }
}
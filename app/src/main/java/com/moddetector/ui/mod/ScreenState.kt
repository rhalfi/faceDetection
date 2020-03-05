package com.moddetector.ui.mod

import android.graphics.Bitmap
import com.moddetector.data.Face

sealed class ScreenState {
    class ErrorInAPI : ScreenState()
    class ErrorNoFaceFound : ScreenState()
    class Loading : ScreenState()
    data class FaceDetectionReady(val bitmap : Bitmap, val face: Face) : ScreenState()
}
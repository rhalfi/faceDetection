package com.moddetector.data

import com.moddetector.R

class Emotion {
    var contempt: Double = 0.0
    var surprise = 0.0
    var happiness: Double = 0.0
    var neutral: Double = 0.0
    var sadness: Double = 0.0
    var disgust: Double = 0.0
    var anger: Double = 0.0
    var fear: Double = 0.0

    fun getTopEmotionStringID(): Int {
        var max = contempt
        var stringID = R.string.contempt

        if (surprise > max) {
            stringID = R.string.surprise
            max = surprise
        }
        if (happiness > max) {
            stringID = R.string.happiness
            max = happiness
        }
        if (neutral > max) {
            stringID = R.string.neutral
            max = neutral
        }
        if (sadness > max) {
            stringID = R.string.sadness
            max = sadness
        }
        if (disgust > max) {
            stringID = R.string.disgust
            max = disgust
        }
        if (anger > max) {
            stringID = R.string.anger
            max = anger
        }
        if (fear > max) {
            stringID = R.string.fear
        }

        return stringID

    }

}
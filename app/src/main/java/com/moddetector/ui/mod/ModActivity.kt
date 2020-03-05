package com.moddetector.ui.mod

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moddetector.R

class ModActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            val uriString = intent.extras?.getString(ModFragment.IMG_URI_KEY)
            val uri: Uri = Uri.parse(uriString)

            val uuid = intent.extras?.getString(ModFragment.IMG_UUID)?: ""
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ModFragment.newInstance(uri, uuid))
                .commitNow()
        }
    }

}
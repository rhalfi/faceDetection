package com.moddetector.ui.mod

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.moddetector.R
import com.moddetector.data.Face
import com.moddetector.mvvm.IViewFragment
import com.moddetector.services.ApiService
import com.moddetector.services.BROADCAST_ACTION
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class ModFragment : IViewFragment<ModViewModel>() {
    companion object {
        val TAG = ModFragment::class.java.simpleName
        fun newInstance(uri: Uri, uuid: String): Fragment {
            val fragment = ModFragment()
            val bundle = Bundle()
            bundle.putString(IMG_URI_KEY, uri.toString())
            bundle.putString(IMG_UUID, uuid)
            fragment.arguments = bundle
            return fragment
        }

        const val IMG_URI_KEY = "IMG_URI_KEY"
        const val IMG_UUID = "IMG_UUID"
        const val NO_FACES_FOUND = 1001
    }

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    lateinit var progressBar: ProgressBar
    lateinit var resultImage: ImageView
    lateinit var emotionTextView: TextView
    lateinit var modMainLayout: LinearLayout
    lateinit var errorLayout: LinearLayout

    lateinit var imageUUID: String
    lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        registerBroadcastReciver()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        imageUUID = arguments?.getString(IMG_UUID) ?: ""
        val root = inflater.inflate(R.layout.fragment_mod, container, false)
        initViews(root)
        initUpdatesFromViewModel()
        return root
    }

    private fun registerBroadcastReciver() {
        var statusIntentFilter = IntentFilter(BROADCAST_ACTION)
        broadcastReceiver = FacesStateReceiver()
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, statusIntentFilter)
    }

    private fun initUpdatesFromViewModel() {
        viewModel.screenState.observe(viewLifecycleOwner, Observer<ScreenState> {
            progressBar.visibility = View.GONE
            when (it) {
                is ScreenState.FaceDetectionReady -> updateUiWithResult(it.bitmap, it.face)
                is ScreenState.ErrorInAPI -> showErrorLayout()
                is ScreenState.ErrorNoFaceFound -> noFacesFound()
                is ScreenState.Loading -> loading()
            }
        })
    }

    private fun loading() {
        progressBar.visibility = View.VISIBLE
        modMainLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
    }

    private fun noFacesFound() {
        requireActivity().setResult(NO_FACES_FOUND)
        requireActivity().finish()
    }

    private fun showErrorLayout() {
        errorLayout.visibility = View.VISIBLE
    }

    private fun updateUiWithResult(bitmap: Bitmap, face: Face) {
        modMainLayout.visibility = View.VISIBLE
        emotionTextView.text = getString(face.faceAttributes.emotion.getTopEmotionStringID())
        resultImage.setImageBitmap(bitmap)
    }

    private fun initViews(root: View) {
        progressBar = root.findViewById(R.id.progressBar)
        emotionTextView = root.findViewById(R.id.modDescription)
        resultImage = root.findViewById(R.id.modImage)
        modMainLayout = root.findViewById(R.id.modMainLayout)
        modMainLayout.visibility = View.GONE
        errorLayout = root.findViewById(R.id.errorLayout)
        root.findViewById<Button>(R.id.retryButton).setOnClickListener {
            progressBar.visibility = View.VISIBLE
            errorLayout.visibility = View.GONE
            val serviceIntent = Intent(requireContext(), ApiService::class.java)
            serviceIntent.putExtra(IMG_UUID, imageUUID)
            requireActivity().startService(serviceIntent)
        }
    }

    override fun onCreateViewModel() = ModViewModel::class.java
    override fun getViewModelFactory() = mViewModelFactory

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(IMG_UUID, imageUUID)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        imageUUID = savedInstanceState?.getString(IMG_UUID) ?: ""
        viewModel.updateScreenState(imageUUID)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    inner class FacesStateReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "onReceive face results")
            viewModel?.updateScreenState(imageUUID)
        }
    }
}


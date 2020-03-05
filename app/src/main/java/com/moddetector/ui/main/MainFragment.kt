package com.moddetector.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.moddetector.R
import com.moddetector.services.ApiService
import com.moddetector.ui.mod.ModActivity
import com.moddetector.ui.mod.ModFragment.Companion.IMG_URI_KEY
import com.moddetector.ui.mod.ModFragment.Companion.IMG_UUID
import com.moddetector.ui.mod.ModFragment.Companion.NO_FACES_FOUND
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class MainFragment : Fragment() {
    private val IS_IMAGE_SELECTED = "IS_IMAGE_SELECTED"
    private val LOAD_IMAGE_FROM_GALLERY = 1
    private val LOAD_IMAGE_FROM_CAM = 2
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGES = 10
    private val MY_PERMISSIONS_REQUEST_CAMERA = 20
    private val START_DETECT = 10
    private val FILE_NAME = "camImg"

    lateinit var previewImage: ImageView
    lateinit var checkModeButton: Button
    var selectedImageUri: Uri? = null
    var isImageReady = false

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.main_fragment, container, false)
        initViews(root)
        return root
    }

    private fun initViews(root: View) {
        root.findViewById<Button>(R.id.loadImageBtn).setOnClickListener {
            choosePhotoFromGallaryIfHasPermission()
        }
        root.findViewById<Button>(R.id.openCamBtn).setOnClickListener {
            takePhotoFromCameraIfHasPermission()
        }

        checkModeButton = root.findViewById(R.id.checkModBtn)
        checkModeButton.visibility = View.GONE
        checkModeButton.setOnClickListener {
            if (selectedImageUri != null || isImageReady) {
                val intent = Intent(requireContext(), ModActivity::class.java)
                val uuid = UUID.randomUUID()
                intent.putExtra(IMG_URI_KEY, selectedImageUri?.toString() ?: "")
                intent.putExtra(IMG_UUID, uuid)
                startActivityForResult(intent, START_DETECT)

                val serviceIntent = Intent(requireContext(), ApiService::class.java)
                serviceIntent.putExtra(IMG_URI_KEY, selectedImageUri?.toString() ?: "")
                serviceIntent.putExtra(IMG_UUID, uuid)
                requireActivity().startService(serviceIntent)


            }
        }
        previewImage = root.findViewById(R.id.imagePreview)
    }


    private fun choosePhotoFromGallaryIfHasPermission() {
        if (!hasReadStoragePermission()) return
        chosePhoetoFromGalery()
    }

    private fun chosePhoetoFromGalery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, LOAD_IMAGE_FROM_GALLERY)
    }

    private fun hasReadStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGES
            )
            return false
        }
        return true
    }

    private fun hasReadCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA
            )
            return false
        }
        return true
    }

    private fun takePhotoFromCameraIfHasPermission() {
        if (!hasReadCameraPermission()) return

        takePhotoFromCamera()
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, LOAD_IMAGE_FROM_CAM)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    takePhotoFromCamera()
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(activity, R.string.permissionNotGranted, Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }

            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGES -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    chosePhoetoFromGalery()
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(activity, R.string.permissionNotGranted, Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }


            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == LOAD_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK -> {
                if (data != null) {
                    val uri = data.data
                    try {
                        showCheckModeButton()
                        Glide.with(this)
                            .asBitmap()
                            .load(uri)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    previewImage.setImageBitmap(resource)
                                    saveBitmapToFile(resource)
                                    isImageReady = true
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                }
                            })

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Failed!", Toast.LENGTH_SHORT).show()
                    }

                }

            }
            requestCode == LOAD_IMAGE_FROM_CAM && resultCode == Activity.RESULT_OK && data != null -> {
                showCheckModeButton()
                isImageReady = true
                val bitmap = data.extras.get("data") as Bitmap
                previewImage.setImageBitmap(bitmap)
                saveBitmapToFile(bitmap)
            }
            requestCode == START_DETECT && resultCode == NO_FACES_FOUND -> {
                Toast.makeText(context, R.string.noFacesFound, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCheckModeButton() {
        checkModeButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
        checkModeButton.visibility = View.VISIBLE
    }

    private fun saveBitmapToFile(bitmap: Bitmap) {
        val f = File(requireContext().cacheDir, FILE_NAME)
        f.createNewFile();

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos)
        val bitmapData = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        Log.d("test", "fileuri=${f.absolutePath}")

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_IMAGE_SELECTED, isImageReady)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        isImageReady = savedInstanceState?.getBoolean(IS_IMAGE_SELECTED) ?: false

        if (isImageReady) {
            showCheckModeButton()
            Glide.with(this)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .load(File(requireContext().cacheDir, FILE_NAME).absoluteFile)
                .into(previewImage)
        }
    }


}

package com.moddetector.mvvm

import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

abstract class IViewDialogFragment<VM: BaseViewModel<IView>> : DialogFragment(), IView {
    lateinit var mViewModel: VM

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewModel = ViewModelProviders.of(this, getViewModelFactory())[onCreateViewModel()!!]
    }

    override fun onStart() {
        super.onStart()
        mViewModel.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mViewModel.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected open fun getViewModelFactory(): ViewModelProvider.Factory? {
        // To provide custom ViewModel factory, this method should be overridden.
        // It is needed in case you want to inject to the VM.
        return null
    }


    protected open fun getViewModel(): VM? {
        return mViewModel
    }

    protected abstract fun onCreateViewModel(): Class<VM>?
}
package com.moddetector.mvvm;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public abstract class IViewFragment<VM extends BaseViewModel> extends Fragment implements IView {
    private VM mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, getViewModelFactory()).get(onCreateViewModel());
    }

    @Override
    public void onStart() {
        super.onStart();
        //noinspection unchecked
        mViewModel.attachView(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel = null;
    }

    protected ViewModelProvider.Factory getViewModelFactory() {
        // To provide custom ViewModel factory, this method should be overridden.
        // It is needed in case you want to inject to the VM.
        return null;
    }


    protected VM getViewModel(){
        return mViewModel;
    }

    abstract protected Class<VM> onCreateViewModel();
}

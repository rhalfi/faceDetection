package com.moddetector.mvvm;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public abstract class IViewActivity<VM extends BaseViewModel> extends AppCompatActivity {
    private VM mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, getViewModelFactory()).get(onCreateViewModel());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //noinspection unchecked
        mViewModel.attachView((IView) this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.detachView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel = null;
    }

    @SuppressWarnings("unused")
    protected VM getViewModel(){
        return mViewModel;
    }

    abstract protected Class<VM> onCreateViewModel();

    // To provide custom ViewModel factory, this method should be overridden.
    // It is needed in case you want to inject to the VM.
    abstract protected ViewModelProvider.Factory getViewModelFactory();
}

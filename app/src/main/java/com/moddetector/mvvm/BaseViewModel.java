package com.moddetector.mvvm;

import androidx.lifecycle.ViewModel;

public class BaseViewModel<IV extends IView> extends ViewModel {
    private boolean mActive = true;
    private IV mView;


    public void attachView(IV view) {
        mView = view;
        onViewAttached();
    }

    public void detachView() {
        mView = null;
        onViewDetached();
    }

    @SuppressWarnings("WeakerAccess")
    protected void onViewAttached() {

    }

    @SuppressWarnings("WeakerAccess")
    protected void onViewDetached() {

    }

    protected boolean isAttachedToView() {
        return mView != null;
    }

    protected IV getView() {
        return mView;
    }

    protected boolean isActive() {
        return mActive;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mActive = false;
    }
}

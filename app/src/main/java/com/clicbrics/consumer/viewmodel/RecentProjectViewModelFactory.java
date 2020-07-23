package com.clicbrics.consumer.viewmodel;

import android.arch.lifecycle.ViewModelProvider;

import com.clicbrics.consumer.helper.RecentProjectResultCallback;

/**
 * Created by Alok on 30-07-2018.
 */
public class RecentProjectViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private RecentProjectResultCallback mResultCallback;

    public RecentProjectViewModelFactory(RecentProjectResultCallback mResultCallback) {
        this.mResultCallback = mResultCallback;
    }
}

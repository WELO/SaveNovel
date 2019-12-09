package com.welo.savenovel.presentation;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Amy on 2019-08-14
 */

public class MainApplication extends Application {
    public MainApplication() {

        Timber.plant(new Timber.DebugTree());

    }
}

package com.welo.savenovel.presentation

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Created by Amy on 2019-08-14
 */
class MainApplication : Application() {
    init {
        Timber.plant(DebugTree())
    }
}
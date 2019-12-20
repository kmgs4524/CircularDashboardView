package com.york.customview

import android.app.Application
import timber.log.Timber

/**
 * @author MP_User
 * created on 2019/12/20
 */
class CustomViewApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
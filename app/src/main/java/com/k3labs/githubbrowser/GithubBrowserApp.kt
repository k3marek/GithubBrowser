package com.k3labs.githubbrowser

import android.app.Activity
import androidx.multidex.MultiDexApplication
import com.k3labs.githubbrowser.db.GithubBrowserDb
import com.k3labs.githubbrowser.di.AppComponent
import com.k3labs.githubbrowser.di.AppInjector
import com.squareup.moshi.Moshi
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject


class GithubBrowserApp : MultiDexApplication(), HasActivityInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        appComponent = AppInjector.init(this)
    }

    override fun activityInjector() = dispatchingAndroidInjector

    fun getDb(): GithubBrowserDb {
        return appComponent.getDb()
    }

    fun getMoshi(): Moshi {
        return appComponent.getMoshi()
    }

    companion object {
        internal lateinit var appComponent: AppComponent
    }
}

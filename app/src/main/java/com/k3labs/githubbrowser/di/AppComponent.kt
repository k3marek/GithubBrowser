package com.k3labs.githubbrowser.di

import android.app.Application
import com.k3labs.githubbrowser.GithubBrowserApp
import com.k3labs.githubbrowser.db.GithubBrowserDb
import com.k3labs.githubbrowser.db.MyTypeConverters
import com.squareup.moshi.Moshi
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        MainActivityModule::class]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(githubApp: GithubBrowserApp)

    fun inject(githubApp: MyTypeConverters)

    //exposed components

    fun getDb(): GithubBrowserDb

    fun getMoshi(): Moshi
}


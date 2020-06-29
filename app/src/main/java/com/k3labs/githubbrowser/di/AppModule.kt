package com.k3labs.githubbrowser.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.k3labs.githubbrowser.api.GithubService
import com.k3labs.githubbrowser.db.*
import com.k3labs.githubbrowser.util.DATABASE_NAME
import com.k3labs.githubbrowser.api.calladapter.LiveDataCallAdapterFactory
import com.k3labs.githubbrowser.api.calladapter.NetworkResponseAdapterFactory
import com.k3labs.githubbrowser.workers.SeedDatabaseWorker
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ViewModelBindingModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideContext(app: Application): Context = app.applicationContext


    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
//            .add(MoshiJsonListAdapersFactory())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val httpTimeout: Long = 30

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .connectTimeout(httpTimeout, TimeUnit.SECONDS)
            .readTimeout(httpTimeout, TimeUnit.SECONDS)
            .writeTimeout(httpTimeout, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.github.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideGithubService(retrofit: Retrofit): GithubService =
        retrofit.create(GithubService::class.java)

    @Singleton
    @Provides
    fun provideDb(app: Application): GithubBrowserDb {
        return Room
            .databaseBuilder(app, GithubBrowserDb::class.java, DATABASE_NAME)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                    WorkManager.getInstance(app.applicationContext).enqueue(request)
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: GithubBrowserDb): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: GithubBrowserDb): RepoDao {
        return db.repoDao()
    }

    @Singleton
    @Provides
    fun provideRepoSearchResultDao(db: GithubBrowserDb): RepoSearchResultDao {
        return db.repoSearchResultDao()
    }

    @Singleton
    @Provides
    fun provideFavReposDao(db: GithubBrowserDb): FavReposDao {
        return db.favReposDao()
    }
}

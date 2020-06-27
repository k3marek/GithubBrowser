package com.k3labs.githubbrowser.workers

import android.content.Context
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.k3labs.githubbrowser.db.GithubBrowserDb
import com.k3labs.githubbrowser.di.AssistedCoroutineWorkerFactory
import com.k3labs.githubbrowser.vo.User
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.coroutineScope
import okio.Okio
import timber.log.Timber

const val USER_FILE = "user.json"

class SeedDatabaseWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    val database: GithubBrowserDb,
    val moshi: Moshi
) : CoroutineWorker(context, workerParameters) {

    private val TAG by lazy { SeedDatabaseWorker::class.java.simpleName }

    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(USER_FILE).use { inputStream ->
                JsonReader.of(Okio.buffer(Okio.source(inputStream))).use { jsonReader ->
                    val listType =
                        Types.newParameterizedType(List::class.java, User::class.java)
                    val adapter: JsonAdapter<List<User>> = moshi.adapter(listType)
                    val users = adapter.fromJson(jsonReader)
                    database.withTransaction {
                        users?.forEach {
                            database.userDao().insert(it)
                        }
                    }
                }
            }
            Result.success()

        } catch (e: Exception) {
            Timber.e(e)
            Result.failure()
        }
    }

    @AssistedInject.Factory
    interface Factory : AssistedCoroutineWorkerFactory
}
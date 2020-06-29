package com.k3labs.githubbrowser.workers

import android.content.Context
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.k3labs.githubbrowser.GithubBrowserApp
import com.k3labs.githubbrowser.vo.User
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Types
import kotlinx.coroutines.coroutineScope
import okio.Okio
import timber.log.Timber

const val USER_FILE = "user.json"

class SeedDatabaseWorker constructor(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val TAG by lazy { SeedDatabaseWorker::class.java.simpleName }

    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(USER_FILE).use { inputStream ->
                JsonReader.of(Okio.buffer(Okio.source(inputStream))).use { jsonReader ->
                    val moshi = (applicationContext as GithubBrowserApp).getMoshi()
                    val db = (applicationContext as GithubBrowserApp).getDb()

                    val listType =
                        Types.newParameterizedType(List::class.java, User::class.java)
                    val adapter: JsonAdapter<List<User>> = moshi.adapter(listType)
                    val users = adapter.fromJson(jsonReader)
                    db.withTransaction {
                        users?.forEach {
                            db.userDao().insert(it)
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
}
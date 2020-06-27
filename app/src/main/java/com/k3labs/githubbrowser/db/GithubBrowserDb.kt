package com.k3labs.githubbrowser.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.k3labs.githubbrowser.util.DATABASE_NAME
import com.k3labs.githubbrowser.vo.*
import com.k3labs.githubbrowser.workers.SeedDatabaseWorker

/**
 * Main database description.
 */
@Database(
    entities = [
        Repo::class,
        FavoriteRepo::class,
        User::class,
        Contributor::class,
        RepoSearchResult::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(MyTypeConverters::class)
abstract class GithubBrowserDb : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun favReposDao(): FavReposDao
    abstract fun userDao(): UserDao

    companion object {

        // For Singleton instantiation
        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): GithubBrowserDb {
            return Room.databaseBuilder(context, GithubBrowserDb::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

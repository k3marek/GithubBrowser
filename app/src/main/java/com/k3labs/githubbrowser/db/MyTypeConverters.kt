package com.k3labs.githubbrowser.db

import androidx.room.TypeConverter
import com.k3labs.githubbrowser.GithubBrowserApp
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class MyTypeConverters {
    init {
        GithubBrowserApp.appComponent.inject(this)
    }

    @Inject
    lateinit var moshi: Moshi

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let {
            it.split(",").map {
                try {
                    it.toInt()
                } catch (ex: NumberFormatException) {
                    Timber.e(ex, "Cannot convert $it to number")
                    null
                }
            }
        }?.filterNotNull()
    }

    @TypeConverter
    fun intListToString(ints: List<Int>?): String? {
        return ints?.joinToString(",")
    }

    @TypeConverter
    fun stringToStringList(data: String?): List<String>? {
        val listMyData = newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(listMyData)
        return data?.let { adapter.fromJson(it) }
    }

    @TypeConverter
    fun stringListToString(input: List<String>?): String? {
        val listMyData = newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(listMyData)
        return input?.let { adapter.toJson(it) }
    }
}

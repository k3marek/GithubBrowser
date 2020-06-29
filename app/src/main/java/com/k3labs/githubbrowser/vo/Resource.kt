package com.k3labs.githubbrowser.vo

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> error(throwable: Throwable?, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, throwable?.message)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

    fun isAvailable(): Boolean {
        return status == Status.SUCCESS
    }

    fun isEmpty(): Boolean {
        return status == Status.SUCCESS &&
                (data == null ||
                        when (data) {
                            is Collection<*> -> data.size == 0
                            else -> false
                        })
    }

    fun size(): Int {
        return if (status == Status.SUCCESS && data != null) {
            when (data) {
                is Collection<*> -> data.size
                else -> 0
            }
        } else 0
    }
}
package com.k3labs.githubbrowser.api.calladapter

import timber.log.Timber
import java.io.IOException
import java.util.regex.Pattern

sealed class NetworkResponse<out T : Any, out U : Any> {

    /**
     * Success response with body
     */
    data class Success<T : Any>(val body: T, val linkHeader: String?) :
        NetworkResponse<T, Nothing>() {

        val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
            (linkHeader?.extractLinks() ?: emptyMap())[NEXT_LINK]?.let { next ->
                val matcher = PAGE_PATTERN.matcher(next)
                if (!matcher.find() || matcher.groupCount() != 1) {
                    null
                } else {
                    try {
                        Integer.parseInt(matcher.group(1))
                    } catch (ex: NumberFormatException) {
                        Timber.w(
                            "cannot parse next page from %s",
                            next
                        )
                        null
                    }
                }
            }
        }
    }

    /**
     * Failure response with body
     */
    data class ApiError<U : Any>(val body: U, val code: Int) : NetworkResponse<Nothing, U>()

    /**
     * Network error
     */
    data class NetworkError(val error: IOException) : NetworkResponse<Nothing, Nothing>()

    /**
     * For example, json parsing error
     */
    data class UnknownError(val error: Throwable?) : NetworkResponse<Nothing, Nothing>()


    companion object {
        private val LINK_PATTERN =
            Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN =
            Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)] = matcher.group(1)
                }
            }
            return links
        }
    }
}
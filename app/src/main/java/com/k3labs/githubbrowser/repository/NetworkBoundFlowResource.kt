package com.k3labs.githubbrowser.repository

import com.k3labs.githubbrowser.api.calladapter.NetworkResponse
import com.k3labs.githubbrowser.vo.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * A generic class that can provide a resource backed by both the sqlite database by Flow and the network.
 *
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */
@Deprecated("Use networkBoundResource inline function")
abstract class NetworkBoundFlowResource<ResultType, RequestType> {

    @ExperimentalCoroutinesApi
    fun asFlow(): Flow<Resource<ResultType>> = flow {
        val flow = query()
            .onStart { emit(Resource.loading(null)) }
            .flatMapConcat { data ->
                if (shouldFetch(data)) {
                    emit(Resource.loading(data))

                    try {
                        saveFetchResult(fetch())
                        query().map { Resource.success(it) }
                    } catch (throwable: Throwable) {
                        onFetchFailed(throwable)
                        query().map { Resource.error(throwable.message ?: "", it) }
                    }
                } else {
                    query().map { Resource.success(it) }
                }
            }

        emitAll(flow)
    }

    abstract fun query(): Flow<ResultType>

    abstract suspend fun fetch(): RequestType

    abstract suspend fun saveFetchResult(data: RequestType)

    open fun onFetchFailed(throwable: Throwable) = Unit

    open fun shouldFetch(data: ResultType?) = true
}

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow<Resource<ResultType>> {
    emit(Resource.loading(null))
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.loading(data))

        try {
            saveFetchResult(fetch())
            query().map { Resource.success(it) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.error(throwable, it) }
        }
    } else {
        query().map { Resource.success(it) }
    }

    emitAll(flow)
}

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <ResultType, RequestType : Any> networkBoundResourceForNetworkResponse(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> NetworkResponse<RequestType, RequestType>,
    crossinline processResponse: suspend (response: NetworkResponse.Success<RequestType>) -> RequestType = { response -> response.body },
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow<Resource<ResultType>> {
    emit(Resource.loading(null))
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.loading(data))

        try {
            when (val result = fetch()) {
                is NetworkResponse.Success -> {
                    saveFetchResult(processResponse(result))
                }
                is NetworkResponse.NetworkError -> {
                    Resource.error(
                        result.error,
                        result
                    )
                }
                is NetworkResponse.UnknownError -> {
                    Resource.error(
                        result.error,
                        result
                    )
                }
                is NetworkResponse.ApiError -> Resource.error(
                    "API error",
                    result.body
                )
            }
            query().map { Resource.success(it) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.error(throwable, it) }
        }
    } else {
        query().map { Resource.success(it) }
    }

    emitAll(flow)
}
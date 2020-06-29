package com.k3labs.githubbrowser.repository

import androidx.room.withTransaction
import com.k3labs.githubbrowser.api.GithubService
import com.k3labs.githubbrowser.api.calladapter.NetworkResponse
import com.k3labs.githubbrowser.db.GithubBrowserDb
import com.k3labs.githubbrowser.vo.RepoSearchResult
import com.k3labs.githubbrowser.vo.Resource
import kotlinx.coroutines.flow.flow
import java.io.IOException

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
suspend inline fun fetchNextSearchPageTask(
    query: String,
    githubService: GithubService,
    db: GithubBrowserDb
) = flow<Resource<Boolean>> {
    val current = db.repoSearchResultDao().findSearchResult(query)
    if (current == null) {
        emit(Resource.error("No current", null))
    } else {
        val nextPage = current.next
        if (nextPage == null) {
            emit(Resource.success(false))
        } else {
            val newValue = try {
                when (val response = githubService.searchRepos(query, nextPage)) {
                    is NetworkResponse.Success -> {
                        // merge all repo ids into 1 list so that it is easier to fetch the
                        // result list.
                        val ids = arrayListOf<Int>()
                        ids.addAll(current.repoIds)

                        ids.addAll(response.body.items.map { it.id })
                        val merged = RepoSearchResult(
                            query, ids,
                            response.body.total, response.nextPage
                        )
                        db.withTransaction {
                            db.repoSearchResultDao().insert(merged)
                            db.repoDao().insertRepos(response.body.items)
                        }
                        Resource.success(response.nextPage != null)
                    }
                    is NetworkResponse.ApiError -> {
                        Resource.error("ApiError", false)
                    }
                    is NetworkResponse.NetworkError -> {
                        Resource.error("NetworkError", false)
                    }
                    is NetworkResponse.UnknownError -> {
                        Resource.error("UnknownError", false)
                    }
                }


            } catch (e: IOException) {
                //emit(Resource.error(e, it))
                Resource.error(e.message!!, true)
            }
            emit(newValue)
        }
    }
}

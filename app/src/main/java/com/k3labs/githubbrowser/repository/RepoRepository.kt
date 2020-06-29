package com.k3labs.githubbrowser.repository

import androidx.room.withTransaction
import com.k3labs.githubbrowser.api.GithubService
import com.k3labs.githubbrowser.db.GithubBrowserDb
import com.k3labs.githubbrowser.db.RepoDao
import com.k3labs.githubbrowser.db.RepoSearchResultDao
import com.k3labs.githubbrowser.util.RateLimiter
import com.k3labs.githubbrowser.vo.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles repo instances.
 *
 * Repository - type of this class.
 */
@Singleton
class RepoRepository @Inject constructor(
    private val db: GithubBrowserDb,
    private val repoDao: RepoDao,
    private val repoSearchResultDao: RepoSearchResultDao,
    private val githubService: GithubService
) {
    private val repoListRateLimit = RateLimiter<String>(2, TimeUnit.MINUTES)

    fun loadRepos(owner: String): Flow<Resource<List<RepoAndFav>>> {
        return networkBoundResource(
            query = { repoDao.loadRepositories(owner) },
            fetch = { githubService.getRepos(owner) },
            saveFetchResult = { items -> repoDao.insertRepos(items) },
            onFetchFailed = { throwable ->
                throwable.printStackTrace()
                repoListRateLimit.reset(owner)
            }
        )
    }

    fun loadRepo(owner: String, name: String): Flow<Resource<RepoAndFav?>> {
        return networkBoundResource(
            query = {
                repoDao.load(
                    ownerLogin = owner,
                    name = name
                )
            },
            fetch = {
                githubService.getRepo(
                    owner = owner,
                    name = name
                )
            },
            shouldFetch = { item -> item == null },
            saveFetchResult = { item -> item?.let { repoDao.insert(item) } },
            onFetchFailed = { throwable -> repoListRateLimit.reset(owner) }
        )
    }

    fun loadContributors(
        repoId: Int,
        owner: String,
        name: String
    ): Flow<Resource<List<Contributor>?>> {
        return networkBoundResource(
            query = { repoDao.loadContributors(owner, name) },
            fetch = { githubService.getContributors(owner, name) },
            saveFetchResult = { items ->
                Timber.v("save $items")
                items.forEach {
                    it.repoId = repoId
                    it.repoName = name
                    it.repoOwner = owner
                }
                db.withTransaction {
                    repoDao.createRepoIfNotExists(
                        Repo(
                            id = Repo.UNKNOWN_ID,
                            name = name,
                            fullName = "$owner/$name",
                            description = "",
                            owner = Repo.Owner(owner, null),
                            stars = 0
                        )
                    )
                    repoDao.insertContributors(items)
                }
            },
            shouldFetch = { data -> data == null || data.isEmpty() },
            onFetchFailed = { throwable -> Timber.e(throwable) }
        )
    }

    suspend fun searchNextPage(query: String): Flow<Resource<Boolean>> {
        return fetchNextSearchPageTask(
            query,
            githubService,
            db
        )
    }

    fun search(query: String): Flow<Resource<List<RepoAndFav>?>> {
        return networkBoundResourceForNetworkResponse(
            query = {
                repoSearchResultDao.search(query).flatMapLatest { searchData ->
                    if (searchData != null) {
                        repoDao.loadOrdered(searchData.repoIds)
                    } else {
                        flowOf(searchData)
                    }
                }
            },
            fetch = { githubService.searchRepos(query) },
            processResponse = { response ->
                val body = response.body
                body.nextPage = response.nextPage
                body
            },
            saveFetchResult = { item ->
                val repoIds = item.items.map { it.id }
                val repoSearchResult = RepoSearchResult(
                    query = query,
                    repoIds = repoIds,
                    totalCount = item.total,
                    next = item.nextPage
                )

                db.withTransaction {
                    repoDao.insertRepos(item.items)
                    repoSearchResultDao.insert(repoSearchResult)
                }
            },
            onFetchFailed = { throwable -> Timber.e(throwable) }
        )
    }
}

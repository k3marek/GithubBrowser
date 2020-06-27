package com.k3labs.githubbrowser.repository

import com.k3labs.githubbrowser.AppExecutors
import com.k3labs.githubbrowser.api.GithubService
import com.k3labs.githubbrowser.db.UserDao
import com.k3labs.githubbrowser.vo.Resource
import com.k3labs.githubbrowser.vo.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */
@Singleton
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val githubService: GithubService
) {

    fun loadUser(login: String): Flow<Resource<User>> {
        return networkBoundResource(
            query = { userDao.findByLogin(login) },
            shouldFetch = { data -> data == null },
            fetch = { githubService.getUser(login) },
            saveFetchResult = { item -> userDao.insert(item) }
        )
    }
}

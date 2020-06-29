package com.k3labs.githubbrowser.repository

import com.k3labs.githubbrowser.db.FavReposDao
import com.k3labs.githubbrowser.vo.FavoriteRepo
import com.k3labs.githubbrowser.vo.RepoAndFav
import com.k3labs.githubbrowser.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles fav repos.
 */
@Singleton
class FavRepoRepository @Inject constructor(
    private val favReposDao: FavReposDao
) {

    fun loadFavRepos(): Flow<Resource<List<RepoAndFav>?>> {
        return favReposDao.loadFavRepos()
            .onStart {
                emit(null)
            }.map {
                when (it) {
                    null -> {
                        Resource.loading<List<RepoAndFav>?>(it)
                    }
                    else -> {
                        Resource.success<List<RepoAndFav>?>(it)
                    }
                }
            }
    }

    suspend fun deleteFav(favoriteRepo: FavoriteRepo) {
        favReposDao.deleteFavorite(favoriteRepo)
    }

    suspend fun addFav(id: Int, name: String, owner: String) {
        favReposDao.insertFav(FavoriteRepo(id, name, owner))
    }

    fun loadFav(id: Int, owner: String, name: String): Flow<FavoriteRepo?> {
        return favReposDao.loadFavRepo(id, owner, name)
    }
}

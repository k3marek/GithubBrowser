package com.k3labs.githubbrowser.repository

import com.k3labs.githubbrowser.db.FavReposDao
import com.k3labs.githubbrowser.vo.FavoriteRepo
import com.k3labs.githubbrowser.vo.RepoAndFav
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */
@Singleton
class FavRepoRepository @Inject constructor(
    private val favReposDao: FavReposDao
) {

    fun loadFavRepos(): Flow<List<RepoAndFav>?> {
        return favReposDao.loadFavRepos()
    }

    suspend fun deleteFav(favoriteRepo: FavoriteRepo) {
        favReposDao.deleteFavorite(favoriteRepo)
    }

    suspend fun addFav(id: Int, name: String, owner: String) {
        favReposDao.insertFav(FavoriteRepo(id, name, owner))
    }

    fun loadFav(id: Int, owner: String, name: String): Flow<FavoriteRepo?> {
        return favReposDao.loadFavRepo(id,owner,name)
    }
}

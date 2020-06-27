package com.k3labs.githubbrowser.db

import androidx.room.*
import com.k3labs.githubbrowser.vo.FavoriteRepo
import com.k3labs.githubbrowser.vo.RepoAndFav
import kotlinx.coroutines.flow.Flow

/**
 * Interface for database access on Fav repos related operations.
 */
@Dao
abstract class FavReposDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFav(favoriteRepo: FavoriteRepo)

    @Delete
    abstract suspend fun deleteFavorite(favoriteRepo: FavoriteRepo)

    @Transaction
    @Query(
        """
        SELECT * FROM repo 
        JOIN favorite_repo AS fav ON repo.id == fav.repoId 
    """
    )
    abstract fun loadFavRepos(): Flow<List<RepoAndFav>?>

    @Query(
        """
        SELECT * FROM favorite_repo 
        WHERE repoId = :id
        AND repoName = :name
        AND repoOwner = :owner
    """
    )
    abstract fun loadFavRepo(id: Int, owner: String, name: String): Flow<FavoriteRepo?>
}

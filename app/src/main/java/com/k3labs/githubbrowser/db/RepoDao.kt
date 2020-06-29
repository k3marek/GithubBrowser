package com.k3labs.githubbrowser.db

import android.util.SparseIntArray
import androidx.room.*
import com.k3labs.githubbrowser.vo.Contributor
import com.k3labs.githubbrowser.vo.Repo
import com.k3labs.githubbrowser.vo.RepoAndFav
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

/**
 * Interface for database access on Repo related operations.
 */
@Dao
abstract class RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(vararg repos: Repo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertContributor(contributor: Contributor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertContributors(contributors: List<Contributor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRepos(repositories: List<Repo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun createRepoIfNotExists(repo: Repo): Long

    @Transaction
    @Query(
        """
            SELECT * FROM repo, favorite_repo 
            WHERE owner_login = :ownerLogin 
            AND name = :name
            """
    )
    abstract fun load(ownerLogin: String, name: String): Flow<RepoAndFav?>

    @Query(
        """
        SELECT login, avatarUrl,repoId, repoName, repoOwner, contributions FROM contributor
        WHERE repoName = :name AND repoOwner = :owner
        ORDER BY contributions DESC"""
    )
    abstract fun loadContributors(owner: String, name: String): Flow<List<Contributor>?>

    @Transaction
    @Query(
        """
        SELECT * FROM repo
        WHERE owner_login = :owner
        ORDER BY stars DESC"""
    )
    abstract fun loadRepositories(owner: String): Flow<List<RepoAndFav>>


    fun loadOrdered(repoIds: List<Int>): Flow<List<RepoAndFav>> {
        val order = SparseIntArray()
        repoIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return loadById(repoIds).mapLatest { repositories ->
            repositories.sortedWith(compareBy { order.get(it.repo.id) })
        }
    }

    @Transaction
    @Query("SELECT * FROM repo WHERE id IN (:repoIds)")
    protected abstract fun loadById(repoIds: List<Int>): Flow<List<RepoAndFav>>

}

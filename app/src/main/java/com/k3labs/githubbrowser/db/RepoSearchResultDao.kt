package com.k3labs.githubbrowser.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.k3labs.githubbrowser.vo.RepoSearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface for database access on Repo related operations.
 */
@Dao
abstract class RepoSearchResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(result: RepoSearchResult)

    @Query("SELECT * FROM RepoSearchResult WHERE `query` = :query")
    abstract fun search(query: String): Flow<RepoSearchResult?>

    @Query("SELECT * FROM RepoSearchResult WHERE `query` = :query")
    abstract suspend fun findSearchResult(query: String): RepoSearchResult?

}

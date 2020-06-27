package com.k3labs.githubbrowser.vo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favorite_repo",
    primaryKeys = ["repoId"],
    foreignKeys = [
        ForeignKey(
            entity = Repo::class,
            parentColumns = ["id", "name", "owner_login"],
            childColumns = ["repoId", "repoName", "repoOwner"],
            onUpdate = ForeignKey.CASCADE,
            deferred = false
        )
    ]
)
data class FavoriteRepo(
    val repoId: Int,
    var repoName: String,
    var repoOwner: String
) {
    // does not show up in the response but set in post processing.

    // does not show up in the response but set in post processing.
}
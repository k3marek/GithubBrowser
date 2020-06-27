package com.k3labs.githubbrowser.vo

import androidx.room.Embedded
import androidx.room.Relation

data class RepoAndFav(

    @Embedded
    val repo: Repo,

    @Relation(
        parentColumn = "id",
        entityColumn = "repoId"
    )
    val favoriteRepo: FavoriteRepo? = null

) {
    val isFav: Boolean
        get() = favoriteRepo != null
}
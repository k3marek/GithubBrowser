package com.k3labs.githubbrowser.vo

import androidx.room.Embedded
import androidx.room.Relation

data class UserAndRepos(

    @Embedded
    val user: User,

    @Relation(
        parentColumn = "login",
        entityColumn = "owner_login"
    )
    val repo: List<Repo> = mutableListOf()
)

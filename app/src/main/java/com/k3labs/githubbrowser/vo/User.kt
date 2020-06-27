package com.k3labs.githubbrowser.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(primaryKeys = ["login"])
data class User(
    @Json(name = "login")
    val login: String,
    @Json(name = "avatar_url")
    val avatarUrl: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "company")
    val company: String?,
    @Json(name = "repos_url")
    val reposUrl: String?,
    @Json(name = "blog")
    val blog: String?
)

package com.k3labs.githubbrowser.vo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.squareup.moshi.Json

@Entity(
    tableName = "repo",
    indices = [
        Index("id"),
        Index("owner_login")],
    primaryKeys = ["id", "name", "owner_login"]
)
data class Repo(
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "full_name")
    val fullName: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "owner")
    @field:Embedded(prefix = "owner_")
    val owner: Owner,
    @Json(name = "stargazers_count")
    val stars: Int
) {
    data class Owner(
        @Json(name = "login")
        val login: String,
        @Json(name = "url")
        val url: String?
    )

    companion object {
        const val UNKNOWN_ID = -1
    }
}
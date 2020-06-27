package com.k3labs.githubbrowser.vo

import androidx.room.Entity
import androidx.room.ForeignKey
import com.squareup.moshi.Json

@Entity(
    primaryKeys = ["repoName", "repoOwner", "login"],
    foreignKeys = [ForeignKey(
        entity = Repo::class,
        parentColumns = ["id", "name", "owner_login"],
        childColumns = ["repoId", "repoName", "repoOwner"],
        onUpdate = ForeignKey.CASCADE,
        deferred = false
    )]
)
data class Contributor(
    @Json(name = "login")
    val login: String,
    @Json(name = "contributions")
    val contributions: Int,
    @Json(name = "avatar_url")
    val avatarUrl: String?
) {
    var repoId: Int = 0
    // does not show up in the response but set in post processing.
    lateinit var repoName: String

    // does not show up in the response but set in post processing.
    lateinit var repoOwner: String
}

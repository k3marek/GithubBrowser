package com.k3labs.githubbrowser.vo

import androidx.room.Entity
import androidx.room.TypeConverters
import com.k3labs.githubbrowser.db.MyTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(MyTypeConverters::class)
data class RepoSearchResult(
    val query: String,
    val repoIds: List<Int>,
    val totalCount: Int,
    val next: Int?
)

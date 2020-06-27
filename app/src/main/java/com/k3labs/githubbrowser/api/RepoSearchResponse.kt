package com.k3labs.githubbrowser.api

import com.k3labs.githubbrowser.vo.Repo
import com.squareup.moshi.Json


/**
 * Simple object to hold repo search responses. This is different from the Entity in the database
 * because we are keeping a search result in 1 row and denormalizing list of results into a single
 * column.
 */
data class RepoSearchResponse(
    @field:Json(name ="total_count")
    val total: Int = 0,
    @field:Json(name="items")
    val items: List<Repo>
) {
    var nextPage: Int? = null
}

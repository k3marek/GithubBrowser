package com.k3labs.githubbrowser.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.k3labs.githubbrowser.api.*
import com.k3labs.githubbrowser.db.GithubBrowserDb
import com.k3labs.githubbrowser.vo.Resource

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
//todo
class FetchNextSearchPageTask constructor(
    private val query: String,
    private val githubService: GithubService,
    private val db: GithubBrowserDb
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>>()
    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {

    }
}

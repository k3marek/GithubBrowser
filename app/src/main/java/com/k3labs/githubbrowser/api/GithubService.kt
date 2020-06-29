package com.k3labs.githubbrowser.api

import com.k3labs.githubbrowser.api.calladapter.NetworkResponse
import com.k3labs.githubbrowser.vo.Contributor
import com.k3labs.githubbrowser.vo.Repo
import com.k3labs.githubbrowser.vo.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST API access points
 */
interface GithubService {
    @GET("users/{login}")
    suspend fun getUser(@Path("login") login: String): User

    @GET("users/{login}/repos")
    suspend fun getRepos(@Path("login") login: String): List<Repo>

    @GET("repos/{owner}/{name}")
    suspend fun getRepo(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): Repo?

    @GET("repos/{owner}/{name}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): List<Contributor>


    @GET("search/repositories")
    suspend fun searchRepos(@Query("q") query: String): NetworkResponse<RepoSearchResponse, RepoSearchResponse>

    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int
    ): NetworkResponse<RepoSearchResponse, RepoSearchResponse>

}

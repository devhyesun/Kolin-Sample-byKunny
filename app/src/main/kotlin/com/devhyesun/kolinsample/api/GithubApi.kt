package com.devhyesun.kolinsample.api

import com.devhyesun.kolinsample.api.model.GithubRepo
import com.devhyesun.kolinsample.api.model.RepoSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("search/repositories")
    fun searchRepository(@Query("q") query: String): Call<RepoSearchResponse>

    @GET("repos/{owner}/{name}")
    fun getRepository(@Path("owner") ownerLogin: String, @Path("name") repoName: String): Call<GithubRepo>
}

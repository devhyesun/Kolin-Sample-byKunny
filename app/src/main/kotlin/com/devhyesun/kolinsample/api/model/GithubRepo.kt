package com.devhyesun.kolinsample.api.model

import com.google.gson.annotations.SerializedName

class GithubRepo(
    val name: String,
    @field:SerializedName("full_name") val fullName: String,
    val owner: GithubOwner,
    val description: String,
    val language: String,
    @field:SerializedName("updated_at") val updateAt: String,
    @field:SerializedName("stargazers_count") var stars: Int)

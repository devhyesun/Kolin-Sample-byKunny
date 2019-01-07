package com.devhyesun.kolinsample.api.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "repositories")
class GithubRepo(
    val name: String,
    @field:SerializedName("full_name")
    @PrimaryKey @ColumnInfo(name = "full_name") val fullName: String,
    @Embedded val owner: GithubOwner,
    val description: String?,
    val language: String?,
    @field:SerializedName("updated_at") val updateAt: String,
    @field:SerializedName("stargazers_count") var stars: Int)

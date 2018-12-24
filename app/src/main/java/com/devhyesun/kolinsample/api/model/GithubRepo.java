package com.devhyesun.kolinsample.api.model;

import com.google.gson.annotations.SerializedName;

public final class GithubRepo {

    public final String name;

    @SerializedName("full_name")
    public final String fullName;

    public final GithubOwner owner;

    public final String description;

    public final String language;

    @SerializedName("updated_at")
    public final String updateAt;

    @SerializedName("starazers_count")
    public int stars;

    public GithubRepo(String name, String fullName, GithubOwner owner, String description, String language, String updateAt, int stars) {
        this.name = name;
        this.fullName = fullName;
        this.owner = owner;
        this.description = description;
        this.language = language;
        this.updateAt = updateAt;
        this.stars = stars;
    }
}

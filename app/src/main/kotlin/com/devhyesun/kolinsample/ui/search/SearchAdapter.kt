package com.devhyesun.kolinsample.ui.search

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.model.GithubRepo

import java.util.ArrayList

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.RepositoryHolder>() {

    private var githubRepoList: MutableList<GithubRepo> = ArrayList()

    private var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RepositoryHolder {
        return RepositoryHolder(viewGroup)
    }

    override fun onBindViewHolder(repositoryHolder: RepositoryHolder, i: Int) {
        val githubRepo = githubRepoList[i]

        Glide.with(repositoryHolder.itemView.context)
            .load(githubRepo.owner.avatarUrl)
            .into(repositoryHolder.ivProfile)

        repositoryHolder.tvName.text = githubRepo.fullName
        repositoryHolder.tvLanguage.text = if (TextUtils.isEmpty(githubRepo.language))
            repositoryHolder.itemView.context.getText(R.string.no_language_specified)
        else
            githubRepo.language

        repositoryHolder.itemView.setOnClickListener {
            if (itemClickListener != null) {
                itemClickListener!!.onItemClick(githubRepo)
            }
        }
    }

    override fun getItemCount(): Int {
        return githubRepoList.size
    }

    fun setGithubRepoList(githubRepoList: List<GithubRepo>) {
        this.githubRepoList = githubRepoList.toMutableList()
    }

    fun setItemClickListener(itemClickListener: ItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    fun clearGihubRepoList() {
        this.githubRepoList.clear()
    }

    class RepositoryHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false)) {
        var ivProfile: ImageView
        var tvName: TextView
        var tvLanguage: TextView

        init {

            ivProfile = itemView.findViewById(R.id.iv_repository_profile)
            tvName = itemView.findViewById(R.id.tv_repository_name)
            tvLanguage = itemView.findViewById(R.id.tv_repository_language)
        }
    }

    interface ItemClickListener {
        fun onItemClick(repository: GithubRepo)
    }
}

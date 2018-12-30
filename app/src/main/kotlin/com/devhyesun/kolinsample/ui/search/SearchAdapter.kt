package com.devhyesun.kolinsample.ui.search

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.model.GithubRepo
import kotlinx.android.synthetic.main.item_repository.view.*

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.RepositoryHolder>() {

    private var githubRepoList: MutableList<GithubRepo> = mutableListOf()

    private var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) =
        RepositoryHolder(viewGroup)

    override fun onBindViewHolder(repositoryHolder: RepositoryHolder, i: Int) {
        githubRepoList[i].let {githubRepo ->
            with(repositoryHolder.itemView) {
                Glide.with(repositoryHolder.itemView.context)
                    .load(githubRepo.owner.avatarUrl)
                    .into(iv_repository_profile)

                tv_repository_name.text = githubRepo.fullName
                tv_repository_language.text = if (TextUtils.isEmpty(githubRepo.language))
                    repositoryHolder.itemView.context.getText(R.string.no_language_specified)
                else
                    githubRepo.language

                setOnClickListener {
                    if (itemClickListener != null) {
                        itemClickListener!!.onItemClick(githubRepo)
                    }
                }
            }
        }
    }

    override fun getItemCount() =
        githubRepoList.size

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
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false))

    interface ItemClickListener {
        fun onItemClick(repository: GithubRepo)
    }
}

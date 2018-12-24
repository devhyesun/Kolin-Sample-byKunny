package com.devhyesun.kolinsample.ui.search;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devhyesun.kolinsample.R;
import com.devhyesun.kolinsample.api.model.GithubRepo;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.RepositoryHolder>{

    private List<GithubRepo> githubRepoList = new ArrayList<>();

    @Nullable
    private ItemClickListener itemClickListener;

    @NonNull
    @Override
    public RepositoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RepositoryHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RepositoryHolder repositoryHolder, int i) {
        final GithubRepo githubRepo = githubRepoList.get(i);

        Glide.with(repositoryHolder.itemView.getContext())
                .load(githubRepo.owner.avatarUrl)
                .into(repositoryHolder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return githubRepoList.size();
    }

    public void setGithubRepoList(List<GithubRepo> githubRepoList) {
        this.githubRepoList = githubRepoList;
    }

    public void setItemClickListener(@Nullable ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void clearGihubRepoList() {
        this.githubRepoList.clear();
    }

    static class RepositoryHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvName;
        TextView tvLanguage;

        RepositoryHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repository, parent, false));

            ivProfile = itemView.findViewById(R.id.iv_repository_profile);
            tvName = itemView.findViewById(R.id.tv_repository_name);
            tvLanguage = itemView.findViewById(R.id.tv_repository_language);
        }
    }

    public interface ItemClickListener {
        void onItemClick(GithubRepo repository);
    }
}

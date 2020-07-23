package com.clicbrics.consumer.view.adapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.BlogListActivity;
import com.clicbrics.consumer.databinding.ItemArticleListLayoutBinding;
import com.clicbrics.consumer.viewmodel.ArticleViewModel;

import java.util.List;

/**
 * Created by Alok on 02-08-2018.
 */
public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.BindingHolder> {

    private List<Blog> blogList;
    private int mTotalBlogCount;
    public ArticleListAdapter(List<Blog> blogList,int totalBlogCount) {
        this.blogList = blogList;
        this.mTotalBlogCount = totalBlogCount;
    }

    @NonNull
    @Override
    public ArticleListAdapter.BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemArticleListLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_article_list_layout,parent,false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleListAdapter.BindingHolder holder, final int position) {
        final ItemArticleListLayoutBinding binding = holder.binding;

        if(position == 0){
            holder.binding.idCoverImageLayout.setVisibility(View.VISIBLE);
            holder.binding.idArticleListLayout.setVisibility(View.GONE);
        }else{
            holder.binding.idCoverImageLayout.setVisibility(View.GONE);
            holder.binding.idArticleListLayout.setVisibility(View.VISIBLE);
        }
        Blog blog = blogList.get(position);
        ArticleViewModel model = new ArticleViewModel(blog);
        binding.setArticleModel(model);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(binding.getRoot().getContext(),BlogListActivity.class);
                intent.putExtra("Position",position);
                intent.putExtra("TotalBlogCount",mTotalBlogCount);
                intent.putExtra("ArticleType","blog");
                binding.getRoot().getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(blogList != null) {
            return blogList.size();
        }else{
            return 0;
        }
    }

    class BindingHolder extends RecyclerView.ViewHolder{
        ItemArticleListLayoutBinding binding;
        public BindingHolder(ItemArticleListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

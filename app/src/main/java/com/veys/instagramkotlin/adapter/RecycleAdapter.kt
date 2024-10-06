package com.veys.instagramkotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.veys.instagramkotlin.databinding.RecycleRowBinding
import com.veys.instagramkotlin.model.Post

class RecycleAdapter(val postList:ArrayList<Post>):RecyclerView.Adapter<RecycleAdapter.postHolder>() {

    class postHolder(val binding: RecycleRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): postHolder {
        val binding = RecycleRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return postHolder(binding)
    }

    override fun onBindViewHolder(holder: postHolder, position: Int) {
        holder.binding.recycleEmailText.text = postList.get(position).userEmail
        holder.binding.recycleCommentText.text = postList.get(position).comment
        Picasso.get().load(postList.get(position).imagesUri).into(holder.binding.recycleImageView)

    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
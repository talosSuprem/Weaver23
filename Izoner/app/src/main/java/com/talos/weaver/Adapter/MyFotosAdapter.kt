package com.talos.weaver.Adapter

import android.content.Context

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater

import com.bumptech.glide.Glide

import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.talos.weaver.Fragments.PostDetailFragment
import com.talos.weaver.Model.Post
import com.talos.weaver.R


class MyFotosAdapter(private val mContext: Context, private val mPosts: MutableList<Post?>?) :
    RecyclerView.Adapter<MyFotosAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.fotos_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val post = mPosts!![position]
        Glide.with(mContext).load(post!!.postimage).into(holder.post_image)
        holder.post_image.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("postid", post.postid)
            editor.apply()
            //(mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(
                //R.id.fragment_container,
                PostDetailFragment()
           // ).commit()
        }
    }

    override fun getItemCount(): Int {
        return mPosts!!.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var post_image: ImageView

        init {
            post_image = itemView.findViewById(R.id.post_image)
        }
    }
}
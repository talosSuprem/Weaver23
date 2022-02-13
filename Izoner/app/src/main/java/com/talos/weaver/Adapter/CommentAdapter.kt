package com.talos.weaver.Adapter

import android.app.AlertDialog
import android.content.Context

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import android.view.ViewGroup
import android.view.LayoutInflater

import com.google.firebase.auth.FirebaseAuth
import android.content.Intent

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseError
import android.view.View
import android.widget.*
import com.talos.weaver.MainActivity
import com.talos.weaver.Model.Comment
import com.talos.weaver.Model.User
import com.talos.weaver.R


class CommentAdapter(
    private val mContext: Context,
    private val mComment: MutableList<Comment?>?,
    private val postid: String
) : RecyclerView.Adapter<CommentAdapter.ImageViewHolder>() {
    private var firebaseUser: FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ImageViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ImageViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComment!![position]
        holder.comment.text = comment!!.comment
        getUserInfo(holder.image_profile, holder.username, comment.publisher)
        holder.username.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("publisherid", comment.publisher)
            mContext.startActivity(intent)
        }
        holder.image_profile.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("publisherid", comment.publisher)
            mContext.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
            if (comment.publisher == firebaseUser!!.uid) {
                val alertDialog = AlertDialog.Builder(mContext).create()
                alertDialog.setTitle("Do you want to delete?")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "No"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, "Yes"
                ) { dialog, which ->
                    FirebaseDatabase.getInstance().getReference("Comments")
                        .child(postid).child(comment.commentid!!)
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    dialog.dismiss()
                }
                alertDialog.show()
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return mComment!!.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image_profile: ImageView
        var username: TextView
        var comment: TextView

        init {
            image_profile = itemView.findViewById(R.id.image_profile)
            username = itemView.findViewById(R.id.username)
            comment = itemView.findViewById(R.id.comment)
        }
    }

    private fun getUserInfo(imageView: ImageView, username: TextView, publisherid: String?) {
        val reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(publisherid!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(
                    User::class.java
                )
                Glide.with(mContext).load(user!!.imageurl).into(imageView)
                username.text = user.username
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
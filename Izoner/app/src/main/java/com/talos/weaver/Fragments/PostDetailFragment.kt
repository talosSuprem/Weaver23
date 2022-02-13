package com.talos.weaver.Fragments

import android.content.Context

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle

import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import androidx.fragment.app.Fragment
import com.talos.weaver.Adapter.PostAdapter
import com.talos.weaver.Model.Post
import com.talos.weaver.R
import java.util.ArrayList

class PostDetailFragment constructor() : Fragment() {
    var postid: String? = null
    private var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post?>? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_post_detail, container, false)
        val prefs: SharedPreferences =
            requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        postid = prefs.getString("postid", "none")
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager: LinearLayoutManager = LinearLayoutManager(getContext())
        recyclerView!!.setLayoutManager(mLayoutManager)
        postList = ArrayList()
        postAdapter = getContext()?.let { PostAdapter(it, postList) }
        recyclerView!!.setAdapter(postAdapter)
        readPost()
        return view
    }

    private fun readPost() {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Posts").child(
                (postid)!!
            )
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList!!.clear()
                val post: Post? = dataSnapshot.getValue(Post::class.java)
                postList!!.add(post)
                postAdapter!!.notifyDataSetChanged()
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
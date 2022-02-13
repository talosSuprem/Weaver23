package com.talos.weaver


import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseError
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.talos.weaver.Model.Comment
import com.talos.weaver.Model.User

import java.util.ArrayList
import java.util.HashMap

class CommentsActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var commentAdapter: com.talos.weaver.Adapter.CommentAdapter? = null
    private var commentList: MutableList<Comment?>? = null
    var addcomment: EditText? = null
    var image_profile: ImageView? = null
    var post: TextView? = null
    var postid: String? = null
    var publisherid: String? = null
    var firebaseUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Comments")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        val intent = intent
        postid = intent.getStringExtra("postid")
        publisherid = intent.getStringExtra("publisherid")
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(this)
        recyclerView!!.setLayoutManager(mLayoutManager)
        commentList = ArrayList()
        commentAdapter =
            com.talos.weaver.Adapter.CommentAdapter(this, commentList, postid!!)
        recyclerView!!.setAdapter(commentAdapter)
        post = findViewById(R.id.post)
        addcomment = findViewById(R.id.add_comment)
        image_profile = findViewById(R.id.image_profile)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        post!!.setOnClickListener(View.OnClickListener {
            if (addcomment!!.getText().toString() == "") {
                Toast.makeText(
                    this@CommentsActivity,
                    "You can't send empty message",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                addComment()
            }
        })
        image
        readComments()
    }

    private fun addComment() {
        val reference = FirebaseDatabase.getInstance().getReference("Comments").child(
            postid!!
        )
        val commentid = reference.push().key
        val hashMap = HashMap<String, Any?>()
        hashMap["comment"] = addcomment!!.text.toString()
        hashMap["publisher"] = firebaseUser!!.uid
        hashMap["commentid"] = commentid
        reference.child(commentid!!).setValue(hashMap)
        addNotification()
        addcomment!!.setText("")
    }

    private fun addNotification() {
        val reference = FirebaseDatabase.getInstance().getReference("Notifications").child(
            publisherid!!
        )
        val hashMap = HashMap<String, Any?>()
        hashMap["userid"] = firebaseUser!!.uid
        hashMap["text"] = "commented: " + addcomment!!.text.toString()
        hashMap["postid"] = postid
        hashMap["ispost"] = true
        reference.push().setValue(hashMap)
    }

    private val image: Unit
        private get() {
            val reference = FirebaseDatabase.getInstance().getReference("Users").child(
                firebaseUser!!.uid
            )
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(
                        User::class.java
                    )
                    Glide.with(applicationContext).load(user!!.imageurl).into(image_profile!!)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun readComments() {
        val reference = FirebaseDatabase.getInstance().getReference("Comments").child(
            postid!!
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val comment = snapshot.getValue(
                        Comment::class.java
                    )
                    commentList!!.add(comment)
                }
                commentAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
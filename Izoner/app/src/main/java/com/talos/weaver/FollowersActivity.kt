package com.talos.weaver


import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import androidx.appcompat.widget.Toolbar
import com.talos.weaver.Adapter.UserAdapter
import com.talos.weaver.Model.User

import java.util.ArrayList

class FollowersActivity : AppCompatActivity() {
    var id: String? = null
    var title: String? = null
    private var idList: MutableList<String?>? = null
    var recyclerView: RecyclerView? = null
    var userAdapter: UserAdapter? = null
    var userList: MutableList<User?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followers)
        val intent = intent
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))
        userList = ArrayList()
        userAdapter = UserAdapter(this, userList, false)
        recyclerView!!.setAdapter(userAdapter)
        idList = ArrayList()
        when (title) {
            "likes" -> likes
            "following" -> following
            "followers" -> followers
            "views" -> views
        }
    }

    private val views: Unit
        private get() {
            val reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(id!!).child(intent.getStringExtra("storyid")!!).child("views")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    idList!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        idList!!.add(snapshot.key)
                    }
                    showUsers()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    private val followers: Unit
        private get() {
            val reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id!!).child("followers")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    idList!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        idList!!.add(snapshot.key)
                    }
                    showUsers()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    private val following: Unit
        private get() {
            val reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id!!).child("following")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    idList!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        idList!!.add(snapshot.key)
                    }
                    showUsers()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    private val likes: Unit
        private get() {
            val reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(id!!)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    idList!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        idList!!.add(snapshot.key)
                    }
                    showUsers()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun showUsers() {
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(
                        User::class.java
                    )
                    for (id in idList!!) {
                        if (user!!.id == id) {
                            userList!!.add(user)
                        }
                    }
                }
                userAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
package com.talos.weaver.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context

import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseUser
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences

import android.content.Intent
import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseError
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.GridLayoutManager
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.talos.weaver.Adapter.MyFotosAdapter
import com.talos.weaver.EditProfileActivity
import com.talos.weaver.FollowersActivity
import com.talos.weaver.Model.Post
import com.talos.weaver.Model.User
import com.talos.weaver.OptionsActivity
import com.talos.weaver.R

import java.util.*

class ProfileFragment constructor() : Fragment() {



    var image_profile: ImageView? = null
    var options: ImageView? = null
    var posts: TextView? = null
    var followers: TextView? = null
    var following: TextView? = null
    var fullname: TextView? = null
    var bio: TextView? = null
    var level: TextView? = null
    var locker: Button? = null
    var username: TextView? = null
    var coinUser: TextView? = null
    var edit_profile: Button? = null
    private var mySaves: MutableList<String?>? = null
    var firebaseUser: FirebaseUser? = null
    var profileid: String? = null
    private var recyclerView: RecyclerView? = null
    private var myFotosAdapter: MyFotosAdapter? = null
    private var postList: MutableList<Post?>? = null
    private var recyclerView_saves: RecyclerView? = null
    private var myFotosAdapter_saves: MyFotosAdapter? = null
    private var postList_saves: MutableList<Post?>? = null
    var togomessage: ImageView? = null
    var my_fotos: TextView? = null
    var saved_fotos: TextView? = null
    var my_writers: TextView? = null


    var fuser: FirebaseUser? = null
    var storageReference: StorageReference? = null
    private var imageUri: Uri? = null
    private var uploadTask: StorageTask<*>? = null


    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val prefs: SharedPreferences =
            requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        profileid = prefs.getString("profileid", "none")
        image_profile = view.findViewById(R.id.image_profile1)

        posts = view.findViewById(R.id.posts)
        followers = view.findViewById(R.id.followers1)
        following = view.findViewById(R.id.following1)
        fullname = view.findViewById(R.id.fullname1)
        bio = view.findViewById(R.id.bio)

        edit_profile = view.findViewById(R.id.edit_profile)
        username = view.findViewById(R.id.username1)
        my_fotos = view.findViewById(R.id.my_fotos)
        my_writers = view.findViewById(R.id.my_writers)
        saved_fotos = view.findViewById(R.id.saved_fotos)
        locker = view.findViewById(R.id.locker);
        locker!!.setOnClickListener {
            val view = View.inflate(activity, R.layout.popupinfoalgo, null)
            val builder = AlertDialog.Builder(activity)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


        }



        options = view.findViewById(R.id.options)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager: LinearLayoutManager = GridLayoutManager(getContext(), 3)
        recyclerView!!.setLayoutManager(mLayoutManager)
        postList = ArrayList()
        myFotosAdapter = getContext()?.let { MyFotosAdapter(it, postList) }
        recyclerView!!.setAdapter(myFotosAdapter)
        recyclerView_saves = view.findViewById(R.id.recycler_view_save)
        recyclerView_saves!!.setHasFixedSize(true)
        val mLayoutManagers: LinearLayoutManager = GridLayoutManager(getContext(), 3)
        recyclerView_saves!!.setLayoutManager(mLayoutManagers)
        postList_saves = ArrayList()
        myFotosAdapter_saves = getContext()?.let { MyFotosAdapter(it, postList_saves) }
        recyclerView_saves!!.setAdapter(myFotosAdapter_saves)
        recyclerView!!.setVisibility(View.VISIBLE)
        recyclerView_saves!!.setVisibility(View.GONE)


        userInfo()
        getFollowers()
        nrPosts
        myFotos()
        mySaves()
        if ((profileid == firebaseUser!!.getUid())) {
            edit_profile!!.setText("Edit Profile")
        } else {
            checkFollow()
            saved_fotos!!.setVisibility(View.GONE)
        }
        edit_profile!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                val btn: String = edit_profile!!.getText().toString()
                if ((btn == "Edit Profile")) {
                    startActivity(Intent(getContext(), EditProfileActivity::class.java))
                } else if ((btn == "follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                        .child(firebaseUser!!.getUid())
                        .child("following").child((profileid)!!).setValue(true)
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                        .child((profileid)!!)
                        .child("followers").child(firebaseUser!!.getUid()).setValue(true)
                    addNotification()
                } else if ((btn == "following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                        .child(firebaseUser!!.getUid())
                        .child("following").child((profileid)!!).removeValue()
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                        .child((profileid)!!)
                        .child("followers").child(firebaseUser!!.getUid()).removeValue()
                }
            }
        })
        options!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                startActivity(Intent(getContext(), OptionsActivity::class.java))
            }
        })
        my_fotos!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                recyclerView!!.setVisibility(View.VISIBLE)
                recyclerView_saves!!.setVisibility(View.GONE)
            }
        })
        my_writers!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                recyclerView!!.setVisibility(View.GONE)
                recyclerView_saves!!.setVisibility(View.VISIBLE)
            }
        })
        saved_fotos!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                recyclerView!!.setVisibility(View.GONE)
                recyclerView_saves!!.setVisibility(View.VISIBLE)
            }
        })


        followers!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                val intent: Intent = Intent(getContext(), FollowersActivity::class.java)
                intent.putExtra("id", profileid)
                intent.putExtra("title", "followers")
                startActivity(intent)
            }
        })
        following!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                val intent: Intent = Intent(getContext(), FollowersActivity::class.java)
                intent.putExtra("id", profileid)
                intent.putExtra("title", "following")
                startActivity(intent)
            }
        })
        return view
    }


    ///////////








    ///////////




    private fun addNotification() {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Notifications").child(
                (profileid)!!
            )
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("userid", firebaseUser!!.getUid())
        hashMap.put("text", "started following you")
        hashMap.put("postid", "")
        hashMap.put("ispost", false)
        reference.push().setValue(hashMap)
    }

    private fun userInfo() {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(
                (profileid)!!
            )
        reference.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (getContext() == null) {
                    return
                }
                val user: User? = dataSnapshot.getValue(
                    User::class.java
                )
                Glide.with((getContext())!!).load(user!!.imageurl).into((image_profile)!!)
                username!!.setText(user.username)
                fullname!!.setText(user.fullname)
                bio!!.setText(user.bio)



            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun checkFollow() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Follow").child(firebaseUser!!.getUid()).child("following")
        reference.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child((profileid)!!).exists()) {
                    edit_profile!!.setText("following")
                } else {
                    edit_profile!!.setText("follow")
                }
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getFollowers() {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Follow").child(
                (profileid)!!
            ).child("followers")
        reference.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                followers!!.setText("" + dataSnapshot.getChildrenCount())
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
        val reference1: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Follow").child(
                (profileid)!!
            ).child("following")
        reference1.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                following!!.setText("" + dataSnapshot.getChildrenCount())
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val nrPosts: Unit
        private get() {
            val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Posts")
            reference.addValueEventListener(object : ValueEventListener {
                public override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var i: Int = 0
                    for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                        val post: Post? = snapshot.getValue(Post::class.java)
                        if ((post!!.publisher == profileid)) {
                            i++
                        }
                    }
                    posts!!.setText("" + i)
                }

                public override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun myFotos() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList!!.clear()
                for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                    val post: Post? = snapshot.getValue(Post::class.java)
                    if ((post!!.publisher == profileid)) {
                        postList!!.add(post)
                    }
                }
                Collections.reverse(postList)
                myFotosAdapter!!.notifyDataSetChanged()
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun mySaves() {
        mySaves = ArrayList()
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Saves").child(
                firebaseUser!!.getUid()
            )
        reference.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                    mySaves!!.add(snapshot.getKey())
                }
                readSaves()
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readSaves() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList_saves!!.clear()
                for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                    val post: Post? = snapshot.getValue(Post::class.java)
                    for (id: String? in mySaves!!) {
                        if ((post!!.postid == id)) {
                            postList_saves!!.add(post)
                        }
                    }
                }
                myFotosAdapter_saves!!.notifyDataSetChanged()
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


}
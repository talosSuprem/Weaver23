package com.talos.weaver.Fragments

import android.app.Dialog
import android.content.Context

import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.ads.interstitial.InterstitialAd
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.appbar.MaterialToolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.android.gms.ads.AdRequest
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*
import com.talos.weaver.*
import com.talos.weaver.Adapter.PostAdapter
import com.talos.weaver.Model.Post
import com.talos.weaver.Model.Story
import com.talos.weaver.Model.User
import com.talos.weaver.R
import de.hdodenhof.circleimageview.CircleImageView


import java.util.ArrayList
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase
import com.talos.weaver.Adapter.AdapterPostsSocial
import com.talos.weaver.Model.ModelPost


class HomeFragment() : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var postAdapter: AdapterPostsSocial? = null
    private var postList: MutableList<ModelPost?>? = null
    var username1: TextView? = null
    var followers: TextView? = null
    var following: TextView? = null


    var reference: DatabaseReference? = null
    


    var name: TextView? = null
    private var recyclerView_story: RecyclerView? = null
    private var storyAdapter: com.talos.weaver.Adapter.StoryAdapter? = null
    private var storyList: MutableList<Story?>? = null
    private var followingList: MutableList<String?>? = null
    var firebaseUser: FirebaseUser? = null
    var profileid: String? = null
    var progress_circular: ProgressBar? = null
    private var dialog: Dialog? = null
    var notification: ImageView? = null
    var messenger: ImageView? = null


    var image_profile: CircleImageView? = null
    private var floatingActionButton: FloatingActionButton? = null
    private var floatingActionButton2: FloatingActionButton? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val prefs = requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        profileid = prefs.getString("id", "none")
        val adRequest = AdRequest.Builder().build()
        val toolbar: MaterialToolbar = view.findViewById(R.id.topAppBar)
        notification = view.findViewById(R.id.notifIcon)
        notification!!.setOnClickListener {
            val intent = Intent(activity, NotificationActivity::class.java)
            startActivity(intent)
        }
        messenger = view.findViewById(R.id.messenger1)
        messenger!!.setOnClickListener {
            val intent1 = Intent(activity, CryptersActivity::class.java)
            startActivity(intent1)
        }
        val drawerLayout: DrawerLayout = view.findViewById(R.id.drawer_layout)


        val navigationView: NavigationView = view.findViewById(R.id.navigation_view)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            drawerLayout.openDrawer(
                GravityCompat.START
            )
        })
        navigationView.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                firebaseUser = FirebaseAuth.getInstance().currentUser
                val prefs = context!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                profileid = prefs.getString("profileid", "none")
                image_profile = view.findViewById(R.id.image_profile)

                name = view.findViewById(R.id.name)
                username1 = view.findViewById(R.id.username_to_nav)
                firebaseUser = FirebaseAuth.getInstance().currentUser
                reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
                reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val user = dataSnapshot.getValue(User::class.java)
                            username1!!.setText(user!!.username)

                        }
                    }

                    override fun onCancelled(@NonNull databaseError: DatabaseError) {}
                })

                val id = item.itemId


                drawerLayout.closeDrawer(GravityCompat.START)
                //a corrigern changer le nonm

                username1 = view.findViewById(R.id.username_to_nav)
                /**
                auth = FirebaseAuth.getInstance()
                currentUser = auth.getCurrentUser()
                val uid: String = currentUser.getUid()

                refUsername = FirebaseDatabase.getInstance().reference.child("users").child(uid)

                refUsername.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val user = dataSnapshot.getValue(User::class.java)
                            navUsername.setText(user.getUsername())
                            navEmail.setText(user.getEmail())
                        }
                    }

                    override fun onCancelled(@NonNull databaseError: DatabaseError) {}
                })**/
                when (id) {


                    R.id.nav_notification -> Toast.makeText(activity, "Home is Clicked", Toast.LENGTH_SHORT)
                        .show()
                    R.id.nav_message -> {
                        val intent = Intent(activity, CryptersActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(activity, "Messages", Toast.LENGTH_SHORT).show()
                    }
                    R.id.shorts -> {
                        val intent0 = Intent(activity, com.talos.weaver.ShortsActivity::class.java)
                        startActivity(intent0)
                        Toast.makeText(activity, "Messages", Toast.LENGTH_SHORT).show()
                    }
                    R.id.cryptube -> {
                        val intent2 = Intent(activity, com.talos.weaver.CryptubeActivity::class.java)
                        startActivity(intent2)
                        Toast.makeText(activity, "Messages", Toast.LENGTH_SHORT).show()
                    }
                    R.id.trash -> {
                        val `in` = Intent(activity, CoinsActivity::class.java)
                        startActivity(`in`)
                        Toast.makeText(activity, "coins info", Toast.LENGTH_SHORT).show()
                    }
                    R.id.settings -> {
                        val `in` = Intent(activity, OptionsActivity::class.java)
                        startActivity(`in`)
                        Toast.makeText(activity, "coins info", Toast.LENGTH_SHORT).show()
                    }
                    R.id.nav_login -> {
                        FirebaseAuth.getInstance().signOut()
                        startActivity(
                            Intent(activity, StartActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        Toast.makeText(activity, "Login is Clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.nav_share -> {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        val Body = "Download this App"
                        val sub = "https://play.google.com/store/apps/details?id=com.talos.weaver"
                        intent.putExtra(Intent.EXTRA_TEXT, Body)
                        intent.putExtra(Intent.EXTRA_TEXT, sub)
                        startActivity(Intent.createChooser(intent, "Share using"))

                        Toast.makeText(activity, "Share this app", Toast.LENGTH_SHORT).show()
                        Toast.makeText(activity, "When your friend create an account", Toast.LENGTH_SHORT).show()
                        Toast.makeText(activity, "You will get points reward", Toast.LENGTH_SHORT).show()
                    }
                    else -> return true
                }
                return true
            }









        })
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        recyclerView!!.setLayoutManager(mLayoutManager)
        postList = ArrayList()
        postAdapter = AdapterPostsSocial((context)!!, postList)
        recyclerView!!.setAdapter(postAdapter)
        recyclerView_story = view.findViewById(R.id.recycler_view_story)
        recyclerView_story!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL, false
        )
        recyclerView_story!!.setLayoutManager(linearLayoutManager)
        storyList = ArrayList()
        storyAdapter = com.talos.weaver.Adapter.StoryAdapter(context, storyList)
        recyclerView_story!!.setAdapter(storyAdapter)
        image_profile = view.findViewById(R.id.image_profile)
        progress_circular = view.findViewById(R.id.progress_circular)
        floatingActionButton2 = view.findViewById(R.id.togowrite)
        floatingActionButton2!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(activity, PublishContentActivity::class.java)
                startActivity(intent)
                Toast.makeText(activity, "post a new text", Toast.LENGTH_LONG).show()

            }
        })


        floatingActionButton = view.findViewById(R.id.togofoto)
        floatingActionButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val intent = Intent(activity, AddPostSocialActivity::class.java)
                startActivity(intent)
                Toast.makeText(activity, "post a new photo", Toast.LENGTH_LONG).show()
            }
        })
        dialog = Dialog((activity)!!)
        checkFollowing()
        return view
    }

    /**
     * private void openDialog() {
     *
     *
     * dialog.setContentView(R.layout.custpopup);
     * dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
     *
     *
     * TextView textView = dialog.findViewById(R.id.txtclose);
     * dialog.show();
     * Button button1 = dialog.findViewById(R.id.getcredits);
     *
     * Button button = dialog.findViewById(R.id.makephoto);
     * button.setOnClickListener(new View.OnClickListener() {
     * @Override
     * public void onClick(View v) {
     * Intent intent = new Intent(getActivity(), PostActivity.class);
     * startActivity(intent);
     * Toast.makeText(getActivity(), "-100 credits", Toast.LENGTH_LONG).show();
     * }
     * });
     *
     *
     * textView.setOnClickListener(new View.OnClickListener() {
     * @Override
     * public void onClick(View v) {
     * dialog.dismiss();
     * }
     * });
     *
     *
     * button1.setOnClickListener(new View.OnClickListener() {
     * @Override
     * public void onClick(View v) {
     *
     * Toast.makeText(getActivity(), "+10 credits", Toast.LENGTH_LONG).show();
     *
     * mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
     * @Override
     * public void onAdDismissedFullScreenContent() {
     * // Called when fullscreen content is dismissed.
     * Log.d("TAG", "The ad was dismissed.");
     * }
     *
     * @Override
     * public void onAdFailedToShowFullScreenContent(AdError adError) {
     * // Called when fullscreen content failed to show.
     * Log.d("TAG", "The ad failed to show.");
     * }
     *
     * @Override
     * public void onAdShowedFullScreenContent() {
     * // Called when fullscreen content is shown.
     * // Make sure to set your reference to null so you don't
     * // show it a second time.
     * mInterstitialAd = null;
     * Log.d("TAG", "The ad was shown.");
     * }
     * });
     *
     * }
     * });
     *
     * } */
    private fun checkFollowing() {
        followingList = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("following")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                followingList!!.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    followingList!!.add(snapshot.key)
                }
                readPosts()
                readStory()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readPosts() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts1")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList!!.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(ModelPost::class.java)
                    for (id: String? in followingList!!) {
                        if ((post!!.uid == id)) {
                            postList!!.add(post)
                        }
                    }
                }
                postAdapter!!.notifyDataSetChanged()
                progress_circular!!.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readStory() {
        val reference = FirebaseDatabase.getInstance().getReference("Story")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val timecurrent = System.currentTimeMillis()
                storyList!!.clear()
                storyList!!.add(
                    Story(
                        "", 0, 0, "",
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )
                )
                for (id: String? in followingList!!) {
                    var countStory = 0
                    var story: Story? = null
                    for (snapshot: DataSnapshot in dataSnapshot.child((id)!!).children) {
                        story = snapshot.getValue(Story::class.java)
                        if (timecurrent > story!!.timestart && timecurrent < story.timeend) {
                            countStory++
                        }
                    }
                    if (countStory > 0) {
                        storyList!!.add(story)
                    }
                }
                storyAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
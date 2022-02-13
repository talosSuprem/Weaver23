package com.talos.weaver.Fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.android.gms.ads.AdRequest
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.talos.weaver.*
import com.talos.weaver.Adapter.AdapterPostsSocial
import com.talos.weaver.Adapter.SectionPagerAdapter
import com.talos.weaver.Model.ModelPost
import com.talos.weaver.Model.Story
import com.talos.weaver.Model.User
import com.talos.weaver.R
import de.hdodenhof.circleimageview.CircleImageView

class Home1Fragment : Fragment() {
    var myFragment: View? = null
    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null



    var username: TextView? = null









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
        // Inflate the layout for this fragment

        myFragment = inflater.inflate(R.layout.fragment_home1, container, false)







        firebaseUser = FirebaseAuth.getInstance().currentUser

        val prefs = requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        profileid = prefs.getString("id", "none")
        val adRequest = AdRequest.Builder().build()
        val toolbar: MaterialToolbar = myFragment!!.findViewById(R.id.topAppBar)
        notification = myFragment!!.findViewById(R.id.notifIcon)
        notification!!.setOnClickListener {
            val intent = Intent(activity, NotificationSocialFragment::class.java)
            startActivity(intent)
        }
        messenger = myFragment!!.findViewById(R.id.messenger1)
        messenger!!.setOnClickListener {
            val intent1 = Intent(activity, CryptersActivity::class.java)
            startActivity(intent1)
        }
        val drawerLayout: DrawerLayout = myFragment!!.findViewById(R.id.drawer_layout)



        val navigationView: NavigationView = myFragment!!.findViewById(R.id.navigation_view)


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
                image_profile = myFragment!!.findViewById(R.id.image_profile)

                name = myFragment!!.findViewById(R.id.name)
                username1 = myFragment!!.findViewById(R.id.username_to_nav)
                firebaseUser = FirebaseAuth.getInstance().currentUser



                val id = item.itemId


                drawerLayout.closeDrawer(GravityCompat.START)
                //a corrigern changer le nonm

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
                    reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val user = dataSnapshot.getValue(User::class.java)
                                username1!!.text = user!!.username

                            }
                        }
                        override fun onCancelled(@NonNull databaseError: DatabaseError) {}
                    })

                username1 = myFragment!!.findViewById(R.id.username_to_nav)

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
                    R.id.news -> {
                        val intent0 = Intent(activity, NewsActivity::class.java)
                        startActivity(intent0)
                        Toast.makeText(activity, "News", Toast.LENGTH_SHORT).show()
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
                    R.id.nav_cgu -> {

                        val i = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://drive.google.com/file/d/1nJZhe_nZk1XvjI4N8zriuNSQVy82Vkf0/view?usp=sharing")
                        )
                        startActivity(i)
                    }
                    else -> return true
                }
                return true
            }





        })


        progress_circular = myFragment!!.findViewById(R.id.progress_circular)
        floatingActionButton2 = myFragment!!.findViewById(R.id.togowrite)
        floatingActionButton2!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(activity, PublishContentActivity::class.java)
                startActivity(intent)
                Toast.makeText(activity, "post a new text", Toast.LENGTH_LONG).show()

            }
        })


        floatingActionButton = myFragment!!.findViewById(R.id.togofoto)
        floatingActionButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val intent = Intent(activity, AddPostSocialActivity::class.java)
                startActivity(intent)
                Toast.makeText(activity, "post a new photo", Toast.LENGTH_LONG).show()
            }
        })












        viewPager = myFragment!!.findViewById(R.id.viewPager2)
        tabLayout = myFragment!!.findViewById(R.id.tabLayout2)







        return myFragment
    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setUpViewPager(viewPager: ViewPager?) {
        val adapter = SectionPagerAdapter(childFragmentManager)
        adapter.addFragment(WeavesFragment(), "Posts")
        adapter.addFragment(SecondFragment(), "best Score")

        viewPager!!.adapter = adapter
    }

}
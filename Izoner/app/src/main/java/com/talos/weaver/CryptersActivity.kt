package com.talos.weaver

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.talos.weaver.FragmentMessagerie.UsersChatFragment
import com.talos.weaver.Model.ModelChat
import com.talos.weaver.Model.User
import de.hdodenhof.circleimageview.CircleImageView

class CryptersActivity : AppCompatActivity() {
    var profile_image: CircleImageView? = null
    var username: TextView? = null
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var returnh:ImageView? = null
    var createGroup:Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypters)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("")
        profile_image = findViewById(R.id.profile_image)
        createGroup = findViewById(R.id.createGroup)
        createGroup!!.setOnClickListener {
            val intent = Intent(this@CryptersActivity, GroupCreateActivity::class.java)
            startActivity(intent)
        }
        returnh = findViewById(R.id.returnhomeicon)
        returnh!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@CryptersActivity, MainActivity::class.java)
            startActivity(intent)

        })
        username = findViewById(R.id.username)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(
                    User::class.java
                )
                username!!.setText(user!!.username)
                if (user.imageurl == "default") {
                    profile_image!!.setImageResource(R.mipmap.ic_launcher)
                } else {

                    //change this
                    Glide.with(applicationContext).load(user.imageurl).into(profile_image!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var unread = 0
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(ModelChat::class.java)
                    if (chat!!.receiver == firebaseUser!!.uid && !chat.isSeen) {
                        unread++
                    }
                }
                if (unread == 0) {
                    viewPagerAdapter.addFragment(ChatListFragment(), "Chats")
                } else {
                    viewPagerAdapter.addFragment(ChatListFragment(), "($unread) Chats")
                }
                viewPagerAdapter.addFragment(UsersChatFragment(), "Users")
                viewPagerAdapter.addFragment(GroupChatsFragment(), "Groups")
                viewPagerAdapter.addFragment(ProfileSocialeFragment(), "Profile")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menuchat, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this@CryptersActivity, OptionsActivity::class.java))
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                // change this code beacuse your app will crash
                startActivity(
                    Intent(
                        this@CryptersActivity,
                        StartActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                return true
            }
        }
        return false
    }

    internal inner class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(
        fm!!
    ) {
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        // Ctrl + O
        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        init {
            fragments = ArrayList()
            titles = ArrayList()
        }
    }

    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }
}
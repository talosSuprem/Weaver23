package com.talos.weaver

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.talos.weaver.FragmentMessagerie.UsersChatFragment
import com.talos.weaver.Fragments.*


class MainActivity : AppCompatActivity() {
    var bottom_navigation: BottomNavigationView? = null
    var selectedfragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        //window.setFlags(
        //    WindowManager.LayoutParams.FLAG_SECURE,
         //   WindowManager.LayoutParams.FLAG_SECURE
       // )




        if(AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkTheme)
        }else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)







        MobileAds.initialize(this)
        bottom_navigation = findViewById(R.id.bottom_navigation)
        bottom_navigation!!.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        val intent = intent.extras
        if (intent != null) {
            val publisher = intent.getString("publisherid")
            val editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit()
            editor.putString("profileid", publisher)
            editor.apply()
            //supportFragmentManager.beginTransaction().replace(
              //  R.id.fragment_container,
                //ProfileFragment()
          //  ).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                Home1Fragment()
            ).commit()
        }
    }

    private val navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_weave -> selectedfragment = Home1Fragment()
                R.id.nav_notification -> selectedfragment = NotificationSocialFragment()
                R.id.nav_search -> selectedfragment = UsersChatFragment()
                R.id.nav_wallet -> selectedfragment = WalletFragment()


                R.id.nav_profile -> {
                    val editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit()
                    editor.putString("profileid", FirebaseAuth.getInstance().currentUser!!.uid)
                    editor.apply()
                    selectedfragment = ProfileSocialeFragment()
                }
            }
            if (selectedfragment != null) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    selectedfragment!!
                ).commit()
            }
            true
        }
}
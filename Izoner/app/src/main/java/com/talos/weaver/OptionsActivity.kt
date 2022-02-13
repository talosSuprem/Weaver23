package com.talos.weaver


import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar

class OptionsActivity : AppCompatActivity() {
    var logout: TextView? = null
    var edit_profile: TextView? = null
    var cgu: TextView? = null
    var aSwitch: Switch? = null
    var textView1: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkTheme)
        }else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        edit_profile = findViewById(R.id.edit_profile)
        cgu = findViewById(R.id.cgu)
        aSwitch = findViewById(R.id.mode)
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            aSwitch!!.isChecked = true;
        }

        fun resset() {
            val intent = Intent(applicationContext, OptionsActivity::class.java)
            startActivity(intent)
            finish()
        }
        aSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                resset()
            }
        }




        edit_profile!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@OptionsActivity, EditProfileActivity::class.java)
            startActivity(intent)
        })
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Options")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        cgu!!.setOnClickListener {
            val i = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://drive.google.com/file/d/1nJZhe_nZk1XvjI4N8zriuNSQVy82Vkf0/view?usp=sharing")
            )
            startActivity(i)
        }
        logout = findViewById(R.id.logout)
        logout!!.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(this@OptionsActivity, StartActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        })
    }
}
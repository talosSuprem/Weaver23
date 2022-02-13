package com.talos.weaver

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    var username: EditText? = null
    var fullname: EditText? = null
    var email: EditText? = null
    var password: EditText? = null
    var register: Button? = null
    var txt_login: TextView? = null
    var togourl: TextView? = null
    var auth: FirebaseAuth? = null



    var reference: DatabaseReference? = null
    var pd: ProgressDialog? = null
    var logo: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        fullname = findViewById(R.id.fullname)
        password = findViewById(R.id.password)
        register = findViewById(R.id.register)
        txt_login = findViewById(R.id.txt_login)
        togourl = findViewById(R.id.rules)
        togourl!!.setOnClickListener {

            val i = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://drive.google.com/file/d/1nJZhe_nZk1XvjI4N8zriuNSQVy82Vkf0/view?usp=sharing")
            )
            startActivity(i)

        }
        logo = findViewById(R.id.logologin2)
        logo!!.setOnClickListener {
            logo!!.animate().apply {
                duration = 1000
                rotationYBy(3600f)
            }.withEndAction {
                logo!!.animate().apply {
                    duration = 10000
                    rotationXBy(3600f)

                }.start()
            }
        }


        auth = FirebaseAuth.getInstance()
        txt_login!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    Login2Activity::class.java
                )
            )
        })
        register!!.setOnClickListener(View.OnClickListener {
            pd = ProgressDialog(this@RegisterActivity)
            pd!!.setMessage("Please wait...")
            pd!!.show()
            val str_username = username!!.getText().toString()
            val str_fullname = fullname!!.getText().toString()
            val str_email = email!!.getText().toString()
            val str_password = password!!.getText().toString()
            if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(
                    str_email
                ) || TextUtils.isEmpty(str_password)
            ) {
                Toast.makeText(
                    this@RegisterActivity,
                    "All fields are required!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (str_password.length < 6) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password must have 6 characters!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                register(str_username, str_fullname, str_email, str_password)
            }
        })
    }


    fun register(username: String, fullname: String, email: String?, password: String?) {
        auth!!.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this@RegisterActivity) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth!!.currentUser
                    val userID = firebaseUser!!.uid
                    val email = firebaseUser!!.email

                    reference =
                        FirebaseDatabase.getInstance().reference.child("Users").child(userID)
                    val date = DateFormat.getDateInstance().format(Date())
                    val map = HashMap<String, Any>()
                    map["id"] = userID
                    map["username"] = username.toLowerCase()
                    map["fullname"] = fullname
                    map["email"] = email.toString()
                    map["imageurl"] =
                        "https://firebasestorage.googleapis.com/v0/b/instagramtest-fcbef.appspot.com/o/placeholder.png?alt=media&token=b09b809d-a5f8-499b-9563-5252262e9a49"
                    map["bio"] = ""
                    map["onlineStatus"] = "online"
                    map["typingTo"] = "noOne"
                    map["coinUser"] = 1
                    map["dislikeUser"] = 0
                    map["likeUser"] = 0
                    map["strikes"] = 0
                    map["joined"] = date
                    map["levelUser"] = 0
                    map["tokenCounter"] = 1
                    map["admin"] = false
                    map["compterPts"] = 1000
                    map["status"] = "offline"
                    map["coinCount"] = 0
                    map["listToken"] = ""
                    map["cover"] = ""
                    map["search"] = username.toLowerCase()
                    reference!!.setValue(map).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            pd!!.dismiss()
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            val database = FirebaseDatabase.getInstance()

                            val myref = database.getReference("")
                        }
                    }
                } else {
                    pd!!.dismiss()
                    Toast.makeText(
                        this@RegisterActivity,
                        "You can't register with this email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
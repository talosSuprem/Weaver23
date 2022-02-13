package com.talos.weaver

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.*

class StartActivity : AppCompatActivity() {

    //private val RC_SIGN_IN = 100
    var mGoogleSignInClient: GoogleSignInClient? = null
    var mGoogleLoginBtn: SignInButton? = null

    private var mAuth: FirebaseAuth? = null

    var login: Button? = null
    var register: Button? = null
    var firebaseUser: FirebaseUser? = null
    var logo: ImageView? = null
    override fun onStart() {
        super.onStart()
        MobileAds.initialize(this)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        //check if user is null
        if (firebaseUser != null) {
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()



        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()

        mGoogleLoginBtn = findViewById(R.id.googleLoginBtn)
        login = findViewById(R.id.login)
        register = findViewById(R.id.register)
        logo = findViewById(R.id.logologin2)




        logo!!.setOnClickListener {
            logo!!.animate().apply {
                duration = 100
                rotationYBy(36f)

            }.withEndAction {
                logo!!.animate().apply {
                    duration = 10000
                    rotationXBy(3600f)

                }.start()
            }
        }
        login!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@StartActivity,
                    Login2Activity::class.java
                )
            )
        })
        register!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@StartActivity,
                    RegisterActivity::class.java
                )
            )
        })





    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                    if (task.result.additionalUserInfo!!.isNewUser) {

                        val email = user!!.email
                        val uid = user.uid
                        val date = DateFormat.getDateInstance().format(Date())
                        val map = HashMap<Any, Any?>()
                        map["id"] = uid
                        map["username"] = "username"
                        map["fullname"] = "fullname"
                        map["email"] = email
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
                        map["search"] = "username.toLowerCase()"
                        val database = FirebaseDatabase.getInstance()
                        val reference = database.getReference("Users")
                        reference.child(uid).setValue(map)
                    }
                    startActivity(Intent(this@StartActivity, MainActivity::class.java))
                    finish()
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@StartActivity, "Login Failed...", Toast.LENGTH_SHORT)
                        .show()
                    //updateUI(null);
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@StartActivity,
                    "" + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }



}
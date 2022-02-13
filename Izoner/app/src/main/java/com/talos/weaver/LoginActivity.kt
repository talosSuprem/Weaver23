package com.talos.weaver


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import java.text.DateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 100



    var email: EditText? = null
    var password: EditText? = null
    var login: Button? = null
    var txt_signup: TextView? = null
    var txt_forgot: TextView? = null
    var auth: FirebaseAuth? = null
    var logo: ImageView? = null
    var mGoogleLoginBtn: SignInButton? = null
    var googleSignInClient: GoogleSignInClient? = null


    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("77585142121-6v4b30hr2ehubugmo66ls6bn4rjhii40.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)



        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        txt_signup = findViewById(R.id.txt_signup)
        txt_forgot = findViewById(R.id.txt_forgot)
        mGoogleLoginBtn = findViewById(R.id.googleLoginBtn)
        auth = FirebaseAuth.getInstance()

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
        txt_signup!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        })
        txt_forgot!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    com.talos.weaver.ResetPasswordActivity::class.java
                )
            )
        })

        fun signIn() {
            val signInIntent = googleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }



        fun firebaseAuthWithGoogle(idToken: String, username: String.Companion, fullname: String.Companion) {

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        val user = auth!!.currentUser

                        val firebaseUser = auth!!.currentUser
                        val userID = firebaseUser!!.uid


                        if (task.result.additionalUserInfo!!.isNewUser) {
                            val date = DateFormat.getDateInstance().format(Date())
                            val map = HashMap<String, Any>()
                            map["id"] = userID
                            map["username"] = username
                            map["fullname"] = fullname
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
                            map["bigbrother"] = false
                            map["compterPts"] = 100
                            map["imageURL"] = "default"
                            map["status"] = "offline"
                            map["coinCount"] =  0
                            map["search"] = username


                            val database = FirebaseDatabase.getInstance()

                            val reference = database.getReference("Users")
                            reference.child(userID)

                        }


                        val intent =
                            Intent(this@LoginActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        //updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this@LoginActivity, "Failed...", Toast.LENGTH_SHORT)
                            .show()
                        //updateUI(null)
                    }
                }.addOnFailureListener {

                }


        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!


                    val username = String
                    val fullname = String
                    firebaseAuthWithGoogle(account.idToken!!, username, fullname)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately


                }
            }
        }

        mGoogleLoginBtn!!.setOnClickListener {
            signIn()

        }
        login!!.setOnClickListener(View.OnClickListener {
            val pd = ProgressDialog(this@LoginActivity)
            pd.setMessage("Please wait...")
            pd.show()
            val str_email = email!!.getText().toString()
            val str_password = password!!.getText().toString()
            if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                Toast.makeText(this@LoginActivity, "All fields are required!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                auth!!.signInWithEmailAndPassword(str_email, str_password)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            val reference = FirebaseDatabase.getInstance().reference.child("Users")
                                .child(auth!!.currentUser!!.uid)
                            reference.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    pd.dismiss()
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    pd.dismiss()
                                }
                            })
                        } else {
                            pd.dismiss()
                            Toast.makeText(
                                this@LoginActivity,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        })







    }


}
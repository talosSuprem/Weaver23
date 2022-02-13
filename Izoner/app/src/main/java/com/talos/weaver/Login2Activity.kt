package com.talos.weaver

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import android.widget.EditText
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import android.app.ProgressDialog
import android.os.Bundle
import com.talos.weaver.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import android.content.Intent
import android.util.Patterns
import android.view.View
import android.widget.Button
import com.talos.weaver.Login2Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.talos.weaver.MainActivity
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import java.text.DateFormat
import java.util.*

class Login2Activity : AppCompatActivity() {
    var mGoogleSignInClient: GoogleSignInClient? = null
    var mEmailEt: EditText? = null
    var mPasswordEt: EditText? = null
    var mLoginBtn: Button? = null
    var mGoogleLoginBtn: SignInButton? = null
    private var mAuth: FirebaseAuth? = null
    var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()
        mEmailEt = findViewById(R.id.email)
        mPasswordEt = findViewById(R.id.password)
        mLoginBtn = findViewById(R.id.login)
        mGoogleLoginBtn = findViewById(R.id.googleLoginBtn)
        mLoginBtn!!.setOnClickListener(View.OnClickListener {
            val email = mEmailEt!!.getText().toString()
            val passw = mPasswordEt!!.getText().toString().trim { it <= ' ' }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailEt!!.setError("Invalid Email")
                mEmailEt!!.setFocusable(true)
            } else {
                loginUser(email, passw)
            }
        })
        pd = ProgressDialog(this)
        mGoogleLoginBtn!!.setOnClickListener(View.OnClickListener {
            val signInIntent = mGoogleSignInClient!!.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        })
        pd!!.setMessage("Logging In...")
    }

    private fun loginUser(email: String, passw: String) {
        pd!!.show()
        mAuth!!.signInWithEmailAndPassword(email, passw)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    pd!!.dismiss()
                    val user = mAuth!!.currentUser
                    startActivity(Intent(this@Login2Activity, MainActivity::class.java))
                    finish()
                } else {
                    pd!!.dismiss()
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this@Login2Activity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@Login2Activity,
                    "" + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
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
                    startActivity(Intent(this@Login2Activity, MainActivity::class.java))
                    finish()
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@Login2Activity, "Login Failed...", Toast.LENGTH_SHORT)
                        .show()
                    //updateUI(null);
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@Login2Activity,
                    "" + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }
}
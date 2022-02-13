package com.talos.weaver


import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser
import android.os.Bundle
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseError
import com.rengwuxian.materialedittext.MaterialEditText
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import android.webkit.MimeTypeMap
import android.app.ProgressDialog
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import com.talos.weaver.Model.User

import java.util.HashMap

class EditProfileActivity : AppCompatActivity() {
    var close: ImageView? = null
    var image_profile: ImageView? = null
    var save: TextView? = null
    var tv_change: TextView? = null
    var fullname: MaterialEditText? = null
    var username: MaterialEditText? = null
    var bio: MaterialEditText? = null
    var firebaseUser: FirebaseUser? = null
    private var mImageUri: Uri? = null
    private var uploadTask: StorageTask<*>? = null
    var storageRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        close = findViewById(R.id.close)
        image_profile = findViewById(R.id.image_profile)
        save = findViewById(R.id.save)
        tv_change = findViewById(R.id.tv_change)
        fullname = findViewById(R.id.fullname)
        username = findViewById(R.id.username)
        bio = findViewById(R.id.bio)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        storageRef = FirebaseStorage.getInstance().getReference("uploads")
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(
            firebaseUser!!.uid
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(
                    User::class.java
                )
                fullname!!.setText(user!!.fullname)
                username!!.setText(user.username)
                bio!!.setText(user.bio)
                Glide.with(applicationContext).load(user.imageurl).into(image_profile!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        close!!.setOnClickListener(View.OnClickListener { finish() })
        save!!.setOnClickListener(View.OnClickListener {
            updateProfile(
                fullname!!.getText().toString(),
                username!!.getText().toString(),
                bio!!.getText().toString()
            )
        })
        tv_change!!.setOnClickListener(View.OnClickListener {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this@EditProfileActivity)
        })
        image_profile!!.setOnClickListener(View.OnClickListener {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this@EditProfileActivity)
        })
    }

    private fun updateProfile(fullname: String, username: String, bio: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(
            firebaseUser!!.uid
        )
        val map = HashMap<String, Any>()
        map["fullname"] = fullname
        map["username"] = username
        map["bio"] = bio
        reference.updateChildren(map)
        Toast.makeText(this@EditProfileActivity, "Successfully updated!", Toast.LENGTH_SHORT).show()
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun uploadImage() {
        val pd = ProgressDialog(this)
        pd.setMessage("Uploading")
        pd.show()
        if (mImageUri != null) {
            val fileReference = storageRef!!.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(mImageUri!!)
            )
            uploadTask = fileReference.putFile(mImageUri!!)
            (uploadTask as UploadTask).continueWithTask(Continuation<UploadTask.TaskSnapshot?, Task<Uri>> { task ->
                if (task.isSuccessful) {
                    throw task.exception!!
                }
                fileReference.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result!!
                    val miUrlOk = downloadUri.toString()
                    val reference = FirebaseDatabase.getInstance().getReference("Users").child(
                        firebaseUser!!.uid
                    )
                    val map1 = HashMap<String, Any>()
                    map1["imageurl"] = "" + miUrlOk
                    reference.updateChildren(map1)
                    pd.dismiss()
                } else {
                    Toast.makeText(this@EditProfileActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@EditProfileActivity,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this@EditProfileActivity, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            mImageUri = result.uri
            uploadImage()
        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show()
        }
    }
}
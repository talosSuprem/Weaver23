package com.talos.weaver

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.os.Bundle
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import android.webkit.MimeTypeMap
import android.app.ProgressDialog
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import java.text.DateFormat
import java.util.*

class PostActivity : AppCompatActivity() {
    private var mImageUri: Uri? = null
    var miUrlOk = ""
    private var uploadTask: StorageTask<*>? = null
    var storageRef: StorageReference? = null
    var close: ImageView? = null
    var image_added: ImageView? = null
    var post: TextView? = null
    var dateofpost: TextView? = null
    var description: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        close = findViewById(R.id.close)
        image_added = findViewById(R.id.image_added)
        post = findViewById(R.id.post)
        dateofpost = findViewById(R.id.dateofpost)
        description = findViewById(R.id.description)
        storageRef = FirebaseStorage.getInstance().getReference("posts")
        close!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@PostActivity, MainActivity::class.java))
            finish()
        })
        post!!.setOnClickListener(View.OnClickListener { uploadImage_10() })
        CropImage.activity()
            .setAspectRatio(1, 1)
            .start(this@PostActivity)
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun uploadImage_10() {
        val pd = ProgressDialog(this)
        pd.setMessage("Posting")
        pd.show()
        if (mImageUri != null) {
            val fileReference = storageRef!!.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(mImageUri!!)
            )
            uploadTask = fileReference.putFile(mImageUri!!)
            (uploadTask as UploadTask).continueWithTask(Continuation<UploadTask.TaskSnapshot?, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                fileReference.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result!!
                    miUrlOk = downloadUri.toString()
                    val reference = FirebaseDatabase.getInstance().getReference("Posts")
                    val date = DateFormat.getDateInstance().format(Date())
                    val postid = reference.push().key
                    val hashMap = HashMap<String, Any?>()
                    hashMap["postid"] = postid
                    hashMap["dateofPost"] = date
                    hashMap["postimage"] = miUrlOk
                    hashMap["description"] = description!!.text.toString()
                    hashMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                    reference.child(postid!!).setValue(hashMap)
                    pd.dismiss()
                    startActivity(Intent(this@PostActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@PostActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@PostActivity,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this@PostActivity, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            mImageUri = result.uri
            image_added!!.setImageURI(mImageUri)
        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@PostActivity, MainActivity::class.java))
            finish()
        }
    }
}
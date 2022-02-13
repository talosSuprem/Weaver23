package com.talos.weaver

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.StorageReference
import android.os.Bundle
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import android.webkit.MimeTypeMap
import android.app.ProgressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.UploadTask
import java.util.HashMap

class AddStoryActivity : AppCompatActivity() {
    private var mImageUri: Uri? = null
    var miUrlOk = ""
    private var uploadTask: StorageTask<*>? = null
    var storageRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)
        storageRef = FirebaseStorage.getInstance().getReference("story")
        CropImage.activity()
            .setAspectRatio(9, 16)
            .start(this@AddStoryActivity)
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
                if (task.isSuccessful) {
                    throw task.exception!!
                }
                fileReference.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    miUrlOk = downloadUri.toString()
                    val myid = FirebaseAuth.getInstance().currentUser!!.uid
                    val reference = FirebaseDatabase.getInstance().getReference("Story")
                        .child(myid)
                    val storyid = reference.push().key
                    val timeend = System.currentTimeMillis() + 86400000 // 1 day later
                    val hashMap = HashMap<String, Any?>()
                    hashMap["imageurl"] = miUrlOk
                    hashMap["timestart"] = ServerValue.TIMESTAMP
                    hashMap["timeend"] = timeend
                    hashMap["storyid"] = storyid
                    hashMap["userid"] = myid
                    reference.child(storyid!!).setValue(hashMap)
                    pd.dismiss()
                    finish()
                } else {
                    Toast.makeText(this@AddStoryActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@AddStoryActivity,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this@AddStoryActivity, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            mImageUri = result.uri
            uploadImage_10()
        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@AddStoryActivity, MainActivity::class.java))
            finish()
        }
    }
}
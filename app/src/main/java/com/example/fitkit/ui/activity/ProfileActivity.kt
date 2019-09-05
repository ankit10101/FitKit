package com.example.fitkit.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.fitkit.R
import com.example.fitkit.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser

    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        storage = FirebaseStorage.getInstance();
        storageReference = storage!!.reference;

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        throw databaseError.toException();
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val gender = dataSnapshot.child("gender").getValue(String::class.java)
                        val height = dataSnapshot.child("height").getValue(String::class.java)
                        val weight = dataSnapshot.child("weight").getValue(String::class.java)
                        updateUI(weight, height, gender)
                    }
                })
        }

        ivProfilePic.setOnClickListener {
            chooseImage()
        }

        btnSaveEdit.setOnClickListener {
            val email = etEmailSignUp.text.toString()
            val name = etName.text.toString()
            val gender = etGender.text.toString()
            val height = etHeight.text.toString()
            val weight = etWeight.text.toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(baseContext, "Please Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name != user?.displayName) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.e("TAG", "User profile updated.")
                        }
                    }
            }
            if (email != user?.email) {
                user?.updateEmail(email)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.e("TAG", "User email address updated.")
                        }
                    }
            }
            val userDb = User(email, name, gender, height, weight)
            FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(userDb)
            Toast.makeText(this, "Saved user details", Toast.LENGTH_SHORT).show()
            uploadImage()
        }
    }

    fun updateUI(weight: String?, height: String?, gender: String?) {
        runOnUiThread {
            etEmailSignUp.setText(user?.email, TextView.BufferType.EDITABLE)
            etGender.setText(gender, TextView.BufferType.EDITABLE)
            etName.setText(user?.displayName, TextView.BufferType.EDITABLE)
            etHeight.setText(height, TextView.BufferType.EDITABLE)
            etWeight.setText(weight, TextView.BufferType.EDITABLE)

            storageReference!!.child("images/" + user?.uid.toString()).downloadUrl
                .addOnSuccessListener { uri ->
                    Picasso.get().load(uri.toString()).into(ivProfilePic)
                    tvWait.visibility = View.INVISIBLE
                    progress_circular.visibility = View.INVISIBLE

                }
                .addOnFailureListener {
                }
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
//                ivProfilePic.setImageBitmap(bitmap)
                if (filePath != null) {
                    Picasso.get().load(filePath).into(ivProfilePic)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
//        uploadImage()
    }

    private fun uploadImage() {
        if (filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val ref = storageReference!!.child("images/" + user?.uid.toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->
                    //calculating progress percentage
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")
                }
        }
    }

}

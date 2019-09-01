package com.example.fitkit.ui.activity

import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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
//            user?.updatePassword(password)
//                ?.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.e("TAG", "User password updated.")
//                    }
//                }
            val userDb: User = User(email, name, gender, height, weight)
            FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(userDb)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        throw databaseError.toException();
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val email = dataSnapshot.child("email").getValue(String::class.java)
                        val gender = dataSnapshot.child("gender").getValue(String::class.java)
                        val height = dataSnapshot.child("height").getValue(String::class.java)
                        val name = dataSnapshot.child("name").getValue(String::class.java)
                        val weight = dataSnapshot.child("weight").getValue(String::class.java)
                        updateUI(weight, height, gender)
                    }
                })
        }
    }

    fun updateUI(weight: String?, height: String?, gender: String?) {
        runOnUiThread {
            etEmailSignUp.setText(user?.email, TextView.BufferType.EDITABLE)
            etGender.setText(gender, TextView.BufferType.EDITABLE)
            etName.setText(user?.displayName, TextView.BufferType.EDITABLE)
            etHeight.setText(height, TextView.BufferType.EDITABLE)
            etWeight.setText(weight, TextView.BufferType.EDITABLE)
            tvWait.visibility = View.INVISIBLE
            progress_circular.visibility = View.INVISIBLE
        }
    }
}

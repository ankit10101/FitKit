package com.example.fitkit.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.fitkit.R
import com.example.fitkit.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(baseContext, "Please Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(baseContext, "Please Enter PassWord", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Login Failed!!", Toast.LENGTH_SHORT).show()
                    it.printStackTrace()
                }
        }

        tvSignIn.setOnClickListener {
            tvSignIn.visibility = View.INVISIBLE
            btnRegister.visibility = View.INVISIBLE
            btnLogin.visibility = View.VISIBLE
        }

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(baseContext, "Please Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(baseContext, "Please Enter PassWord", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    //                    val user = User(email, name, gender, height, weight)
//                    FirebaseDatabase.getInstance().getReference("Users")
//                        .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(user)
                    Toast.makeText(this, "Registration Completed", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                    it.printStackTrace()
                }
        }
    }
}

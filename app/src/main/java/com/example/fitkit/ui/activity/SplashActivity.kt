package com.example.fitkit.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.fitkit.R

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar!!.hide()
        Handler().postDelayed({
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }
}

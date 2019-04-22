package com.example.fitkit

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.fitkit.fragments.BmiCalculatorFragment
import com.example.fitkit.fragments.SportsNewsFragment
import com.example.fitkit.fragments.StepCounterFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        lateinit var fragment: Fragment
        when (item.itemId) {
            R.id.navigation_stepCounter ->
                fragment = StepCounterFragment()
            R.id.navigation_bmiCalculator ->
                fragment = BmiCalculatorFragment()
            R.id.navigation_sportsNews ->
                fragment = SportsNewsFragment()
        }
        return loadFragment(fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(StepCounterFragment())
        navigation.setOnNavigationItemSelectedListener(this)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        fragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
            return true
        } ?: run {
            return false
        }
    }
}
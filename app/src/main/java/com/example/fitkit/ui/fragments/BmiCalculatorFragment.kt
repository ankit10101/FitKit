package com.example.fitkit.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitkit.R
import kotlinx.android.synthetic.main.fragment_bmi_calculator.*
import java.text.DecimalFormat


class BmiCalculatorFragment : Fragment() {
    companion object {
        var bmiCalculatorFragment: BmiCalculatorFragment? = null
        //For getting the same unique instance of the fragment on every call;
        //i.e. Singleton pattern
        fun getInstance(): Fragment? {

            if (bmiCalculatorFragment == null) {
                bmiCalculatorFragment = BmiCalculatorFragment()
            }
            return bmiCalculatorFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bmi_calculator, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonCalculate.setOnClickListener {
            val twoDecimalPlaces = DecimalFormat(".##")
            val kg = java.lang.Double.parseDouble(inputKg.text.toString())
            val m = java.lang.Double.parseDouble(inputM.text.toString())
            showBMI.text = "BMI = " + twoDecimalPlaces.format(computeBMI(kg, m)).toString()
            showResult.text = getCategory(computeBMI(kg, m))
            ivBMI.visibility = View.VISIBLE
        }
    }

    private fun getCategory(result: Double): String {
        val category: String
        if (result < 15) {
            category = "very severely underweight"
        } else if (result in 15.0..16.0) {
            category = "severely underweight"
        } else if (result > 16 && result <= 18.5) {
            category = "underweight"
        } else if (result > 18.5 && result <= 25) {
            category = "normal (healthy weight)"
        } else if (result > 25 && result <= 30) {
            category = "overweight"
        } else if (result > 30 && result <= 35) {
            category = "moderately obese"
        } else if (result > 35 && result <= 40) {
            category = "severely obese"
        } else {
            category = "very severely obese"
        }
        return category
    }

    private fun computeBMI(kg: Double, m: Double): Double {
        return kg / Math.pow(m, 2.0)
    }
}

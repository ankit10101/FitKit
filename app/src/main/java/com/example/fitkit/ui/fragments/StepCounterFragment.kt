package com.example.fitkit.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.fitkit.R
import kotlinx.android.synthetic.main.fragment_step_counter.*
import java.text.DecimalFormat


class StepCounterFragment : Fragment(), SensorEventListener {
    companion object {
        var stepCounterFragment: StepCounterFragment? = null
        //For getting the same unique instance of the fragment on every call;
        //i.e. Singleton pattern
        fun getInstance(): Fragment? {

            if (stepCounterFragment == null) {
                stepCounterFragment = StepCounterFragment()
            }
            return stepCounterFragment
        }
    }

    var running = false
    var sensorManager: SensorManager? = null
    var count = 1
    var x: Float = 0F

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_step_counter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onResume() {
        super.onResume()
        running = true
        val stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepsSensor == null) {
            Toast.makeText(requireContext(), "No Step Counter Sensor !", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        val twoDecimalPlaces = DecimalFormat(".##")
        if (count == 1) {
            x = event!!.values[0]
        }
        if (running) {
            tvStepsValue.text = "" + twoDecimalPlaces.format((event!!.values[0] - x))
            tvDistance.text = "" + twoDecimalPlaces.format(((event.values[0] - x) * 0.78))
            count++
        }
    }


}

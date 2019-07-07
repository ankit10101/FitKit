package com.example.fitkit.ui.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitkit.R
import com.example.fitkit.StepCountingService
import kotlinx.android.synthetic.main.fragment_step_counter.*

class StepCounterFragment : Fragment(){

    lateinit var countedStep: String
    lateinit var DetectedStep: String

    var isServiceStopped: Boolean = false

    private var intent: Intent? = null
    private val TAG = "SensorEvent"


    companion object {
        private var stepCounterFragment: StepCounterFragment? = null
        //For getting the same unique instance of the fragment on every call;
        //i.e. Singleton pattern
        fun getInstance(): Fragment? {

            if (stepCounterFragment == null) {
                stepCounterFragment = StepCounterFragment()
            }
            return stepCounterFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_step_counter, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ___ instantiate intent ___ \\
        //  Instantiate the intent declared globally - which will be passed to startService and stopService.
        intent = Intent(requireContext(),StepCountingService::class.java)
        init() // Call view initialisation method.
    }

    // Initialise views.
    fun init() {

        isServiceStopped = true // variable for managing service state - required to invoke "stopService" only once to avoid Exception.

        // ________________ Service Management (Start & Stop Service). ________________ //
        // ___ start Service & register broadcast receiver ___ \\
        startServiceBtn.setOnClickListener {
            // start Service.
            activity?.startService(Intent(requireContext(), StepCountingService::class.java))
            // register our BroadcastReceiver by passing in an IntentFilter. * identifying the message that is broadcasted by using static string "BROADCAST_ACTION".
            activity?.registerReceiver(broadcastReceiver, IntentFilter(StepCountingService.BROADCAST_ACTION))
            isServiceStopped = false
        }

        // ___ unregister receiver & stop service ___ \\
        stopServiceBtn.setOnClickListener {
            if (!isServiceStopped) {
                // call unregisterReceiver - to stop listening for broadcasts.
                activity?.unregisterReceiver(broadcastReceiver)
                // stop Service.
                activity?.stopService(Intent(requireContext(), StepCountingService::class.java))
                isServiceStopped = true
            }
        }
    }


    // --------------------------------------------------------------------------- \\
    // ___ create Broadcast Receiver ___ \\
    // create a BroadcastReceiver - to receive the message that is going to be broadcast from the Service
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // call updateUI passing in our intent which is holding the data to display.
            updateViews(intent)
        }
    }
    // ___________________________________________________________________________ \\


    // --------------------------------------------------------------------------- \\
    // ___ retrieve data from intent & set data to textviews __ \\
    @SuppressLint("SetTextI18n")
    private fun updateViews(intent: Intent) {
        // retrieve data out of the intent.
        countedStep = intent.getStringExtra("Counted_Step")
        DetectedStep = intent.getStringExtra("Detected_Step")
        Log.d(TAG, countedStep)
        Log.d(TAG, DetectedStep)
        if(stepCountTxV != null)
        stepCountTxV.text = "$countedStep Steps Counted"
        //        stepDetectTxV.setText("Steps Detected = " + String.valueOf(DetectedStep) + '"');

    }
}
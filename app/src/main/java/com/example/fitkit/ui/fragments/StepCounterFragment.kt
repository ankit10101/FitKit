package com.example.fitkit.ui.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.text.DecimalFormat
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitkit.R
import com.example.fitkit.StepCountingService
import kotlinx.android.synthetic.main.fragment_step_counter.*

class StepCounterFragment : Fragment() {

    private lateinit var countedStep: String

    private var isServiceStopped: Boolean = false

    private var intent: Intent? = null

    companion object {
        private var stepCounterFragment: StepCounterFragment? = null
        fun getInstance(): Fragment? {
            if (stepCounterFragment == null) {
                stepCounterFragment = StepCounterFragment()
            }
            return stepCounterFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_step_counter, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intent = Intent(requireContext(), StepCountingService::class.java)
        init()
    }

    // Initialise views.
    private fun init() {

        isServiceStopped =
            true // variable for managing service state - required to invoke "stopService" only once to avoid Exception.

        // ________________ Service Management (Start & Stop Service). ________________ //
        // ___ start Service & register broadcast receiver ___ \\
        btnStart.setOnClickListener {
            // start Service.
            activity?.startService(Intent(requireContext(), StepCountingService::class.java))
            // register our BroadcastReceiver by passing in an IntentFilter. * identifying the message that is broadcasted by using static string "BROADCAST_ACTION".
            activity?.registerReceiver(
                broadcastReceiver,
                IntentFilter(StepCountingService.BROADCAST_ACTION)
            )
            isServiceStopped = false
            btnStart.visibility = View.GONE
            btnStop.visibility = View.VISIBLE
        }

        // ___ unregister receiver & stop service ___ \\
        btnStop.setOnClickListener {
            if (!isServiceStopped) {
                // call unregisterReceiver - to stop listening for broadcasts.
                activity?.unregisterReceiver(broadcastReceiver)
                // stop Service.
                activity?.stopService(Intent(requireContext(), StepCountingService::class.java))
                isServiceStopped = true
                btnStart.visibility = View.VISIBLE
                btnStop.visibility = View.GONE
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

    // ___ retrieve data from intent & set data to text views __ \\
    @SuppressLint("SetTextI18n")
    private fun updateViews(intent: Intent) {
        // retrieve data out of the intent.
        val twoDecimalPlaces = DecimalFormat(".##")
        countedStep = intent.getStringExtra("Counted_Step")
        Log.d("TAG", countedStep)
        if (tvStepsValue != null)
            tvStepsValue.text = countedStep
        tvDistance.text = twoDecimalPlaces.format(((countedStep.toDouble()) * 0.78))

    }
}
package com.example.fitkit

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log

class StepCountingService : Service(), SensorEventListener {

    companion object {
        const val TAG = "StepService"
        const val BROADCAST_ACTION = "com.example.fitkit.mybroadcast"
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var stepCounterSensor: Sensor


    private var stepCounter: Int = 0
    private var newStepCounter: Int = 0

    var serviceStopped: Boolean = false // Boolean variable to control the repeating timer.

    private lateinit var notificationManager: NotificationManager

    private lateinit var intent: Intent

    // Create a handler - that will be used to broadcast our data, after a specified amount of time.
    private val handler = Handler()
    // Declare and initialise counter - for keeping a record of how many times the service carried out updates.
    var counter = 0

    override fun onCreate() {
        super.onCreate()
        // ___ (2) create/instantiate intent. ___ \\
        // Instantiate the intent declared globally, and pass "BROADCAST_ACTION" to the constructor of the intent.
        intent = Intent(BROADCAST_ACTION)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.v("Service", "Start")

        showNotification()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        }
        sensorManager.registerListener(this, stepCounterSensor, 0)

        stepCounter = 0
        newStepCounter = 0

        serviceStopped = false

        // --------------------------------------------------------------------------- \\
        // ___ (3) start handler ___ \\
        /////if (serviceStopped == false) {
        // remove any existing callbacks to the handler
        handler.removeCallbacks(updateBroadcastData)
        // call our handler with or without delay.
        handler.post(updateBroadcastData) // 0 seconds
        /////}
        // ___________________________________________________________________________ \\

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("Service", "Stop")

        serviceStopped = true

        dismissNotification()
    }

    override fun onSensorChanged(event: SensorEvent) {
        // STEP_COUNTER Sensor.
        // *** Step Counting does not restart until the device is restarted - therefore, an algorithm for restarting the counting must be implemented.
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val countSteps = event.values[0].toInt()

            // -The long way of starting a new step counting sequence.-
            /**
             * int tempStepCount = countSteps;
             * int initialStepCount = countSteps - tempStepCount; // Nullify step count - so that the step counting can restart.
             * currentStepCount += initialStepCount; // This variable will be initialised with (0), and will be incremented by itself for every Sensor step counted.
             * stepCountTxV.setText(String.valueOf(currentStepCount));
             * currentStepCount++; // Increment variable by 1 - so that the variable can increase for every Step_Counter event.
             */

            // -The efficient way of starting a new step counting sequence.-
            if (stepCounter == 0) { // If the stepCounter is in its initial value, then...
                stepCounter =
                    event.values[0].toInt() // Assign the StepCounter Sensor event value to it.
            }
            newStepCounter =
                countSteps - stepCounter // By subtracting the stepCounter variable from the Sensor event value - We start a new counting sequence from 0. Where the Sensor event value will increase, and stepCounter value will be only initialised once.
        }

        Log.v("Service Counter", newStepCounter.toString())

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    private fun showNotification() {
        val notificationBuilder = NotificationCompat.Builder(this)
        notificationBuilder.setContentTitle("FitKit")
        notificationBuilder.setContentText("Step Counter is running in the background.")
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationBuilder.color = Color.parseColor("#6600cc")
        val colorLED = Color.argb(255, 0, 255, 0)
        notificationBuilder.setLights(colorLED, 500, 500)
        // To  make sure that the Notification LED is triggered.
        notificationBuilder.priority = Notification.PRIORITY_HIGH
        notificationBuilder.setOngoing(true)
        ;
        val resultPendingIntent = PendingIntent.getActivity(this, 0, Intent(), 0)
        notificationBuilder.setContentIntent(resultPendingIntent)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        notificationManager.notify(0, notificationBuilder.build())

    }

    private fun dismissNotification() {
        notificationManager.cancel(0)
    }

    private val updateBroadcastData = object : Runnable {
        override fun run() {
            if (!serviceStopped) {
                // Only allow the repeating timer while service is running
                // (once service is stopped the flag state will change and the code inside the conditional
                // statement here will not execute).
                // Call the method that broadcasts the data to the Activity..
                broadcastSensorValue()
                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun broadcastSensorValue() {
        intent.putExtra("Counted_Step_Int", newStepCounter)
        intent.putExtra("Counted_Step", newStepCounter.toString())
        sendBroadcast(intent)
    }

}
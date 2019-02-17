package com.lynx.wind.dev

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.properties.Delegates


class ShakeDetector : SensorEventListener {

    private var DEFAULT_THRESHOLD_ACCELERATION = 2.0f
    private var DEFAULT_THRESHOLD_SHAKE_NUMBER = 3
    private val INTERVAL = 200

    private var context: Context? = null
    private lateinit var listener: ShakeListener
    private val manager by lazy { context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private var accelerometer: Sensor? = null
    private var accels = ArrayList<SensorAccel>()

    var threshold: Float = DEFAULT_THRESHOLD_ACCELERATION
    var shakeNumber: Int = DEFAULT_THRESHOLD_SHAKE_NUMBER

    var isSensorRegistered by Delegates.observable(false) { _, _, newValue ->
        if (newValue) manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        else {
            try {
                manager.unregisterListener(this)
            } catch (ignore: Exception) {
            }
        }
    }

    fun create(context: Context?, listener: ShakeListener) {
        this.listener = listener
        this.context = context
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun start(): Boolean {
        accelerometer?.let { isSensorRegistered = true }
        return isSensorRegistered
    }

    fun stop() {
        isSensorRegistered = false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nothing to do
    }

    override fun onSensorChanged(event: SensorEvent) {
        lateinit var data: SensorAccel

        event.let {
            data = SensorAccel(it.values[0], it.values[1], it.values[2], it.timestamp)
        }

        if (accels.isEmpty()) accels.add(data)
        else if (data.timestamp - accels[accels.size - 1].timestamp > INTERVAL) accels.add(data)

        check()
    }

    private fun check() {
        val vector = intArrayOf(0, 0, 0)
        val matrix = arrayOf(
            intArrayOf(0, 0), // Represents X axis, positive and negative direction.
            intArrayOf(0, 0), // Represents Y axis, positive and negative direction.
            intArrayOf(0, 0)  // Represents Z axis, positive and negative direction.
        )

        for (accel in accels) {
            if (accel.x > threshold && vector[0] < 1) {
                vector[0] = 1
                matrix[0][0]++
            }
            if (accel.x < -threshold && vector[0] > -1) {
                vector[0] = -1
                matrix[0][1]++
            }
            if (accel.y > threshold && vector[1] < 1) {
                vector[1] = 1
                matrix[1][0]++
            }
            if (accel.y < -threshold && vector[1] > -1) {
                vector[1] = -1
                matrix[1][1]++
            }
            if (accel.z > threshold && vector[2] < 1) {
                vector[2] = 1
                matrix[2][0]++
            }
            if (accel.z < -threshold && vector[2] > -1) {
                vector[2] = -1
                matrix[2][1]++
            }
        }

        for (axis in matrix) {
            for (direction in axis) {
                if (direction < shakeNumber) {
                    return
                }
            }
        }

        listener.onShake()
        accels.clear()
    }

    private class SensorAccel(var x: Float, var y: Float, var z: Float, var timestamp: Long) {
        override fun toString(): String {
            return "Accel{x=$x, y=$y, z=$z, timestamp=$timestamp}"
        }
    }
}

interface ShakeListener {
    fun onShake()
}
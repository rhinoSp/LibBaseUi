package com.rhino.ui.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import kotlin.math.abs

/**
 * @author LuoLin
 * @since Create on 2022/1/16.
 */
class SensorUtils {

    private var sensorManager: SensorManager? = null
    private val sensorMap by lazy {
        mutableMapOf<Int, Sensor?>()
    }

    /**
     * 加速度传感器摇一摇
     */
    fun isAccelerometerShaking(event: SensorEvent?): Boolean {
        val type = event?.sensor?.type
        return if (type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val sensitivity = 10
            return abs(x) > sensitivity && abs(y) > sensitivity && abs(z) > sensitivity
        } else {
            false
        }
    }

    /**
     * 注册加速传感器
     */
    fun registerAccelerometerSensor(
        activity: FragmentActivity,
        sensorEventListener: SensorEventListener
    ) {
        register(activity, sensorEventListener, Sensor.TYPE_ACCELEROMETER)
    }

    /**
     * 取消注册加速传感器
     */
    fun unregisterAccelerometerSensor(sensorEventListener: SensorEventListener) {
        unregister(sensorEventListener, Sensor.TYPE_ACCELEROMETER)
    }

    /**
     * 注册
     */
    fun register(
        activity: FragmentActivity,
        sensorEventListener: SensorEventListener,
        type: Int
    ) {
        // 传感器管理器
        sensorManager = activity.getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        // 加速度传感器
        val sensor = sensorManager?.getDefaultSensor(type)
        // 缓存sensor
        sensorMap[type] = sensor
        // 注册监听
        sensorManager?.registerListener(
            sensorEventListener,
            sensor,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    /**
     * 取消注册
     */
    fun unregister(sensorEventListener: SensorEventListener, type: Int) {
        sensorManager?.unregisterListener(sensorEventListener, sensorMap[type])
    }

}
package com.github.soundtricker.androidthings.samleuserdriver

import android.app.Activity
import android.os.Bundle
import java.io.IOException
import android.util.Log
import android.view.KeyEvent
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService


class MainActivity : Activity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val GPIO_BUTTON_PIN_NAME= "BCM21"
        private val GPIO_LED_PIN_NAME = "BCM6"
    }

    private var mButtonInputDriver: ButtonInputDriver? = null
    private var mLedGpio: Gpio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupButton()
        setupLed()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyButton()
        destroyLed()
    }

    private fun setupButton() {
        try {
            mButtonInputDriver = ButtonInputDriver(GPIO_BUTTON_PIN_NAME,
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_SPACE
            )
            mButtonInputDriver!!.register()
        } catch (e: IOException) {
            Log.e(TAG, "failed initialize button driver", e)
            return
        }
    }
    private fun setupLed() {
        try {
            val pioSerivce = PeripheralManagerService()
            mLedGpio = pioSerivce.openGpio(GPIO_LED_PIN_NAME)
        } catch (e: IOException) {
            Log.e(TAG, "failed initialize led", e)
            return
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Turn on the LED
            setLedValue(true)
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Turn off the LED
            setLedValue(false)
            return true
        }

        return super.onKeyUp(keyCode, event)
    }

    /**
     * Update the value of the LED output.
     */
    private fun setLedValue(value: Boolean) {
        try {
            mLedGpio!!.value = value
        } catch (e: IOException) {
            Log.e(TAG, "Error updating GPIO value", e)
        }

    }

    private fun destroyButton() {
        Log.i(TAG, "Closing button")
        mButtonInputDriver?.unregister()
        try {
            mButtonInputDriver?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing Button driver", e)
        } finally {
            mButtonInputDriver = null
        }

    }
    private fun destroyLed() {
        try {
            mLedGpio!!.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing LED", e)
        } finally {
            mLedGpio = null
        }
    }
}

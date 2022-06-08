package com.example.pigeneratorcoroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), FragmentTimeCounter.SendMessageListener {
    private var fragmentCalculatorPI: FragmentCalculatorPI? = null
    private var fragmentTimeCounter: FragmentTimeCounter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentCalculatorPI = FragmentCalculatorPI()
        fragmentTimeCounter = FragmentTimeCounter()

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_calculator, fragmentCalculatorPI!!)
            .replace(R.id.container_time_counter, fragmentTimeCounter!!)
            .commit()
    }

    override fun onMessageDetails(message: String) {
        val calculatorPI: FragmentCalculatorPI? = supportFragmentManager
            .findFragmentById(R.id.container_calculator) as FragmentCalculatorPI?
        if (calculatorPI != null) {
            when (message) {
                getString(R.string.message_start) -> calculatorPI.onClickStart()
                getString(R.string.message_restart) -> calculatorPI.onClickRestart()
                getString(R.string.message_pause) -> calculatorPI.onClickPause()
            }
        }
    }
}
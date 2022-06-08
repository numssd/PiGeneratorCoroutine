package com.example.pigeneratorcoroutine

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_time_counter.*
import kotlinx.coroutines.*

class FragmentTimeCounter : Fragment() {
    private var isActive = true
    private var job : Job? = null
    private var messageListener: SendMessageListener? = null
    private var countColor = 0
    private var seconds = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time_counter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initControl()

        job = CoroutineScope(Dispatchers.Main).launch {
            runTimer()
        }
    }

    private fun initControl() {
        setTimer(getString(R.string.start_time))
        buttonStart?.isClickable = false
        buttonStart?.setOnClickListener {
            messageListener?.onMessageDetails(getString(R.string.message_start))
            buttonStart?.isClickable = false
            buttonPause?.isClickable = true
            buttonRestart?.isClickable = true

            job?.cancel()
            isActive = true
            job = CoroutineScope(Dispatchers.Main).launch {
                runTimer()
            }

        }
        buttonPause?.setOnClickListener {
            messageListener?.onMessageDetails(getString(R.string.message_pause))

            buttonStart?.isClickable = true
            buttonPause?.isClickable = false
            buttonRestart?.isClickable = true

            isActive = false
        }
        buttonRestart?.setOnClickListener {
            messageListener?.onMessageDetails(getString(R.string.message_restart))

            buttonStart?.isClickable = true
            buttonPause?.isClickable = true
            buttonRestart?.isClickable = false

            isActive = false
            seconds = 0
        }

    }

    private fun setTimer(time: String) {
        textViewTimer?.text = time
    }

    private suspend fun runTimer() {
        while (isActive){
            delay(1000)
            val secondsTimer = seconds%60
            val minutesTimer = seconds/60

            if (countColor==20){
                backgroundCount?.setBackgroundColor(Color.argb(
                    255, (0..255).random(), (0..255).random(), (0..255).random()))
                countColor=0
            } else {
                countColor ++
            }
            val time = String.format(getString(R.string.time_format), minutesTimer, secondsTimer)

            setTimer(time)
            seconds ++
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SendMessageListener) {
            messageListener = context
        } else {
            throw RuntimeException("$context must implement FragmentBListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        messageListener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SECONDS_KEY, seconds)
        outState.putInt(COUNT_KEY, countColor)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        seconds = savedInstanceState?.getInt(SECONDS_KEY) ?: 0
        countColor = savedInstanceState?.getInt(COUNT_KEY) ?: 0
        super.onViewStateRestored(savedInstanceState)
    }

    interface SendMessageListener {
        fun onMessageDetails(message: String)
    }

    companion object{
        const val SECONDS_KEY = "seconds"
        const val COUNT_KEY = "count"
    }
}
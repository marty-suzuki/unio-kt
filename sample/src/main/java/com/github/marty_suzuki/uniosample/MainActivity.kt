package com.github.marty_suzuki.uniosample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import com.github.marty_suzuki.uniosample.counterunio.CounterUnioInput
import com.github.marty_suzuki.uniosample.counterunio.CounterUnioOutput
import com.github.marty_suzuki.uniosample.counterunio.CounterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: CounterViewModel by viewModels()
    private var scope: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.count_up_button).setOnClickListener {
            viewModel.input.getLambda(CounterUnioInput::countUp).invoke()
        }

        val countDownButton = findViewById<Button>(R.id.count_down_button)
        countDownButton.setOnClickListener {
            viewModel.input.getLambda(CounterUnioInput::countDown).invoke()
        }

        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main).also { scope ->
            viewModel.output
                .getFlow(CounterUnioOutput::isCountDownEnabled)
                .onEach {
                    countDownButton.isEnabled = it
                }
                .launchIn(scope)

            val textView = findViewById<TextView>(R.id.textView)
            viewModel.output
                .getFlow(CounterUnioOutput::count)
                .onEach {
                    textView.text = it
                }
                .launchIn(scope)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope?.cancel()
        scope = null
    }
}
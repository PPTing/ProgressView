package me.ppting.view.progress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var leftValue  = 0
    private var rightValue  = 0
    companion object{
        private const val OFFSET = 10
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addLeft.setOnClickListener {
            leftValue += OFFSET
            progressView.updateLeftValue(leftValue)
            tvLeftValue.text = "$leftValue"
        }
        minLeft.setOnClickListener {
            leftValue -= OFFSET
            progressView.updateLeftValue(leftValue)
            tvLeftValue.text = "$leftValue"
        }
        addRight.setOnClickListener {
            rightValue += OFFSET
            progressView.updateRightValue(rightValue)
            tvRightValue.text = "$rightValue"
        }
        minRight.setOnClickListener {
            rightValue -= OFFSET
            progressView.updateRightValue(rightValue)
            tvRightValue.text = "$rightValue"
        }
    }
}
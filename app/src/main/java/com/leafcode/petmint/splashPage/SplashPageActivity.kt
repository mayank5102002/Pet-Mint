package com.leafcode.petmint.splashPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.viewModels
import com.leafcode.petmint.MainActivity
import com.leafcode.petmint.databinding.ActivitySplashPageBinding

class SplashPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashPageBinding

    private val viewModel : SplashPageViewModel by viewModels()

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.splashPageProgressBar
        val splashPageLoadingText = binding.splashPageLoadingText

        viewModel.mainActivity.observe(this,{
            if (it == true){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                viewModel.mainActivityDone()
                this.finish()
            }
        })

        viewModel.countDown()

        splashPageLoadingText.text = viewModel.getMoto()

    }
}
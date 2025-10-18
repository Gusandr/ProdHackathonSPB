package com.example.prodhackathonspb.splash.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.prodhackathonspb.databinding.ActivityEntranceMainBinding
import com.example.prodhackathonspb.menu.presentation.MainMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    private lateinit var binding: ActivityEntranceMainBinding
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntranceMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.linearLayout) { view, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Только paddingTop!
            view.setPadding(view.paddingLeft, sysBars.top, view.paddingRight, view.paddingBottom)
            insets
        }



        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    when (state) {
                        is SplashState.Authenticated -> {
                            startActivity(Intent(this@SplashActivity, MainMenuActivity::class.java))
                            finish()
                        }
                        is SplashState.Unauthenticated, is SplashState.Error -> {
                            // Просто остаёмся на activity_entrance_main
                            setupNavigation()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupNavigation() {
        // Пример — кнопки по id из layout
        binding.buttonEntranceWithMail.setOnClickListener {
            val intent = Intent(this, com.example.prodhackathonspb.login.presentation.LoginActivity::class.java)
            startActivity(intent)
        }
        binding.textView3.setOnClickListener {
            val intent = Intent(this, com.example.prodhackathonspb.signup.presentation.SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}

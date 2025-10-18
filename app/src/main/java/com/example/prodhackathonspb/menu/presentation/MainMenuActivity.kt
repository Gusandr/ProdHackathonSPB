package com.example.prodhackathonspb.menu.presentation


import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.prodhackathonspb.databinding.ActivityMainMenuBinding
import com.example.prodhackathonspb.main.presentation.MainMenuViewModel
import com.example.prodhackathonspb.profile.presentation.UserProfileActivity
import com.example.prodhackathonspb.splash.presentation.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding
    private val viewModel by viewModels<MainMenuViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeUi()

        binding.adminHeaderAvatar.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        viewModel.loadMainData()
    }

    private fun observeUi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.adminHeaderTitle.text = "Добрый день" +
                            if (state.userName.isNotBlank()) ", ${state.userName}" else ", Мавроди"
                    // todo: наполнение меню, ошибки (но не вкладка Splash!)
                }
            }
        }
    }
}

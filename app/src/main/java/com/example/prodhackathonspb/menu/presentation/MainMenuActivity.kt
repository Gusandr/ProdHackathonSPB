package com.example.prodhackathonspb.menu.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.prodhackathonspb.databinding.ActivityMainMenuBinding
import com.example.prodhackathonspb.main.presentation.MainMenuViewModel
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
            // Устанавливаем отступы только там, где надо (например, если есть кастомный toolbar/layout)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeUi()
        setupQuickLinks()
    }

    private fun observeUi() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.adminHeaderTitle.text = "Добрый день" + if (state.userName.isNotBlank()) ", ${state.userName}" else "хуй"
                    // todo: отобразить данные пользователя, gpu и прочее как потребуется
                }
            }
        }
    }

    private fun setupQuickLinks() {
        binding.quickAccessGroupsButton.setOnClickListener {
            // todo: переход на экран групп (реализуешь сам)
        }
    }
}

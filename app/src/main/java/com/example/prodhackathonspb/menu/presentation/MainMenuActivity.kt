package com.example.prodhackathonspb.menu.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.prodhackathonspb.databinding.ActivityMainMenuBinding
import com.example.prodhackathonspb.groups.presentation.ActivityGroups
import com.example.prodhackathonspb.profile.presentation.UserProfileActivity
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

        // Корректная работа с insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Клик на "аватар профиля"
        binding.adminHeaderAvatar.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        // Клик на "группы" — используй именно эту кнопку!
        binding.quickAccessGroupsButton.setOnClickListener {
            startActivity(Intent(this, ActivityGroups::class.java))
        }

        // Добавить группу (пример асинхронного вызова)
        binding.buttonEntranceWithMail.setOnClickListener {
            lifecycleScope.launch {
                val success = viewModel.addGroup()
                if (success) {
                    Toast.makeText(this@MainMenuActivity, "Группа добавлена!", Toast.LENGTH_SHORT).show()
                } else {
                    // Ошибка выводится из ViewModel через uiState.error
                }
            }
        }

        observeUi()

        viewModel.loadMainData()
    }

    private fun observeUi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.adminHeaderTitle.text = "Добрый день" +
                            if (state.userName.isNotBlank()) ", ${state.userName}" else ", Мавроди"
                    state.error?.let {
                        Toast.makeText(this@MainMenuActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

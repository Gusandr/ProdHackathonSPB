package com.example.prodhackathonspb.main.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.prodhackathonspb.databinding.ActivityMainBinding
import com.example.prodhackathonspb.login.presentation.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Загружаем данные пользователя
        viewModel.loadUserData()

        // Кнопка выхода
        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Наблюдаем за данными пользователя
                launch {
                    viewModel.userData.collect { user ->
                        user?.let {
                            binding.textUserEmail.text = it.email ?: "Неизвестно"
                            // Отображаем другие данные пользователя
                        }
                    }
                }

                // Наблюдаем за состоянием загрузки
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) {
                            android.view.View.VISIBLE
                        } else {
                            android.view.View.GONE
                        }
                    }
                }

                // Наблюдаем за событием logout
                launch {
                    viewModel.logoutEvent.collect {
                        navigateToLogin()
                    }
                }

                // Наблюдаем за ошибками
                launch {
                    viewModel.errorEvent.collect { message ->
                        Toast.makeText(
                            this@MainActivity,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

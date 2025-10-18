package com.example.prodhackathonspb.login.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.prodhackathonspb.databinding.ActivityLoginBinding
import com.example.prodhackathonspb.main.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Кнопка регистрации
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (validateInput(email, password)) {
                viewModel.signUp(email, password)
            }
        }

        // Кнопка входа (если есть)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (validateInput(email, password)) {
                viewModel.signUp(email, password)
            }
        }

        // Очистка ошибок при вводе
        binding.etEmail.doAfterTextChanged {
            binding.tilEmail.error = null
        }

        binding.etPassword.doAfterTextChanged {
            binding.tilPassword.error = null
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Собираем несколько flows параллельно
                launch {
                    viewModel.showNetworkError.collect { message ->
                        Toast.makeText(
                            this@LoginActivity,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                        // Блокируем кнопки во время загрузки
                        binding.btnSignUp.isEnabled = !isLoading
                        binding.btnLogin.isEnabled = !isLoading
                        binding.etEmail.isEnabled = !isLoading
                        binding.etPassword.isEnabled = !isLoading
                    }
                }

                launch {
                    viewModel.loginSuccess.collect {
                        Toast.makeText(
                            this@LoginActivity,
                            "Успешная регистрация!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Переход на главный экран
                        navigateToMain()
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isBlank()) {
            binding.tilEmail.error = "Введите email"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Неверный формат email"
            isValid = false
        }

        if (password.isBlank()) {
            binding.tilPassword.error = "Введите пароль"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Пароль должен быть минимум 6 символов"
            isValid = false
        }

        return isValid
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

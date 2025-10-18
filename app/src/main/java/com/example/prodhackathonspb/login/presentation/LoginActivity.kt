package com.example.prodhackathonspb.login.presentation

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.prodhackathonspb.R
import com.example.prodhackathonspb.databinding.ActivityEntranceLoginBinding
import com.example.prodhackathonspb.main.presentation.MainActivity
import com.example.prodhackathonspb.signup.presentation.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntranceLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEntranceLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Класс для кастомного TypefaceSpan — прямо тут
        class CustomTypefaceSpan(private val typeface: Typeface) : TypefaceSpan("") {
            override fun updateDrawState(ds: TextPaint) {
                ds.typeface = typeface
            }
            override fun updateMeasureState(paint: TextPaint) {
                paint.typeface = typeface
            }
        }

        // Применение кастомного Montserrat Regular к hint обоих полей
        val montserrat = ResourcesCompat.getFont(this, R.font.montserrat_regular) ?: Typeface.DEFAULT

        val hintMail = SpannableString("Почта").apply {
            setSpan(CustomTypefaceSpan(montserrat), 0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        binding.editTextMail.hint = hintMail

        val hintPass = SpannableString("Пароль").apply {
            setSpan(CustomTypefaceSpan(montserrat), 0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        binding.editTextPassword.hint = hintPass


        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Настройки полей ввода (шрифт для текста задавай в xml)

        binding.editTextMail.setText("")
        // hint задан выше

        binding.editTextPassword.apply {
            setText("")
            // hint задан выше
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        binding.buttonEntranceWithMail.setOnClickListener {
            val email = binding.editTextMail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (validateInput(email, password)) {
                viewModel.signIn(email, password)
            }
        }

        binding.textView3.setOnClickListener {
            navigateToSignUp()
        }

        binding.editTextMail.doAfterTextChanged {
            // Можно добавить логику
        }
        binding.editTextPassword.doAfterTextChanged {
            // Можно добавить логику
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Ошибки
                launch {
                    viewModel.showNetworkError.collect { message ->
                        Toast.makeText(
                            this@LoginActivity,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                // Загрузка
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.buttonEntranceWithMail.isEnabled = !isLoading
                        binding.editTextMail.isEnabled = !isLoading
                        binding.editTextPassword.isEnabled = !isLoading
                        binding.textView3.isEnabled = !isLoading

                        binding.textEntranceWithMail.text = if (isLoading) {
                            "Загрузка..."
                        } else {
                            "Продолжить"
                        }
                    }
                }

                // Успех
                launch {
                    viewModel.loginSuccess.collect {
                        Toast.makeText(
                            this@LoginActivity,
                            "Вход выполнен успешно!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToMain()
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isBlank()) {
            Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show()
            binding.editTextMail.requestFocus()
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Неверный формат email", Toast.LENGTH_SHORT).show()
            binding.editTextMail.requestFocus()
            isValid = false
        }

        if (password.isBlank() && isValid) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()
            binding.editTextPassword.requestFocus()
            isValid = false
        } else if (password.length < 6 && isValid) {
            Toast.makeText(
                this,
                "Пароль должен быть минимум 6 символов",
                Toast.LENGTH_SHORT
            ).show()
            binding.editTextPassword.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}

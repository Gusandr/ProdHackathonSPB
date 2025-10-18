package com.example.prodhackathonspb.signup.presentation

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
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
import com.example.prodhackathonspb.databinding.ActivityEntranceSignUpBinding
import com.example.prodhackathonspb.login.presentation.LoginActivity
import com.example.prodhackathonspb.login.presentation.LoginViewModel
import com.example.prodhackathonspb.main.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntranceSignUpBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEntranceSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Вложенный CustomTypefaceSpan — всё тут, как просил
        class CustomTypefaceSpan(private val typeface: Typeface) : TypefaceSpan("") {
            override fun updateDrawState(ds: TextPaint) {
                ds.typeface = typeface
            }
            override fun updateMeasureState(paint: TextPaint) {
                paint.typeface = typeface
            }
        }

        // Получаем Montserrat Regular из res/font
        val montserrat = ResourcesCompat.getFont(this, com.example.prodhackathonspb.R.font.montserrat_regular) ?: Typeface.DEFAULT

        // Устанавливаем кастомный hint на все поля
        binding.editTextNumber.apply {
            setText("")
            val hint = SpannableString("Почта").apply {
                setSpan(CustomTypefaceSpan(montserrat), 0, length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            setHint(hint)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        binding.editTextUserName.apply {
            setText("")
            val hint = SpannableString("Пароль").apply {
                setSpan(CustomTypefaceSpan(montserrat), 0, length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            setHint(hint)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        binding.editTextPassword.apply {
            setText("")
            val hint = SpannableString("Повторите пароль").apply {
                setSpan(CustomTypefaceSpan(montserrat), 0, length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            setHint(hint)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // hint/setText больше не нужны — уже установлены выше
        binding.buttonEntranceWithMail.setOnClickListener {
            val email = binding.editTextNumber.text.toString().trim()
            val password = binding.editTextUserName.text.toString().trim()
            val passwordConfirm = binding.editTextPassword.text.toString().trim()

            if (validateInput(email, password, passwordConfirm)) {
                viewModel.signUp(email, password)
            }
        }

        binding.textView3.setOnClickListener {
            navigateToLogin()
        }

        binding.textIfNotHaveAccount.setOnClickListener {
            navigateToLogin()
        }

        binding.editTextNumber.doAfterTextChanged { }
        binding.editTextUserName.doAfterTextChanged { }
        binding.editTextPassword.doAfterTextChanged { }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Ошибки
                launch {
                    viewModel.showNetworkError.collect { message ->
                        Toast.makeText(
                            this@SignUpActivity,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                // Загрузка
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.buttonEntranceWithMail.isEnabled = !isLoading
                        binding.editTextNumber.isEnabled = !isLoading
                        binding.editTextUserName.isEnabled = !isLoading
                        binding.editTextPassword.isEnabled = !isLoading
                        binding.textView3.isEnabled = !isLoading

                        binding.textEntranceWithMail.text = if (isLoading) {
                            "Загрузка..."
                        } else {
                            "Зарегистрироваться"
                        }
                    }
                }

                // Успех
                launch {
                    viewModel.loginSuccess.collect {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Регистрация выполнена успешно!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToMain()
                    }
                }
            }
        }
    }

    private fun validateInput(
        email: String,
        password: String,
        passwordConfirm: String
    ): Boolean {
        var isValid = true

        // Проверка email
        if (email.isBlank()) {
            Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show()
            binding.editTextNumber.requestFocus()
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Неверный формат email", Toast.LENGTH_SHORT).show()
            binding.editTextNumber.requestFocus()
            isValid = false
        }

        // Проверка пароля
        if (password.isBlank() && isValid) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()
            binding.editTextUserName.requestFocus()
            isValid = false
        } else if (password.length < 6 && isValid) {
            Toast.makeText(
                this,
                "Пароль должен быть минимум 6 символов",
                Toast.LENGTH_SHORT
            ).show()
            binding.editTextUserName.requestFocus()
            isValid = false
        }

        // Проверка подтверждения пароля
        if (passwordConfirm.isBlank() && isValid) {
            Toast.makeText(this, "Повторите пароль", Toast.LENGTH_SHORT).show()
            binding.editTextPassword.requestFocus()
            isValid = false
        } else if (password != passwordConfirm && isValid) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            binding.editTextPassword.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

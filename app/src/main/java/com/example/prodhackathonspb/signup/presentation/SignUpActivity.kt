package com.example.prodhackathonspb.signup.presentation

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
import com.example.prodhackathonspb.databinding.ActivityEntranceSignUpBinding
import com.example.prodhackathonspb.login.presentation.LoginActivity
import com.example.prodhackathonspb.login.presentation.LoginViewModel
import com.example.prodhackathonspb.menu.presentation.MainMenuActivity
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

        // Хинты фирменным шрифтом (по id)
        class CustomTypefaceSpan(private val typeface: Typeface) : TypefaceSpan("") {
            override fun updateDrawState(ds: TextPaint) { ds.typeface = typeface }
            override fun updateMeasureState(paint: TextPaint) { paint.typeface = typeface }
        }
        val montserrat = ResourcesCompat.getFont(this, R.font.montserrat_regular) ?: Typeface.DEFAULT

        binding.editTextNumber.hint = SpannableString("Почта").apply {
            setSpan(CustomTypefaceSpan(montserrat), 0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        binding.editTextUserName.hint = SpannableString("Пароль").apply {
            setSpan(CustomTypefaceSpan(montserrat), 0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        binding.editTextPassword.hint = SpannableString("Повторите пароль").apply {
            setSpan(CustomTypefaceSpan(montserrat), 0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.editTextNumber.setText("")
        binding.editTextUserName.setText("")
        binding.editTextPassword.setText("")

        binding.editTextUserName.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.editTextPassword.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        binding.buttonEntranceWithMail.setOnClickListener {
            val email = binding.editTextNumber.text.toString().trim()
            val password = binding.editTextUserName.text.toString().trim()
            val passwordRepeat = binding.editTextPassword.text.toString().trim()
            if (validateInput(email, password, passwordRepeat)) {
                viewModel.signUp(email, password)
            }
        }

        binding.textIfNotHaveAccount.setOnClickListener { navigateToLogin() }
        binding.textView3.setOnClickListener { navigateToLogin() }

        binding.editTextNumber.doAfterTextChanged { }
        binding.editTextUserName.doAfterTextChanged { }
        binding.editTextPassword.doAfterTextChanged { }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.showNetworkError.collect { message ->
                        Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.buttonEntranceWithMail.isEnabled = !isLoading
                        binding.editTextNumber.isEnabled = !isLoading
                        binding.editTextUserName.isEnabled = !isLoading
                        binding.editTextPassword.isEnabled = !isLoading
                        binding.textIfNotHaveAccount.isEnabled = !isLoading
                        binding.textView3.isEnabled = !isLoading
                        binding.textEntranceWithMail.text = if (isLoading) "Загрузка..." else "Зарегистрироваться"
                    }
                }
                launch {
                    viewModel.loginSuccess.collect {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Регистрация успешна!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToMainMenu()
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String, passwordRepeat: String): Boolean {
        var isValid = true
        if (email.isBlank()) {
            Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show(); binding.editTextNumber.requestFocus(); isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Неверный формат email", Toast.LENGTH_SHORT).show(); binding.editTextNumber.requestFocus(); isValid = false
        }
        if (password.isBlank() && isValid) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show(); binding.editTextUserName.requestFocus(); isValid = false
        } else if (password.length < 6 && isValid) {
            Toast.makeText(this, "Пароль должен быть минимум 6 символов", Toast.LENGTH_SHORT).show(); binding.editTextUserName.requestFocus(); isValid = false
        }
        if (passwordRepeat.isBlank() && isValid) {
            Toast.makeText(this, "Повторите пароль", Toast.LENGTH_SHORT).show(); binding.editTextPassword.requestFocus(); isValid = false
        } else if (password != passwordRepeat && isValid) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show(); binding.editTextPassword.requestFocus(); isValid = false
        }
        return isValid
    }

    private fun navigateToMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

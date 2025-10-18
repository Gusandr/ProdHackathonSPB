package com.example.prodhackathonspb.profile.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.prodhackathonspb.R
import com.example.prodhackathonspb.databinding.ActivityUserProfileBinding
import com.example.prodhackathonspb.login.presentation.LoginActivity
import com.example.prodhackathonspb.splash.presentation.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val viewModel by viewModels<UserProfileViewModel>()
    private var alexMode = false
    private var lastEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Назад
        binding.imageButtonBack.setOnClickListener { finish() }

        // Выход
        binding.buttonLogout.setOnClickListener { viewModel.logout() }

        // Быстрые действия (пример — переход к группам)
        binding.quickAccessGroupsButton.setOnClickListener {
            Toast.makeText(this, "Перейти в раздел групп (demo)", Toast.LENGTH_SHORT).show()
        }

        binding.imageUserIcon.setOnClickListener {
            if (!alexMode) {
                binding.imageUserIcon.setImageResource(R.drawable.icon_logotype_alex_meme)
                lastEmail = binding.textUserMail.text.toString()
                binding.textUserMail.text = "dog.git.@alex.com"
            } else {
                binding.imageUserIcon.setImageResource(R.drawable.icon_user_profile_default)
                binding.textUserMail.text = lastEmail ?: "DevSquad@yandex.ru"
            }
            alexMode = !alexMode
        }

        // Карточка приглашения (вариант: если state.hasInvite == true, иначе — убрать)
        // Можно также обработать Accept/Decline для приглашения:
        binding.buttonAccept.setOnClickListener { viewModel.onInviteAccepted() }
        binding.buttonDecline.setOnClickListener { viewModel.onInviteDeclined() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        // email
                        binding.textUserMail.text = state.email ?: "—"
                        // аватар если есть
                        // binding.imageUserIcon.setImageResource(...) // если будет url/base64 — используй Glide/Picasso
                        // показать/скрыть приглашение
                        binding.frameLayoutCard.visibility =
                            if (state.hasInvite) View.VISIBLE else View.GONE
                        // текст приглашения
                        binding.textView2.text = state.inviteFrom?.let { "От: $it" } ?: ""
                        binding.textView24.text = state.inviteGroup ?: ""
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    viewModel.logoutFlow.collect {
                        // full backstack clear, переход к авторизации
                        val intent = Intent(this@UserProfileActivity, SplashActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                launch {
                    viewModel.errorFlow.collect {
                        Toast.makeText(this@UserProfileActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

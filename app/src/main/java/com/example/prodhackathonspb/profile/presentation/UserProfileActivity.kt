package com.example.prodhackathonspb.profile.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prodhackathonspb.R
import com.example.prodhackathonspb.databinding.ActivityUserProfileBinding
import com.example.prodhackathonspb.login.presentation.LoginActivity
import com.example.prodhackathonspb.profile.data.UserInviteAdapter
import com.example.prodhackathonspb.splash.presentation.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val viewModel by viewModels<UserProfileViewModel>()
    private lateinit var inviteAdapter: UserInviteAdapter
    private var alexMode = false
    private var lastEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageButtonBack.setOnClickListener { finish() }
        binding.buttonLogout.setOnClickListener { viewModel.logout() }
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

        inviteAdapter = UserInviteAdapter(
            onAccept = { inviteId -> viewModel.acceptInvite(inviteId) },
            onDecline = { inviteId -> viewModel.declineInvite(inviteId) }
        )
        binding.invitesRecycler.adapter = inviteAdapter
        binding.invitesRecycler.layoutManager = LinearLayoutManager(this)
        binding.invitesRecycler.setHasFixedSize(true)

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        binding.textUserMail.text = state.email ?: "—"
                        inviteAdapter.submitList(state.invites)
                        binding.invitesRecycler.visibility = if (state.invites.isEmpty()) View.GONE else View.VISIBLE
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    viewModel.logoutFlow.collect {
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

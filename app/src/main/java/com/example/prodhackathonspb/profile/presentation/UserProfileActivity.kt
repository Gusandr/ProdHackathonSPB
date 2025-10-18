package com.example.prodhackathonspb.profile.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.prodhackathonspb.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Здесь — твоя логика отображения профиля
    }
}

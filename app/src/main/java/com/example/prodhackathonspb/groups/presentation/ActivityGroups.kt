package com.example.prodhackathonspb.groups.presentation

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.prodhackathonspb.R
import com.example.prodhackathonspb.databinding.ActivityGroupsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityGroups : AppCompatActivity() {
    private lateinit var binding: ActivityGroupsBinding
    private val viewModel by viewModels<ActivityGroupsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageButtonBack.setOnClickListener { finish() }
        binding.buttonEntranceWithMail.setOnClickListener {
            viewModel.addGroup()
        }

        // Открываем настроечный BottomSheetDialog
        binding.imageButtonSettings.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            val sheetView = layoutInflater.inflate(R.layout.dialog_window_gpu_settings, null).apply {
                findViewById<ImageView>(R.id.imageView2)?.setOnClickListener {
                    bottomSheet.dismiss()
                }
            }
            bottomSheet.setContentView(sheetView)
            bottomSheet.show()
        }

        // Динамически добавляем группы (как карточки во внутренний layout)
        lifecycleScope.launchWhenStarted {
            viewModel.groups.collect { groups ->
                binding.scrollViewForGroups.removeAllViews()
                groups.forEachIndexed { i, group ->
                    val groupView = layoutInflater.inflate(R.layout.fragment_group, binding.scrollViewForGroups, false)
                    groupView.findViewById<TextView>(R.id.textView2).text = "Группа ${group.id}"
                    groupView.findViewById<TextView>(R.id.textView).text = "Группа ${i + 1}"

                    // Тут ещё можно добавить clickListener для shards/group settings
                    groupView.setOnClickListener {
                        val bottomSheet = BottomSheetDialog(this@ActivityGroups)
                        val sheetView = layoutInflater.inflate(R.layout.dialog_window_group, null).apply {
//                            findViewById<ImageView>(R.id.imageView2)?.setOnClickListener {
//                                bottomSheet.dismiss()
//                            }
                        }
                        bottomSheet.setContentView(sheetView)
                        bottomSheet.show()
                    }

                    binding.scrollViewForGroups.addView(groupView)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.errorFlow.collect {
                Toast.makeText(this@ActivityGroups, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}

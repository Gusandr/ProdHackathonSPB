package com.example.prodhackathonspb.groups.presentation

import android.os.Bundle
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
            Toast.makeText(this, "Добавить группу (реализуй переход)", Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.groups.collect { groups ->
                // Очищаем все предыдущие группы из LinearLayout
                binding.scrollViewForGroups.removeAllViews()
                groups.forEachIndexed { i, group ->
                    val groupView = layoutInflater.inflate(R.layout.fragment_group, binding.scrollViewForGroups, false)
                    groupView.findViewById<TextView>(R.id.textView2).text = "Группа ${group.id}"
                    // textView — надпись "Группа"
                    groupView.findViewById<TextView>(R.id.textView).text = "Группа ${i+1}"
                    // По желанию можешь подставлять изображение group.icon в imageView

                    // При необходимости обработчик клика:
                    groupView.setOnClickListener {
                        val dialog = BottomSheetDialog(this@ActivityGroups) // или requireContext() во фрагменте
                        val view = layoutInflater.inflate(R.layout.dialog_window_group, null)
                        dialog.setContentView(view)
                        dialog.show()
                        Toast.makeText(this@ActivityGroups, "Клик по группе ${group.id}", Toast.LENGTH_SHORT).show()
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


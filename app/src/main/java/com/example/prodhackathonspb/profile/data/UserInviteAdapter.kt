package com.example.prodhackathonspb.profile.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prodhackathonspb.databinding.ItemInviteProfileBinding
import com.example.prodhackathonspb.network.models.GroupInvite

class UserInviteAdapter(
    private val onAccept: (String) -> Unit,
    private val onDecline: (String) -> Unit
) : ListAdapter<GroupInvite, UserInviteAdapter.InviteViewHolder>(
    object : DiffUtil.ItemCallback<GroupInvite>() {
        override fun areItemsTheSame(old: GroupInvite, new: GroupInvite) = old.id == new.id
        override fun areContentsTheSame(old: GroupInvite, new: GroupInvite) = old == new
    }
) {
    inner class InviteViewHolder(val binding: ItemInviteProfileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(invite: GroupInvite) {
            // тут подгрузи user по ID если нужно отобразить не только id, а имя:
            binding.fromText.text = "От: ${invite.inviterId}"
            binding.groupText.text = "Группа: ${invite.groupId}"
            binding.buttonAccept.setOnClickListener { onAccept(invite.id) }
            binding.buttonDecline.setOnClickListener { onDecline(invite.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        InviteViewHolder(ItemInviteProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) =
        holder.bind(getItem(position))
}

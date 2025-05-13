package com.example.phonedialer.ui.contacts

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.phonedialer.databinding.ItemContactBinding

class ContactsAdapter : ListAdapter<Contact, ContactsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            binding.apply {
                nameText.text = contact.name
                numberText.text = contact.phoneNumber

                // Set initials if no photo
                if (contact.photoUri == null) {
                    initialsText.text = contact.name
                        .split(" ")
                        .take(2)
                        .joinToString("") { it.firstOrNull()?.toString() ?: "" }
                        .uppercase()
                    avatarImage.setImageDrawable(null)
                } else {
                    initialsText.text = ""
                    Glide.with(avatarImage)
                        .load(Uri.parse(contact.photoUri))
                        .circleCrop()
                        .into(avatarImage)
                }

                // Call button click listener
                callButton.setOnClickListener {
                    // Handle call action through interface or ViewModel
                }

                // Item click listener
                root.setOnClickListener {
                    // Handle item click through interface or ViewModel
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}

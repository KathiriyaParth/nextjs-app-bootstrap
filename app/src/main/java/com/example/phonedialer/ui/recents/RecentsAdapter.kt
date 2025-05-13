package com.example.phonedialer.ui.recents

import android.provider.CallLog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phonedialer.R
import com.example.phonedialer.databinding.ItemRecentCallBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class RecentsAdapter : ListAdapter<CallLogEntry, RecentsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentCallBinding.inflate(
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
        private val binding: ItemRecentCallBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

        fun bind(call: CallLogEntry) {
            binding.apply {
                nameText.text = call.name ?: call.number
                numberText.text = if (call.name != null) call.number else ""
                timeText.text = dateFormat.format(call.date)
                durationText.text = formatDuration(call.duration)

                val callTypeIcon = when (call.type) {
                    CallLog.Calls.OUTGOING_TYPE -> R.drawable.ic_call_made
                    CallLog.Calls.INCOMING_TYPE -> R.drawable.ic_call_received
                    CallLog.Calls.MISSED_TYPE -> R.drawable.ic_call_missed
                    else -> R.drawable.ic_call
                }
                callTypeIcon.setImageResource(callTypeIcon)
            }
        }

        private fun formatDuration(seconds: Long): String {
            if (seconds == 0L) return ""
            
            return when {
                seconds < 60 -> "${seconds}s"
                seconds < 3600 -> {
                    val minutes = TimeUnit.SECONDS.toMinutes(seconds)
                    val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
                    String.format("%d:%02d", minutes, remainingSeconds)
                }
                else -> {
                    val hours = TimeUnit.SECONDS.toHours(seconds)
                    val remainingMinutes = TimeUnit.SECONDS.toMinutes(seconds - TimeUnit.HOURS.toSeconds(hours))
                    String.format("%d:%02d", hours, remainingMinutes)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CallLogEntry>() {
        override fun areItemsTheSame(oldItem: CallLogEntry, newItem: CallLogEntry): Boolean {
            return oldItem.number == newItem.number && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CallLogEntry, newItem: CallLogEntry): Boolean {
            return oldItem == newItem
        }
    }
}

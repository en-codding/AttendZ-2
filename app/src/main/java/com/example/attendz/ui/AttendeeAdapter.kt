package com.example.attendz.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendz.R
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.attendz.ui.view_model.Participant
import java.text.SimpleDateFormat
import java.util.Date

class AttendeeAdapter : ListAdapter<Participant, AttendeeAdapter.AttendeeViewHolder>(AttendeeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_attendee, parent, false)
        return AttendeeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AttendeeViewHolder, position: Int) {
        val participant = getItem(position)
        holder.bind(participant)
    }

    inner class AttendeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val attendeeNameTextView: TextView = itemView.findViewById(R.id.attendeeName)
        private val attendeeEmailTextView: TextView = itemView.findViewById(R.id.attendeeEmail)
        private val dateTextView: TextView = itemView.findViewById(R.id.joinDate)
        fun bind(participant: Participant) {
            val date: Date? = participant.joined_at?.toDate()
            val dateFormat = SimpleDateFormat("dd MMM yyyy hh:mm a")
            val dateString: String = dateFormat.format(date)
            attendeeNameTextView.text = participant.display_name
            dateTextView.text = "Joined At : " + dateString
            attendeeEmailTextView.text = participant.email
            // Bind other event details to corresponding views if needed
        }
    }

    private class AttendeeDiffCallback: DiffUtil.ItemCallback<Participant>() {
        override fun areItemsTheSame(oldItem: Participant, newItem: Participant): Boolean {
            return oldItem.event_id == newItem.event_id
        }

        override fun areContentsTheSame(oldItem: Participant, newItem: Participant): Boolean {
            return oldItem == newItem
        }
    }
}
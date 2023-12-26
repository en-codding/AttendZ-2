package com.example.attendz.ui
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendz.R
import com.example.attendz.ui.view_model.Event
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import java.text.SimpleDateFormat
import java.util.Date

class JoinedEventAdapter : ListAdapter<Event, JoinedEventAdapter.JoinedEventViewHolder>(JoinedEventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinedEventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return JoinedEventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JoinedEventViewHolder, position: Int) {
        val currentEvent = getItem(position)
        holder.bind(currentEvent)
    }

    inner class JoinedEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventNameTextView: TextView = itemView.findViewById(R.id.eventName)
        private val hostNameTextView: TextView = itemView.findViewById(R.id.hostName)
        private val dateTextView: TextView = itemView.findViewById(R.id.eventDate)
        fun bind(event: Event) {
            val date: Date? = event.created_at?.toDate()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateString: String = dateFormat.format(date)
            eventNameTextView.text = event.name
            hostNameTextView.text = "Host : "+event.host_name
            dateTextView.text = "Date : " + dateString
            // Bind other event details to corresponding views if needed
        }
    }

    private class JoinedEventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}
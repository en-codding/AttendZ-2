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
import com.google.firebase.firestore.DocumentReference
import java.text.SimpleDateFormat
import java.util.Date

class EventAdapter : ListAdapter<Pair<Event, DocumentReference>, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    private var onItemClickListener: ((Pair<Event, DocumentReference>) -> Unit)? = null
    fun setOnItemClickListener(listener: (Pair<Event, DocumentReference>) -> Unit) {
        onItemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = getItem(position)
        holder.bind(currentEvent)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(currentEvent)
        }
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventNameTextView: TextView = itemView.findViewById(R.id.eventName)
        private val hostNameTextView: TextView = itemView.findViewById(R.id.hostName)
        private val dateTextView: TextView = itemView.findViewById(R.id.eventDate)
        fun bind(event: Pair<Event, DocumentReference>) {
            val date: Date? = event.first.created_at?.toDate()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateString: String = dateFormat.format(date)
            eventNameTextView.text = event.first.name
            hostNameTextView.text = "Host : "+event.first.host_name
            dateTextView.text = "Date : " + dateString
            // Bind other event details to corresponding views if needed
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<Pair<Event, DocumentReference>>() {
        override fun areItemsTheSame(oldItem: Pair<Event, DocumentReference>, newItem: Pair<Event, DocumentReference>): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(oldItem: Pair<Event, DocumentReference>, newItem: Pair<Event, DocumentReference>): Boolean {
            return oldItem == newItem
        }
    }
}
package com.example.attendz.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendz.R
import com.example.attendz.ui.EventAdapter
import com.example.attendz.ui.activity.AttendeeHistory
import com.example.attendz.ui.view_model.EventViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class YourEvent : Fragment() {
    private val auth = Firebase.auth
    private val user = auth.currentUser

    private lateinit var eventViewModel: EventViewModel
    private lateinit var recyclerView: RecyclerView
    private var eventAdapter: EventAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_your_event, container, false)

        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        // Initialize RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.yourEventRecyclerView)

        // Set a LinearLayoutManager to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Make sure eventAdapter is initialized only once
        if (eventAdapter == null) {
            eventAdapter = EventAdapter()
            recyclerView.adapter = eventAdapter
        }

        // Trigger loading of the event list
        eventViewModel.loadEventList(user?.email.toString()).observe(viewLifecycleOwner, Observer {eventList ->
            // Update the RecyclerView adapter when the event list changes
            eventAdapter?.submitList(eventList)
            eventAdapter!!.setOnItemClickListener {
                val attendeeHistoryIntent = Intent(requireContext(), AttendeeHistory::class.java)
                attendeeHistoryIntent.putExtra("eventId", it.second.id)
                attendeeHistoryIntent.putExtra("eventName", it.first.name)
                val date: Date? = it.first.created_at?.toDate()
                val dateFormat = SimpleDateFormat("dd MMM yyyy hh:mm a")
                val dateString: String = dateFormat.format(date)
                attendeeHistoryIntent.putExtra("eventDate", dateString)
                startActivity(attendeeHistoryIntent)
            }
        })

        return view

    }


}
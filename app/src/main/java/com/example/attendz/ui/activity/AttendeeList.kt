package com.example.attendz.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendz.R
import com.example.attendz.databinding.ActivityAttendeeListBinding
import com.example.attendz.databinding.ActivityEventQrActivityBinding
import com.example.attendz.ui.AttendeeAdapter
import com.example.attendz.ui.EventAdapter
import com.example.attendz.ui.JoinedEventAdapter
import com.example.attendz.ui.view_model.EventViewModel
import com.example.attendz.ui.view_model.Participant

class AttendeeList : AppCompatActivity() {

    private lateinit var binding: ActivityAttendeeListBinding
    private var attendeeAdapter: AttendeeAdapter? = null
    private lateinit var eventViewModel: EventViewModel
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendeeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val eventId = intent.extras?.getString("eventId")
        val eventName = intent.extras?.getString("eventName")
        binding.eventName.text = eventName
        binding.backToQrButton.setOnClickListener{
            val eventQRActivityIntent = Intent(this, EventQRActivity::class.java)
            eventQRActivityIntent.putExtra("eventId", eventId)
            eventQRActivityIntent.putExtra("eventName", eventName)
            startActivity(eventQRActivityIntent)
        }
        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        // Initialize RecyclerView and its adapter
        recyclerView = binding.attendeeListRecyclerView

        // Set a LinearLayoutManager to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Make sure eventAdapter is initialized only once
        if (attendeeAdapter == null) {
            attendeeAdapter = AttendeeAdapter()
            recyclerView.adapter = attendeeAdapter
        }

        // Observe the event list
        eventViewModel.participantList.observe(this, Observer { attendeeList ->
            binding.numberOfAttendee.text = "Participants: " + attendeeList.size.toString() + " people"
            // Update the RecyclerView adapter when the event list changes
            attendeeAdapter?.submitList(attendeeList)
        })

        // Trigger loading of the event list
        if (eventId != null) {
            eventViewModel.getParticipantsList(eventId)
        }
    }
}
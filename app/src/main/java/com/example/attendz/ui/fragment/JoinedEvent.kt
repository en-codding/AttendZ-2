package com.example.attendz.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendz.R
import com.example.attendz.ui.EventAdapter
import com.example.attendz.ui.JoinedEventAdapter
import com.example.attendz.ui.view_model.EventViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class JoinedEvent : Fragment() {
    private val auth = Firebase.auth
    private val user = auth.currentUser

    private lateinit var eventViewModel: EventViewModel
    private lateinit var recyclerView: RecyclerView
    private var joinedEventAdapter: JoinedEventAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_joined_event, container, false)

        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        // Initialize RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.joinedEventRecyclerView)

        // Set a LinearLayoutManager to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Make sure eventAdapter is initialized only once
        if (joinedEventAdapter == null) {
            joinedEventAdapter = JoinedEventAdapter()
            recyclerView.adapter = joinedEventAdapter
        }

        // Observe the event list
        eventViewModel.joinedEventList.observe(viewLifecycleOwner, Observer { joinedEventList ->
            // Update the RecyclerView adapter when the event list changes
            joinedEventAdapter?.submitList(joinedEventList)
        })
        eventViewModel.loadingState.observe(viewLifecycleOwner, Observer {
            //To Do
        })

        // Trigger loading of the event list
        eventViewModel.getJoinedEventList(user?.email.toString())

        return view
    }


}
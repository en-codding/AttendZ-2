package com.example.attendz.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.attendz.R
import com.example.attendz.databinding.FragmentCreateEventBinding
import com.example.attendz.ui.activity.EventQRActivity
import com.example.attendz.ui.view_model.Event
import com.example.attendz.ui.view_model.EventViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class CreateEvent : Fragment() {


    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels()
    private val auth = Firebase.auth
    private val user = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createEventButton.setOnClickListener{
            val eventName = binding.eventNameInput.text.toString()
            if (eventName.length < 8) {
                binding.eventNameInput.setBackgroundResource(R.drawable.input_border_red)
                binding.errorMsg.visibility = View.VISIBLE
            } else {
                val event = Event(
                    name = eventName,
                    created_at = Timestamp.now(),
                    host_email = user?.email.toString(),
                    host_name = user?.displayName.toString(),
                    ended = false,
                )
                activity?.let { it1 ->
                    eventViewModel.createEvent(event).observe(it1) { documentReference ->
                        if (documentReference != null) {
                            val createEventQrActivityIntent =
                                Intent(activity, EventQRActivity::class.java)
                            createEventQrActivityIntent.putExtra("eventId", documentReference.id)
                            createEventQrActivityIntent.putExtra("eventName", event.name)
                            startActivity(createEventQrActivityIntent)
                        } else {
                            // Handle failure
                        }
                    }
                }

            }
        }
        binding.eventNameInput.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called to notify you that the characters within s are about to be replaced with new text.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that somewhere within s, the text has been changed.
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.length >= 8) {
                        binding.eventNameInput.setBackgroundResource(R.drawable.input_border)
                        binding.errorMsg.visibility = View.INVISIBLE
                    }
                }
                // This method is called to notify you that somewhere within s, the text has been changed.
                // Do something with the entered text
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
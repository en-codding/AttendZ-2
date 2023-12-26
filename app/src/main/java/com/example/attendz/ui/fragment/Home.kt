package com.example.attendz.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.attendz.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class Home : Fragment() {
    private val auth = Firebase.auth
    private val user = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        showFragment(YourEvent())

        //get views
        val displayName = view.findViewById<TextView>(R.id.userName)
        val profileImg = view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileImg)
        displayName.text = user?.displayName ?: "user"
        Picasso.get().load(user?.photoUrl).into(profileImg)
        val yourEventButton = view.findViewById<Button>(R.id.yourEventButton)
        val participatedEventButton = view.findViewById<Button>(R.id.participatedEventButton)
        val purpleColor = activity?.let { ContextCompat.getColor(it, android.R.color.holo_purple) }

        yourEventButton.setOnClickListener{
            yourEventButton.setBackgroundResource(R.drawable.rounded_shape_purple)
            if (purpleColor != null) {
                yourEventButton.setTextColor(purpleColor)
            }
            participatedEventButton.background = null
            participatedEventButton.setTextColor(resources.getColor(R.color.black))
            showFragment(YourEvent())
        }
        participatedEventButton.setOnClickListener{
            participatedEventButton.setBackgroundResource(R.drawable.rounded_shape_purple)
            if (purpleColor != null) {
                participatedEventButton.setTextColor(purpleColor)
            }
            yourEventButton.background = null
            yourEventButton.setTextColor(resources.getColor(R.color.black))
            showFragment(JoinedEvent())
        }

        return view
    }
    private fun showFragment(fragment: Fragment) {
        val childFragmentManager = childFragmentManager
        val childFragmentTransaction = childFragmentManager.beginTransaction()

        childFragmentTransaction.replace(R.id.lytFragmentHome, fragment)

        childFragmentTransaction.commit()
    }
}
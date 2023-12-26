package com.example.attendz.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.attendz.R
import com.example.attendz.ui.activity.SignInActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class Setting : Fragment() {
    private val auth = Firebase.auth
    private val user = auth.currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        val signOutButton = view.findViewById<Button>(R.id.logOutButton)
        val profileImg = view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileImg)
        val displayName = view.findViewById<TextView>(R.id.displayName)
        displayName.text = user?.displayName
        Picasso.get().load(user?.photoUrl).into(profileImg)
        signOutButton.setOnClickListener {
            signOutAndStartSignInActivity()
        }
        return view
    }
    private fun signOutAndStartSignInActivity() {
        Firebase.auth.signOut()
        val intent = Intent(activity, SignInActivity::class.java)
        startActivity(intent)
    }

}
package com.example.attendz.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.attendz.ui.fragment.CreateEvent
import com.example.attendz.ui.fragment.Home
import com.example.attendz.ui.fragment.JoinEvent
import com.example.attendz.R
import com.example.attendz.ui.fragment.Setting
import com.example.attendz.databinding.ActivityMainBinding
import com.example.attendz.ui.view_model.EventViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    private val eventViewModel: EventViewModel by viewModels()

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mnuHome -> showFragment(Home())


                R.id.mnuCreateEvent -> showFragment(CreateEvent())

                R.id.mnuJoinEvent -> showFragment(JoinEvent())

                R.id.mnuSetting -> showFragment(Setting())

                else -> {

                }
            }
            true
        }
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            eventViewModel.getActiveEvent(user.email.toString(), false).observe(this, Observer { events ->
                if (events.isEmpty()) {
                    // The user is already signed in, navigate to MainActivity
                    showFragment(Home()) // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
                } else {
                    val (event, documentReference) = events[0]
                    val intent = Intent(this, EventQRActivity::class.java)
                    intent.putExtra("eventId", documentReference.id)
                    intent.putExtra("eventName", event.name)
                    startActivity(intent)
                    finish() // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
                }
            })
        } else {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mnuHome -> showFragment(Home())


                R.id.mnuCreateEvent ->
                    showFragment(CreateEvent())

                R.id.mnuJoinEvent -> showFragment(JoinEvent())


                R.id.mnuSetting -> showFragment(Setting())

                else -> {

                }
            }
            true
        }
    }
    private fun showFragment(fragment: Fragment) {
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.lytFragment, fragment).commitAllowingStateLoss()
    }
}
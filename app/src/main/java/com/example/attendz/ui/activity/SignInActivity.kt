package com.example.attendz.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.content.res.Configuration
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.attendz.R
import com.example.attendz.ui.view_model.EventViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import java.util.Locale

class SignInActivity : AppCompatActivity() {
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private val eventViewModel: EventViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        auth = FirebaseAuth.getInstance()



        val currentUser = auth.currentUser
        if (currentUser != null) {
            // The user is already signed in, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
        }

        val langBtn = findViewById<ImageView>(R.id.languageButton)
        var isKhmer = false

        langBtn.setOnClickListener {
            if (isKhmer) {
                // Switch back to English
                restoreLanguage()
            } else {
                // Switch to Khmer
                changeLanguage(Locale("km"))
            }
            // Toggle the language flag
            isKhmer = !isKhmer
        }

        /*val langImg = findViewById<ImageView>(R.id.language)
        langImg.setOnClickListener{
            changeLanguage(Locale("en"))
        }*/


        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private var currentLocale: Locale = Locale("en")

    private fun changeLanguage(locale: Locale) {
        val resources = resources

        // Save the current locale before changing it
        if (currentLocale.language == Locale("en").language) {
            currentLocale = resources.configuration.locale
        }

        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Finish and restart the activity
        finish()
        startActivity(intent)
    }

    private fun restoreLanguage() {
        // Check if the current locale is different from the default language
        if (currentLocale.language != Locale("en").language) {
            val resources = resources
            val configuration = Configuration(resources.configuration)
            configuration.setLocale(currentLocale)
            resources.updateConfiguration(configuration, resources.displayMetrics)

            // Finish and restart the activity
            finish()
            startActivity(intent)
        }
    }

}
package com.example.attendz.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.example.attendz.R
import com.example.attendz.databinding.ActivityEventQrActivityBinding
import com.example.attendz.ui.view_model.EventViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class EventQRActivity : AppCompatActivity() {

    private val eventViewModel: EventViewModel by viewModels()
    private lateinit var binding: ActivityEventQrActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventQrActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val eventId = intent.extras?.getString("eventId")
        val eventName = intent.extras?.getString("eventName")
        if (eventId != null && eventName != null) {
            binding.eventName.text = eventName
            generateAndDisplayQRCode(eventId)
            binding.viewAttendeeList.setOnClickListener{
                val attendeeListActivityIntent = Intent(this, AttendeeList::class.java)
                attendeeListActivityIntent.putExtra("eventId", eventId)
                attendeeListActivityIntent.putExtra("eventName", eventName)
                startActivity(attendeeListActivityIntent)
            }
        }
        binding.closeEventButton.setOnClickListener{
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            if (eventId != null) {
                showLeaveDialog(mainActivityIntent, eventId)
            }
        }
    }
    private fun generateAndDisplayQRCode(data: String) {
        try {
            // Use ZXing's MultiFormatWriter to encode the data into a BitMatrix
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 300, 300)

            // Use ZXing's BarcodeEncoder to convert the BitMatrix into a Bitmap
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)

            // Display the QR code in an ImageView
            binding.eventQr.setImageBitmap(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onBackPressed() {
        // No action is taken; back button is effectively disabled
    }
    private fun showLeaveDialog(mainActivityIntent: Intent, eventId: String) {
        val builder = AlertDialog.Builder(this)

        // Set the dialog title and message
        builder.setTitle("Confirmation")
            .setMessage("Are you sure you want to close this event?")

        // Set the positive button and its action
        builder.setPositiveButton("Confirm") { dialog, which ->
            eventViewModel.updateEventField(eventId, "ended", true)
            startActivity(mainActivityIntent)
        }

        // Set the negative/cancel button and its action
        builder.setNegativeButton("Cancel") { dialog, which ->
            // Perform the action when the "Cancel" button is clicked
            // You can put your logic here, or just dismiss the dialog
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
    }
    override fun onResume() {
        super.onResume()
    }
}
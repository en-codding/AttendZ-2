package com.example.attendz.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.attendz.databinding.FragmentJoinEventBinding
import com.example.attendz.ui.view_model.EventViewModel
import com.example.attendz.ui.view_model.Participant
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class JoinEvent : Fragment() {

    private var _binding: FragmentJoinEventBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels()
    private val auth = Firebase.auth
    private val user = auth.currentUser

    private lateinit var codeScanner: CodeScanner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJoinEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = requireActivity()
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, start custom QR code scanning
            startScanning()
        } else {
            // Request camera permission
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.CAMERA),
                100
            )
        }

    }
    private fun startScanning() {
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, binding.scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = true
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread{
                val data = Participant(
                    display_name = user?.displayName.toString(),
                    email = user?.email.toString(),
                    joined_at = Timestamp.now(),
                    event_id = it.text
                )
                eventViewModel.joinEvent(data).observe(requireActivity(), Observer {
                    Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
                })
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            activity.runOnUiThread{
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        }
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(activity, " Camera Permission Granted", Toast.LENGTH_LONG).show()
                startScanning()
            } else {
                Toast.makeText(activity, " Camera Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized){
            codeScanner?.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized){
            codeScanner?.releaseResources()
        }
        super.onPause()
    }

}
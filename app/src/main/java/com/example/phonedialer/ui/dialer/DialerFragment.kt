package com.example.phonedialer.ui.dialer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.phonedialer.databinding.FragmentDialerBinding
import com.google.android.material.snackbar.Snackbar

class DialerFragment : Fragment() {
    private var _binding: FragmentDialerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DialerViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            dialNumber()
        } else {
            Snackbar.make(
                binding.root,
                "Call permission is required to make calls",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialPad()
        setupObservers()
    }

    private fun setupDialPad() {
        with(binding) {
            button0.setOnClickListener { viewModel.appendDigit("0") }
            button1.setOnClickListener { viewModel.appendDigit("1") }
            button2.setOnClickListener { viewModel.appendDigit("2") }
            button3.setOnClickListener { viewModel.appendDigit("3") }
            button4.setOnClickListener { viewModel.appendDigit("4") }
            button5.setOnClickListener { viewModel.appendDigit("5") }
            button6.setOnClickListener { viewModel.appendDigit("6") }
            button7.setOnClickListener { viewModel.appendDigit("7") }
            button8.setOnClickListener { viewModel.appendDigit("8") }
            button9.setOnClickListener { viewModel.appendDigit("9") }
            buttonStar.setOnClickListener { viewModel.appendDigit("*") }
            buttonHash.setOnClickListener { viewModel.appendDigit("#") }
            
            backspaceButton.setOnClickListener { viewModel.removeLastDigit() }
            backspaceButton.setOnLongClickListener {
                viewModel.clearNumber()
                true
            }

            callButton.setOnClickListener { checkPermissionAndDial() }

            phoneNumberInput.setOnClickListener(null) // Disable keyboard
        }
    }

    private fun setupObservers() {
        viewModel.phoneNumber.observe(viewLifecycleOwner) { number ->
            binding.phoneNumberInput.setText(number)
        }
    }

    private fun checkPermissionAndDial() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                dialNumber()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                Snackbar.make(
                    binding.root,
                    "Call permission is required to make calls",
                    Snackbar.LENGTH_LONG
                ).setAction("Grant") {
                    requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                }.show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    private fun dialNumber() {
        val phoneNumber = viewModel.phoneNumber.value
        if (!phoneNumber.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

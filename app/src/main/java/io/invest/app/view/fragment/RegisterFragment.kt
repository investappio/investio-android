package io.invest.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentRegisterBinding

private const val TAG = "Registration"

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select your bday! 💅")
                .build()

        binding.dobInput.setOnClickListener {
            Log.d(TAG, "DOB")
            datePicker.show(childFragmentManager, TAG)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
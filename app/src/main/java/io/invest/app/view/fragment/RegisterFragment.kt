package io.invest.app.view.fragment

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.LocalStore
import io.invest.app.databinding.FragmentRegisterBinding
import io.invest.app.net.Investio
import io.invest.app.util.format
import io.invest.app.util.yearDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.toKotlinInstant
import java.util.*
import javax.inject.Inject

private const val TAG = "Registration"

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var dob = Calendar.getInstance()

    @Inject
    lateinit var investio: Investio

    @Inject
    lateinit var localStore: LocalStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.dobInput.setOnClickListener {
            datePicker.let {
                it.addOnPositiveButtonClickListener { time ->
                    dob.timeInMillis = time
                    binding.dobInput.setText(
                        dob.toInstant().toKotlinInstant()
                            .format(yearDateFormat(Locale.getDefault()))
                    )
                }

                it.show(childFragmentManager, TAG)
            }
        }

        binding.usernameInput.filters = arrayOf<InputFilter>(object : InputFilter.AllCaps() {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ) =
                source.toString().lowercase()
        })

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }

        binding.registerBtn.setOnClickListener {
            lifecycleScope.launch {
                val res = investio.register(
                    binding.nameInput.text.toString(),
                    binding.emailInput.text.toString(),
                    dob.timeInMillis,
                    binding.usernameInput.text.toString(),
                    binding.passwordInput.text.toString()
                )

                res?.let {
                    if (res.success) {
                        withContext(Dispatchers.IO) {
                            localStore.setApiToken(res.token)
                        }

                        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToPortfolioFragment())
                    }

                    Log.d(TAG, res.token)
                }
            }
        }

        return binding.root
    }

    private val datePicker
        get() =
            MaterialDatePicker.Builder.datePicker()
                .setSelection(dob.timeInMillis)
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointBackward.now())
                        .setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds()).build()
                )
                .build()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
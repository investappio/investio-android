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
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.LocalStore
import io.invest.app.databinding.FragmentLoginBinding
import io.invest.app.net.Investio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "Login"

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var investio: Investio

    @Inject
    lateinit var localStore: LocalStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.loginInput.filters = arrayOf<InputFilter>(object : InputFilter.AllCaps() {
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

        binding.registerBtn.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.loginBtn.setOnClickListener {
            lifecycleScope.launch {
                val res = investio.login(
                    binding.loginInput.text.toString(),
                    binding.passwordInput.text.toString()
                )

                res?.let {
                    if (res.success) {
                        withContext(Dispatchers.IO) {
                            localStore.setApiToken(res.token)
                        }

                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToPortfolioFragment())
                    }

                    Log.d(TAG, res.token)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
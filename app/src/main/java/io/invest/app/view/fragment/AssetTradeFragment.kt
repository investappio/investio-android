package io.invest.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentTradeAssetBinding
import io.invest.app.view.viewmodel.AssetViewModel
import io.invest.app.view.viewmodel.PortfolioViewModel
import io.invest.app.view.viewmodel.TradeViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.math.BigDecimal

@AndroidEntryPoint
class AssetTradeFragment : Fragment() {
    private var _binding: FragmentTradeAssetBinding? = null
    private val binding get() = _binding!!
    private val activity get() = requireActivity() as AppCompatActivity

    private var price = BigDecimal(0)
    private var notional = ""
    private var value = ""

    private val args: AssetDetailFragmentArgs by navArgs()
    private val assetViewModel: AssetViewModel by viewModels()
    private val tradeViewModel: TradeViewModel by viewModels()
    private val portfolioViewModel: PortfolioViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTradeAssetBinding.inflate(inflater, container, false)
        activity.setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(findNavController())
        activity.title = args.symbol

        setUpKeypad()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    assetViewModel.assetFlow.collect {
                        it[args.symbol]?.quote?.let { quote ->
                            price = quote
                        }
                    }
                }

                launch {
                    tradeViewModel.inputFlow.collect {
                        value = it
                        binding.textView.text = "\$$it"
                    }
                }

                assetViewModel.getAssets(args.symbol)
            }
        }

        return binding.root
    }

    fun setUpKeypad() {
        binding.inputDecimal.setOnClickListener {
            tradeViewModel.input('.')
        }

        binding.inputZero.setOnClickListener {
            tradeViewModel.input('0')
        }

        binding.inputDelete.setOnClickListener {
            tradeViewModel.delete()
        }

        binding.inputSeven.setOnClickListener {
            tradeViewModel.input('7')
        }

        binding.inputEight.setOnClickListener {
            tradeViewModel.input('8')
        }

        binding.inputNine.setOnClickListener {
            tradeViewModel.input('9')
        }

        binding.inputFour.setOnClickListener {
            tradeViewModel.input('4')
        }

        binding.inputFive.setOnClickListener {
            tradeViewModel.input('5')
        }

        binding.inputSix.setOnClickListener {
            tradeViewModel.input('6')
        }

        binding.inputOne.setOnClickListener {
            tradeViewModel.input('1')
        }

        binding.inputTwo.setOnClickListener {
            tradeViewModel.input('2')
        }

        binding.inputThree.setOnClickListener {
            tradeViewModel.input('3')
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
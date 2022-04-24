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
import io.invest.app.util.Side
import io.invest.app.util.ValueType
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

    private var quote = BigDecimal(0)
    private var format = ""
    private var qty = BigDecimal(0)
    private var cash = BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP)

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

        setUpKeypad()

        binding.sideToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == binding.sellToggle.id) {
                    tradeViewModel.setSide(Side.SELL)
                } else {
                    tradeViewModel.setSide(Side.BUY)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    assetViewModel.assetFlow.collect { assets ->
                        assets[args.symbol]?.quote?.let { quote ->
                            this@AssetTradeFragment.quote = quote
                        }
                    }
                }

                launch {
                    tradeViewModel.inputFlow.collect {
                        binding.tradeDisplay.text = format.format(it)
                    }
                }

                launch {
                    tradeViewModel.typeFlow.collect { type ->
                        format = if (type == ValueType.NOTIONAL) "\$%s" else "%s"
                        binding.swapToggle.setOnClickListener {
                            tradeViewModel.setType(
                                if (type == ValueType.NOTIONAL) {
                                    ValueType.QUANTITY
                                } else ValueType.NOTIONAL,
                                quote
                            )
                        }
                    }
                }

                launch {
                    portfolioViewModel.portfolioFlow.collect {
                        cash =
                            it.portfolio.cash.toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP)
                        qty = (it.portfolio.assets[args.symbol] ?: 0f).toBigDecimal()
                            .setScale(6, BigDecimal.ROUND_HALF_UP)
                        tradeViewModel.refresh()
                    }
                }

                launch {
                    tradeViewModel.sideFlow.collect { side ->
                        if (side == Side.BUY) {
                            binding.buyToggle.isChecked = true
                            binding.confirmButton.text = "Confirm Buy"
                            binding.available.text = "\$$cash\navailable to buy ${args.symbol}"
                        } else {
                            binding.sellToggle.isChecked = true
                            binding.confirmButton.text = "Confirm Sell"
                            binding.available.text = "$qty\n${args.symbol} shares available to sell"
                        }
                    }
                }

                launch {
                    tradeViewModel.successFlow.collect {
                        if (it) {
                            val action =
                                AssetTradeFragmentDirections.actionAssetTradeFragmentToPortfolioFragment()
                            findNavController().navigate(action)
                        }
                    }
                }

                binding.confirmButton.setOnClickListener {
                    launch {
                        tradeViewModel.trade(args.symbol)
                    }
                }

                assetViewModel.getAssets(args.symbol)
                portfolioViewModel.getPortfolio()
            }
        }

        return binding.root
    }

    private fun setUpKeypad() {
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
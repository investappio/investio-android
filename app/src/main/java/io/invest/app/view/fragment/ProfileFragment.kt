package io.invest.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.LocalStore
import io.invest.app.databinding.FragmentProfileBinding
import io.invest.app.net.Investio
import io.invest.app.view.adapter.OrderListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val orderList = mutableListOf<Order>()

    @Inject
    lateinit var localStore: LocalStore

    @Inject
    lateinit var investio: Investio

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val orderAdapter = OrderListAdapter(orderList)
        binding.orderList.adapter = orderAdapter

        binding.logoutBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                localStore.setApiToken("")
            }

            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
        }

        lifecycleScope.launchWhenCreated {
            launch {
                investio.getProfile()?.let { res ->
                    val profile = res.profile
                    binding.tvUser.text = profile.username
                    binding.tvName.text = profile.name
                }
            }

            launch {
                investio.getOrders()?.let { res ->
                    val orders = res.orders
                    orderList.clear()
                    orderList.addAll(orders)
                    orderAdapter.notifyItemRangeChanged(0, orders.size)
                }
            }
            investio.getOrders()?.let { res ->
                val adapter = OrderListAdapter(res.orders.toMutableList())
                binding.rvOrderList.adapter = adapter
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
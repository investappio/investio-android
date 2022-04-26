package io.invest.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentBrowseBinding
import io.invest.app.net.Investio
import io.invest.app.view.adapter.AssetSearchAdapter
import io.invest.app.view.adapter.MoversListAdapter
import io.invest.app.view.adapter.NewsListAdapter
import io.invest.app.view.viewmodel.AssetPriceModel
import io.invest.app.view.viewmodel.BrowseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Browse"

@AndroidEntryPoint
class BrowseFragment : Fragment() {
    private var _binding: FragmentBrowseBinding? = null
    private val binding get() = _binding!!
    private val browseViewModel: BrowseViewModel by viewModels()
    private val moversList : MutableList<AssetPriceModel> = mutableListOf()

    @Inject
    lateinit var investio: Investio

    lateinit var assetSearchAdapter: AssetSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowseBinding.inflate(inflater, container, false)

        assetSearchAdapter = AssetSearchAdapter(requireContext(), investio)
        binding.assetSearchInput.setAdapter(assetSearchAdapter)

        val moversAdapter = MoversListAdapter(moversList)
        binding.topMoverList.adapter = moversAdapter

        val newsAdapter = NewsListAdapter()
        binding.newsList.adapter = newsAdapter
        binding.newsList.setHasFixedSize(false)

        binding.assetSearchInput.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val asset = assetSearchAdapter.getItem(position)
            binding.assetSearchInput.setText("")

            val action =
                BrowseFragmentDirections.actionBrowseFragmentToAssetDetailFragment(asset.symbol)
            findNavController().navigate(action)
            Log.d(TAG, asset.symbol)
        }

        lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    browseViewModel.moversFlow.collect {
                        moversList.clear()
                        moversList.addAll(it)

                        Log.d(TAG, it.toString())

                        moversAdapter.notifyItemRangeChanged(0, it.size)
                    }
                }

                launch {
                    browseViewModel.newsFlow.collectLatest(newsAdapter::submitData)
                }
            }
        }

        lifecycleScope.launch {
            browseViewModel.getMovers(12)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
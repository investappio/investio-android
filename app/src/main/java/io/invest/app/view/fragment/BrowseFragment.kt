package io.invest.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentBrowseBinding
import io.invest.app.net.Investio
import io.invest.app.view.adapter.AssetSearchAdapter
import javax.inject.Inject

private const val TAG = "Browse"

@AndroidEntryPoint
class BrowseFragment : Fragment() {
    private var _binding: FragmentBrowseBinding? = null
    private val binding get() = _binding!!

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

        binding.assetSearchInput.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val asset = assetSearchAdapter.getItem(position)
            binding.assetSearchInput.setText("")

            val action =
                BrowseFragmentDirections.actionBrowseFragmentToAssetDetailFragment(asset.symbol)
            findNavController().navigate(action)
            Log.d(TAG, asset.symbol)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
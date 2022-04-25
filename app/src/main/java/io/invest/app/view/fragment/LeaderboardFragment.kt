package io.invest.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentLeaderboardBinding
import io.invest.app.view.adapter.LeaderboardAdapter
import io.invest.app.view.viewmodel.LeaderboardViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeaderboardFragment : Fragment() {
    private var _binding: FragmentLeaderboardBinding? = null
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val adapter = LeaderboardAdapter()
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        lifecycleScope.launchWhenCreated {
            launch {
                leaderboardViewModel.leaderboardFlow.collectLatest(adapter::submitData)
            }
        }

        binding.leaderboard.adapter = adapter
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
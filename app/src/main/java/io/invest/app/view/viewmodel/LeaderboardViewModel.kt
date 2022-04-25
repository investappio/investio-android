package io.invest.app.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.invest.app.net.LeaderboardPagingSource
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(leaderboardSource: LeaderboardPagingSource) :
    ViewModel() {
    val leaderboardFlow = Pager(
        config = PagingConfig(
            pageSize = 50,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { leaderboardSource }
    ).flow.cachedIn(viewModelScope)
}
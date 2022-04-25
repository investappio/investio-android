package io.invest.app.net

import androidx.paging.*
import io.invest.app.util.LeaderboardItem
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardPagingSource @Inject constructor(private val investio: Investio) :
    PagingSource<Int, LeaderboardItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LeaderboardItem> {
        val pos = params.key ?: 0

        return try {
            val res = investio.getLeaderboard(pos, params.loadSize)
            val items = res?.leaderboard ?: emptyList()

            val nextKey = if (items.isEmpty()) {
                null
            } else {
                pos + items.size
            }

            LoadResult.Page(
                data = items,
                prevKey = if (pos == 0) null else pos - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LeaderboardItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
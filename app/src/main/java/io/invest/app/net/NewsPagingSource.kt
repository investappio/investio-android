package io.invest.app.net

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.invest.app.util.News
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.IOException

class NewsPagingSource constructor(private val investio: Investio) :
    PagingSource<Long, News>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, News> {
        val pos = params.key ?: Clock.System.now().toEpochMilliseconds()

        return try {
            val res = investio.getNews(Instant.fromEpochMilliseconds(pos), params.loadSize)
            val items = res?.news ?: emptyList()

            val nextKey = if (items.isEmpty()) {
                null
            } else {
                items.last().timestamp.toEpochMilliseconds()
            }

            LoadResult.Page(
                data = items,
                prevKey = res?.prev?.toEpochMilliseconds(),
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, News>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
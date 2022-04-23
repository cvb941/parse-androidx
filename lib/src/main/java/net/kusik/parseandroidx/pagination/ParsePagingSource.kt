package net.kusik.parseandroidx.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling

/**
 *  Android paging DataSource implementation using parse LiveQuery.
 *  It will load the query and also subscribe for changes.
 *  On change, it will automatically invalidate itself and trigger a reload.
 */
class ParsePagingSource<T : ParseObject>(
    private val query: ParseQuery<T>,
    private val parseClient: ParseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
) : PagingSource<Int, T>() {

    override val jumpingSupported: Boolean = true

    private val subscriptionHandling: SubscriptionHandling<T>

    init {
        // Subscribe to query
        subscriptionHandling = parseClient.subscribe(query)

        registerInvalidatedCallback {
            parseClient.unsubscribe(query, subscriptionHandling)
        }

        subscriptionHandling.handleEvents { _, _, _ ->
            // invalidate On subscribeQuery change
            invalidate()
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        try {
            query.limit = params.loadSize
            // fetch the next set from a different offset
            query.skip = params.key ?: 0

            // synchronous call
            val posts = query.find()

            return LoadResult.Page(
                data = posts,
                prevKey = null,
                nextKey = if (posts.size < params.loadSize) null else query.skip + posts.size
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            return LoadResult.Error(e)
        }
    }
}
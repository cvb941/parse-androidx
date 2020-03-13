package com.lukaskusik.parseandroidx.pagination

import androidx.paging.PositionalDataSource
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling

/**
 *  Android paging DataSource implementation using parse LiveQuery.
 *  It will load the query and also subscribe for changes.
 *  On change, it will automatically invalidate itself and trigger a reload.
 */
class ParsePositionalDataSource<T : ParseObject>(
    private val query: ParseQuery<T>,
    private val parseClient: ParseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
) : PositionalDataSource<T>() {

    private val subscriptionHandling: SubscriptionHandling<T>

    init {
        // Subscribe to query
        subscriptionHandling = parseClient.subscribe(query)

        subscriptionHandling.handleEvents { _, _, _ ->
            // invalidate On subscribeQuery change
            invalidate()
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        // Use values passed when PagedList was created.
        query.limit = params.requestedLoadSize
        query.skip = params.requestedStartPosition

        // run queries synchronously since function is called on a background thread
        val count = query.count()
        val posts = query.find()

        // return info back to PagedList
        callback.onResult(posts, params.requestedStartPosition, count)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        query.limit = params.loadSize
        // fetch the next set from a different offset
        query.skip = params.startPosition

        // synchronous call
        val posts = query.find()

        // return info back to PagedList
        callback.onResult(posts)
    }

    override fun invalidate() {
        // Clean up the Parse LiveQuery subscription
        parseClient.unsubscribe(query, subscriptionHandling)

        super.invalidate()
    }
}
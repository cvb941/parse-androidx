package com.lukaskusik.parselivedata

import androidx.lifecycle.LiveData
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling


/**
 *  LiveData object upon a parse subscribeQuery, using LiveQuery
 *
 */
abstract class ParseLiveData<T, P : ParseObject>(
    private val parseClient: ParseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
) : LiveData<T>() {

    abstract val subscribeQuery: ParseQuery<P>

    lateinit var subscriptionHandling: SubscriptionHandling<P>

    override fun onActive() {
        // Subscribe to subscribeQuery
        subscriptionHandling = parseClient.subscribe(subscribeQuery)

        subscriptionHandling.handleEvents { _, _, _ ->
            // refresh On subscribeQuery change
            refreshLiveData()
        }

        // Initial refresh
        refreshLiveData()
    }

    override fun onInactive() {
        // Unsubscribe the subscription
        parseClient.unsubscribe(subscribeQuery, subscriptionHandling)
    }

    /**
     * Function used for loading the data from parse on subscribeQuery change
     */
    abstract fun getData(): T

    private fun refreshLiveData() {
        postValue(getData())
    }
}

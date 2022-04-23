package net.kusik.parseandroidx.livedata

import androidx.lifecycle.LiveData
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling


/**
 *  LiveData object upon a parse subscribeQuery, using LiveQuery
 *
 */
class ParseLiveData<T : ParseObject>(
    private val subscribeQuery: ParseQuery<T>,
    private val parseClient: ParseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
) : LiveData<T>() {

    private lateinit var subscriptionHandling: SubscriptionHandling<T>

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

    private fun refreshLiveData() {
        subscribeQuery.getFirstInBackground { `object`, e ->
            postValue(`object`)
        }
    }
}

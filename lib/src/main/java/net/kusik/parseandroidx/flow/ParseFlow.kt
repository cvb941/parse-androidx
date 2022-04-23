package net.kusik.parseandroidx.flow

import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

/**
 *  Flow object upon a parse subscribeQuery, using Flows
 */
class ParseFlow<T : ParseObject>(
    private val subscribeQuery: ParseQuery<T>,
    private val parseClient: ParseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
) {
    private lateinit var subscriptionHandling: SubscriptionHandling<T>

    operator fun invoke() = callbackFlow<List<T>> {
        // Subscribe to subscribeQuery
        subscriptionHandling = parseClient.subscribe(subscribeQuery)

        subscriptionHandling.handleEvents { _, _, _ ->
            // refresh On subscribeQuery change
            subscribeQuery.findInBackground { objects, e ->
                trySend(objects)
            }
        }

        awaitClose {
            // Unsubscribe the subscription
            parseClient.unsubscribe(subscribeQuery, subscriptionHandling)
        }
    }.conflate()
}

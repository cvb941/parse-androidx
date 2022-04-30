package net.kusik.parseandroidx.flow

import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 *  Flow object upon a parse subscribeQuery, using Flows
 */
fun <T : ParseObject?> ParseQuery<T>.toFlow(
    parseClient: ParseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
) = callbackFlow<List<T>> {

    // Subscribe to subscribeQuery
    val subscriptionHandling = parseClient.subscribe(this@toFlow)

    subscriptionHandling.handleEvents { _, _, _ ->
        // refresh On subscribeQuery change
        this@toFlow.findInBackground { objects, e ->
            trySend(objects)
        }
    }

    awaitClose {
        // Unsubscribe the subscription
        parseClient.unsubscribe(this@toFlow, subscriptionHandling)
    }
}
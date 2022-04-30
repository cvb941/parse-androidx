package net.kusik.parseandroidx.flow

import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.yield
import net.kusik.parseandroidx.coroutines.findSuspend

/**
 *  Flow object upon a parse subscribeQuery, using Flows
 */
fun <T : ParseObject> ParseQuery<T>.toFlow(
    parseClient: ParseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
) = callbackFlow<List<T>> {

    // LiveQuery only handles updates, load the initial object manually
    send(this@toFlow.findSuspend())
    yield() // Yield now, so that if the caller wanted only one value, we do not unnecessarily subscribe to updates

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

/**
 *  Flow object upon a parse subscribeQuery, using Flows
 */
fun <T : ParseObject> ParseQuery<T>.toFlowSingle(
    parseClient: ParseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
) = toFlow(parseClient)
    .map { if (it.size > 1) throw IllegalStateException("Result of this parse query has multiple results") else it.firstOrNull() }


package net.kusik.parseandroidx.coroutines

import com.parse.ParseObject
import com.parse.ParseQuery
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun <T : ParseObject> ParseQuery<T>.findSuspend() = suspendCoroutine<List<T>> {
    this.findInBackground { objects, e ->
        if (e == null) {
            it.resume(objects)
        } else {
            it.resumeWithException(e)
        }
    }
}

suspend fun <T : ParseObject> ParseQuery<T>.getFirstSuspend() = suspendCoroutine<T> {
    this.getFirstInBackground { `object`, e ->
        if (e == null) {
            it.resume(`object`)
        } else {
            it.resumeWithException(e)
        }
    }
}
package net.kusik.parseandroidx.coroutines

import com.parse.ParseObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun ParseObject.saveSuspend() {
    return suspendCoroutine { continuation ->
        saveInBackground {
            if (it == null) continuation.resume(Unit)
            else continuation.resumeWithException(it)
        }
    }
}
package net.kusik.parseandroidx.coroutines

import com.parse.ParseAnonymousUtils
import com.parse.ParseUser
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun ParseUser.logInSuspend(username: String, password: String) =
    suspendCoroutine<ParseUser> { cont ->
        ParseUser.logInInBackground(username, password) { user, exception ->
            if (user != null) {
                cont.resume(user)
            } else cont.resumeWithException(exception)
        }
    }

suspend fun ParseAnonymousUtils.logInSuspend() = suspendCoroutine<ParseUser> { cont ->
    ParseAnonymousUtils.logIn { user, exception ->
        if (exception != null) {
            cont.resumeWithException(exception)
        } else {
            cont.resume(user)
        }
    }
}

suspend fun ParseUser.signUpSuspend() = suspendCoroutine<Unit> { cont ->
    this.signUpInBackground { exception ->
        if (exception != null) {
            cont.resumeWithException(exception)
        } else {
            cont.resume(Unit)
        }
    }
}
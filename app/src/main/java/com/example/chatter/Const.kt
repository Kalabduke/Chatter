package com.example.chatter

import java.security.MessageDigest

/**
 * ZEGOCLOUD credentials, injected at build time from local.properties (which is
 * gitignored) - see README.md.
 *
 * Never hardcode these. The AppSign is the secret ZEGOCLOUD signs its authentication
 * tokens with, so anything that ships it - the source, a commit, an APK - lets anyone
 * mint a valid token for any user ID: they can impersonate your users in chat and
 * calls, and spend your account's minutes.
 */
val appID: Long = BuildConfig.ZEGO_APP_ID
val appSign: String = BuildConfig.ZEGO_APP_SIGN

/**
 * A ZEGO user ID derived from an email address.
 *
 * ZEGO accepts user IDs of at most 32 characters containing only letters and digits,
 * so an email cannot be used as one directly - it is too long and contains `@` and `.`.
 * Hashing gives a stable, valid ID: the same address always produces the same ID, which
 * is what lets a user start a chat or a call by typing someone else's email, without
 * needing a separate email-to-ID directory.
 *
 * Every place that identifies a user to ZEGO must go through this function, or the chat
 * and calling halves of the app register the same person under different IDs and never
 * find each other.
 */
fun zegoUserIdFor(email: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
        .digest(email.trim().lowercase().toByteArray(Charsets.UTF_8))
    return digest
        .joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
        .take(32)
}

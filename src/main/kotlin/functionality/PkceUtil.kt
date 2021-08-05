package src.main.kotlin.functionality

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*


@Throws(UnsupportedEncodingException::class)
fun generateCodeVerifier(): String {
    val secureRandom = SecureRandom()
    val codeVerifier = ByteArray(32)
    secureRandom.nextBytes(codeVerifier)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier)
}

@Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class)
fun generateCodeChallenge(codeVerifier: String): String {
    val bytes = codeVerifier.toByteArray(charset("US-ASCII"))
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(bytes, 0, bytes.size)
    val digest = messageDigest.digest()
    return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
}



package src.main.kotlin.functionality

import org.apache.commons.lang3.RandomStringUtils
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*

private const val CODE_VERIFIER_STRING =
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._~"

fun generateVerifier(length: Int): String {
    return RandomStringUtils.random(length, CODE_VERIFIER_STRING)
}




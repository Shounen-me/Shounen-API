package src.main.kotlin.utils

import org.apache.commons.lang3.RandomStringUtils

private const val CODE_VERIFIER_STRING =
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._~"

fun generateVerifier(length: Int): String {
    return RandomStringUtils.random(length, CODE_VERIFIER_STRING)
}




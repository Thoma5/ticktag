package io.ticktag.library.hashing

interface HashingLibrary {
    fun hashPassword(password: String): String
    fun checkPassword(password: String, hash: String): Boolean
}
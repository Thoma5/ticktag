package io.ticktag.library.hashing.impl

import io.ticktag.TicktagLibrary
import io.ticktag.library.hashing.HashingLibrary
import org.springframework.security.crypto.bcrypt.BCrypt

@TicktagLibrary
class HashingLibraryImpl : HashingLibrary {
    override fun checkPassword(password: String, hash: String): Boolean {
        return BCrypt.checkpw(password, hash)
    }

    override fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}
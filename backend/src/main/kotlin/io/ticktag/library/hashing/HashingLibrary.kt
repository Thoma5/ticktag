package io.ticktag.library.hashing

interface HashingLibrary {
    /**
     * Library function for hashing passwords so they can be stored in the databse
     * @param password plain text password
     * @return hashedPassword
     */
    fun hashPassword(password: String): String

    /**
     * Check if password hashed is equal the hash
     * @param password plain text password, which will be checked
     * @param hash hash which will be checked
     * @return true if given password hashed is equal to the given hash, otherwise false
     */
    fun checkPassword(password: String, hash: String): Boolean
}
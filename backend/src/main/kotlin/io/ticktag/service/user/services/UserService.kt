package io.ticktag.service.user.services

import java.util.*

interface UserService {
    fun checkPassword(mail: String, password: String): UUID?
    fun dummy(): Int  // TODO remove this!!!!
}

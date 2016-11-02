package io.ticktag.service.user.services

interface UserService {
    fun checkPassword(mail: String, password: String): Boolean
}
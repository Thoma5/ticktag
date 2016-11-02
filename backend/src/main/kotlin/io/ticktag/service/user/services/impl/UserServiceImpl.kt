package io.ticktag.service.user.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.user.services.UserService
import org.springframework.security.crypto.bcrypt.BCrypt
import javax.inject.Inject

@TicktagService
class UserServiceImpl @Inject constructor(
        private val users: UserRepository
) : UserService {

    override fun checkPassword(mail: String, password: String): Boolean {
        val user = users.findByMail(mail.toLowerCase()) ?: return false
        return BCrypt.checkpw(password, user.passwordHash)
    }
}

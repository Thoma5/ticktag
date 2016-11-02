package io.ticktag.service.user.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.user.services.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.bcrypt.BCrypt
import java.util.*
import javax.inject.Inject

@TicktagService
class UserServiceImpl @Inject constructor(
        private val users: UserRepository
) : UserService {

    @PreAuthorize(AuthExpr.ANONYMOUS)
    override fun checkPassword(mail: String, password: String): UUID? {
        val user = users.findByMail(mail.toLowerCase()) ?: return null
        if (BCrypt.checkpw(password, user.passwordHash))
            return user.id
        else
            return null
    }

    @PreAuthorize(AuthExpr.USER)
    override fun dummy(): Int {
        return Random().nextInt()
    }
}

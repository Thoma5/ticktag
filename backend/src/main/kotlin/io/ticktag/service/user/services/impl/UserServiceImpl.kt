package io.ticktag.service.user.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.user.UserRepository
import io.ticktag.persistence.user.entity.User
import io.ticktag.service.AuthExpr
import io.ticktag.service.TicktagValidationException
import io.ticktag.service.ValidationError
import io.ticktag.service.ValidationErrorDetail
import io.ticktag.service.user.dto.CreateUser
import io.ticktag.service.user.dto.UserResult
import io.ticktag.service.user.services.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.bcrypt.BCrypt
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class UserServiceImpl @Inject constructor(
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

    @PreAuthorize(AuthExpr.ADMIN)
    override fun createUser(@Valid createUser: CreateUser): UserResult {
        if (users.findByMail(createUser.mail) != null) {
            throw TicktagValidationException(listOf(ValidationError("createUser.mail", ValidationErrorDetail.Other("inuse"))))
        }

        var user = User()
        user.mail = createUser.mail.toLowerCase()
        user.name = createUser.name
        user.passwordHash = BCrypt.hashpw(createUser.password, BCrypt.gensalt())
        user = users.save(user)

        return UserResult(user)
    }

    @PreAuthorize(AuthExpr.USER)  // TODO is this correct?
    override fun getUser(id: UUID): UserResult? {
        return UserResult(users.findById(id) ?: return null)
    }

    @PreAuthorize(AuthExpr.USER)  // TODO is this correct?
    override fun getUser(mail: String): UserResult? {
        return UserResult(users.findByMail(mail) ?: return null)
    }
}

package io.ticktag.service.user.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.user.UserRepository
import io.ticktag.persistence.user.entity.Role
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
        val user = users.findByMailIgnoreCase(mail) ?: return null
        if (BCrypt.checkpw(password, user.passwordHash))
            return user.id
        else
            return null
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun createUser(@Valid createUser: CreateUser): UserResult {
        if (users.findByMailIgnoreCase(createUser.mail) != null) {
            throw TicktagValidationException(listOf(ValidationError("createUser.mail", ValidationErrorDetail.Other("inuse"))))
        }

        val mail = createUser.mail
        val name = createUser.name
        val passwordHash = BCrypt.hashpw(createUser.password, BCrypt.gensalt())
        val user = User.create(mail, passwordHash, name, Role.USER, UUID.randomUUID())
        users.insert(user)

        return UserResult(user)
    }

    @PreAuthorize(AuthExpr.USER)  // TODO is this correct?
    override fun getUser(id: UUID): UserResult? {
        return UserResult(users.findById(id) ?: return null)
    }

    @PreAuthorize(AuthExpr.USER)  // TODO is this correct?
    override fun getUser(mail: String): UserResult? {
        return UserResult(users.findByMailIgnoreCase(mail) ?: return null)
    }

    @PreAuthorize(AuthExpr.ADMIN) // TODO should probably be more granular
    override fun listUsers(): List<UserResult> {
        return users.findAll().map(::UserResult)
    }
}

package io.ticktag.service.user.services.impl

import io.ticktag.TicktagService
import io.ticktag.library.hashing.HashingLibrary
import io.ticktag.persistence.user.UserRepository
import io.ticktag.persistence.user.entity.Role
import io.ticktag.persistence.user.entity.User
import io.ticktag.service.*
import io.ticktag.service.user.dto.CreateUser
import io.ticktag.service.user.dto.RoleResult
import io.ticktag.service.user.dto.UpdateUser
import io.ticktag.service.user.dto.UserResult
import io.ticktag.service.user.services.UserService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class UserServiceImpl @Inject constructor(
        private val users: UserRepository,
        private val hashing: HashingLibrary
) : UserService {

    @PreAuthorize(AuthExpr.ANONYMOUS)
    override fun checkPassword(mail: String, password: String): UserResult? {
        val user = users.findByMailIgnoreCase(mail) ?: return null
        if (hashing.checkPassword(password, user.passwordHash))
            return UserResult(user)
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
        val passwordHash = hashing.hashPassword(createUser.password)
        val user = User.create(mail, passwordHash, name, createUser.role, UUID.randomUUID(), createUser.profilePic)
        users.insert(user)

        return UserResult(user)
    }

    @PreAuthorize(AuthExpr.USER)  // TODO is this correct?
    override fun getUser(id: UUID): UserResult? {
        return UserResult(users.findOne(id) ?: return null)
    }

    @PreAuthorize(AuthExpr.USER)  // TODO is this correct?
    override fun getUser(mail: String): UserResult? {
        return UserResult(users.findByMailIgnoreCase(mail) ?: return null)
    }

    @PreAuthorize(AuthExpr.ADMIN) // TODO should probably be more granular
    override fun listUsers(): List<UserResult> {
        return users.findAll().map(::UserResult)
    }

    @PreAuthorize(AuthExpr.ADMIN) // TODO should probably be more granular
    override fun listRoles(): List<RoleResult> {
        return Role.values().map(::RoleResult)
    }


    @PreAuthorize(AuthExpr.ADMIN_OR_SELF)
    override fun updateUser(principal: Principal, @P("userId") id: UUID, @Valid updateUser: UpdateUser): UserResult {
        val user = users.findOne(id) ?: throw NotFoundException()
        if (updateUser.password != null) {
            if (principal.hasRole("ADMIN") || (updateUser.oldPassword != null
                    && checkPassword(user.mail, updateUser.oldPassword) != null)) {  //ADMIN allowed to change password every password, Own User: Password Check
                user.passwordHash = hashing.hashPassword(updateUser.password)
            } else {
                throw TicktagValidationException(listOf(ValidationError("updateUser.oldPassword", ValidationErrorDetail.Other("passwordincorrect"))))
            }
        }

        if (updateUser.mail != null) {
            user.mail = updateUser.mail
        }

        if (updateUser.name != null) {
            user.name = updateUser.name
        }

        if (updateUser.profilePic != null) {
            user.profilePic = updateUser.profilePic
        }
        if (updateUser.role != null) {
            if (principal.hasRole("ADMIN")) {  //Only Admins can change user roles!
                user.role = updateUser.role
            } else {
                throw TicktagValidationException(listOf(ValidationError("updateUser.role", ValidationErrorDetail.Other("notpermitted"))))
            }
        }
        return UserResult(user)
    }
}

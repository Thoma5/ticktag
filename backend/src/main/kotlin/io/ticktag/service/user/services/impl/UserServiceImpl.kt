package io.ticktag.service.user.services.impl

import io.ticktag.TicktagService
import io.ticktag.library.hashing.HashingLibrary
import io.ticktag.library.unicode.NameNormalizationLibrary
import io.ticktag.persistence.user.UserRepository
import io.ticktag.persistence.user.entity.Role
import io.ticktag.persistence.user.entity.User
import io.ticktag.service.*
import io.ticktag.service.user.dto.CreateUser
import io.ticktag.service.user.dto.RoleResult
import io.ticktag.service.user.dto.UpdateUser
import io.ticktag.service.user.dto.UserResult
import io.ticktag.service.user.services.UserService
import org.springframework.data.domain.Pageable
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class UserServiceImpl @Inject constructor(
        private val users: UserRepository,
        private val hashing: HashingLibrary,
        private val nn: NameNormalizationLibrary
) : UserService {

    @PreAuthorize(AuthExpr.USER)
    override fun getUserByUsername(username: String, principal: Principal): UserResult {
        return userToDto(users.findByUsername(username) ?: throw NotFoundException(), principal)
    }

    @PreAuthorize(AuthExpr.USER)
    override fun getUsers(ids: Collection<UUID>, principal: Principal): Map<UUID, UserResult> {
        if (ids.isEmpty()) {
            return emptyMap()
        }
        return users.findByIds(ids).map({ userToDto(it, principal) }).associateBy { it.id }
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listUsersFuzzy(@P("#authProjectId") projectId: UUID, query: String, pageable: Pageable, principal: Principal): List<UserResult> {
        return users.findByProjectIdAndFuzzy(projectId, "%$query%", "%$query%", "%${nn.normalize(query)}%", pageable)
                .map({ userToDto(it, principal) })
    }

    @PreAuthorize(AuthExpr.ANONYMOUS)
    override fun checkPassword(mail: String, password: String): UserResult? {
        val user = users.findByMailIgnoreCase(mail) ?: return null
        if (hashing.checkPassword(password, user.passwordHash)) {
            // This is the only function that may bypass the userToDto function
            // We just checked the password so we can return all the information
            return UserResult(user)
        } else {
            return null
        }
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun createUser(@Valid createUser: CreateUser, principal: Principal): UserResult {
        if (users.findByMailIgnoreCase(createUser.mail) != null) {
            throw TicktagValidationException(listOf(ValidationError("createUser.mail", ValidationErrorDetail.Other("inuse"))))
        }
        if (users.findByUsername(createUser.username) != null) {
            throw TicktagValidationException(listOf(ValidationError("createUser.username", ValidationErrorDetail.Other("inuse"))))
        }

        val mail = createUser.mail
        val name = createUser.name
        val passwordHash = hashing.hashPassword(createUser.password)
        val user = User.create(mail, passwordHash, name, createUser.username, createUser.role, UUID.randomUUID(), createUser.profilePic)
        users.insert(user)

        return userToDto(user, principal)
    }

    @PreAuthorize(AuthExpr.USER)
    override fun getUser(id: UUID, principal: Principal): UserResult {
        return userToDto((users.findOne(id) ?: throw NotFoundException()), principal)
    }

    @PreAuthorize(AuthExpr.ADMIN) // TODO should probably be more granular
    override fun listUsers(principal: Principal): List<UserResult> {
        return users.findAll().map({ userToDto(it, principal) })
    }

    @PreAuthorize(AuthExpr.ADMIN) // TODO should probably be more granular
    override fun listRoles(): List<RoleResult> {
        return Role.values().map(::RoleResult)
    }

    @PreAuthorize(AuthExpr.ADMIN_OR_SELF)
    override fun updateUser(principal: Principal, @P("userId") id: UUID, @Valid updateUser: UpdateUser): UserResult {
        val user = users.findOne(id) ?: throw NotFoundException()
        if (updateUser.password != null) {
            //ADMIN allowed to change password every password, Own User: Password Check
            val isAdmin = principal.hasRole("ADMIN")
            val oldPasswordMatches = (updateUser.oldPassword != null && checkPassword(user.mail, updateUser.oldPassword) != null)
            if (isAdmin || oldPasswordMatches) {
                unsafeUpdateUserPassword(user, updateUser.password)
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
            if (principal.hasRole(AuthExpr.ROLE_GLOBAL_ADMIN)) {  //Only Admins can change user roles!
                user.role = updateUser.role
            } else {
                throw TicktagValidationException(listOf(ValidationError("updateUser.role", ValidationErrorDetail.Other("notpermitted"))))
            }
        }
        return userToDto(user, principal)
    }

    private fun userToDto(user: User, principal: Principal): UserResult {
        val isGlobalObserver = principal.hasRole(AuthExpr.ROLE_GLOBAL_OBSERVER)
        val isSelf = principal.isId(user.id)
        val viewedUserProjects = user.memberships.map { it.project }
        val callingUserProjects = users.findOne(principal.id)?.memberships?.map { it.project } ?: emptyList()
        val haveCommonProject = viewedUserProjects.intersect(callingUserProjects).isNotEmpty()

        if (isGlobalObserver || isSelf || haveCommonProject) {
            return UserResult(user)
        } else {
            return UserResult(user).copy(mail = null)
        }
    }

    private fun unsafeUpdateUserPassword(user: User, newPassword: String) {
        user.passwordHash = hashing.hashPassword(newPassword)
        user.currentToken = UUID.randomUUID()
    }
}

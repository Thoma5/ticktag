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
import java.awt.Color
import java.awt.Font
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.*
import javax.imageio.ImageIO
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class UserServiceImpl @Inject constructor(
        private val users: UserRepository,
        private val hashing: HashingLibrary,
        private val nn: NameNormalizationLibrary
) : UserService {

    @PreAuthorize(AuthExpr.USER)  // TODO maybe refine
    override fun getUserImage(id: UUID): ByteArray {
        val user = users.findOne(id) ?: throw NotFoundException()
        return user.image?.image ?: generateDefaultImagePng(user)
    }

    @PreAuthorize(AuthExpr.USER)  // TODO maybe refine
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
    override fun listUsersFuzzy(@P("authProjectId") projectId: UUID, query: String, pageable: Pageable, principal: Principal): List<UserResult> {
        return users.findByProjectIdAndFuzzy(projectId, query, query, query, pageable)
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
        val user = User.create(mail, passwordHash, name, createUser.username, createUser.role, UUID.randomUUID())
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

    private fun generateDefaultImagePng(user: User): ByteArray {
        val hash = ByteBuffer.wrap(MessageDigest.getInstance("SHA-1").digest(user.username.toByteArray()))
        val alpha = intToDouble(hash.getInt(0), 0.0, Math.PI)
        val hue0 = intToDouble(hash.getInt(4), 0.0, 1.0).toFloat()
        val hue1 = intToDouble(hash.getInt(8), 0.0, 1.0).toFloat()
        val letter = "${user.username[0].toUpperCase()}"

        val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB)
        val g = image.createGraphics()

        g.scale(image.width.toDouble(), image.height.toDouble())
        g.translate(0.5, 0.5)
        g.rotate(alpha)
        g.color = Color.getHSBColor(hue0, 0.5f, 1.0f)
        g.fill(Rectangle2D.Double(-2.0, -2.0, 4.0, 4.0))
        g.color = Color.getHSBColor(hue1, 0.5f, 1.0f)
        g.fill(Rectangle2D.Double(0.0, -2.0, 2.0, 4.0))

        g.transform = AffineTransform()
        val font = Font("Arial", Font.PLAIN, 72)
        g.font = font
        g.color = Color.BLACK
        val bounds = g.fontMetrics.getStringBounds(letter, g)
        g.drawString(
                letter,
                image.width.toFloat() / 2 - bounds.width.toFloat() / 2,
                image.height.toFloat() / 2 - bounds.height.toFloat() / 2 - bounds.minY.toFloat())

        val bos = ByteArrayOutputStream()
        ImageIO.write(image, "png", bos)
        bos.close()
        return bos.toByteArray()
    }

    private fun intToDouble(i: Int, from: Double, to: Double): Double {
        assert(from <= to)
        var d = i.toDouble()  // [-2^31, 2^31)
        d -= Int.MIN_VALUE.toDouble()  // [0, 2^32)
        d /= Int.MAX_VALUE.toDouble() - Int.MIN_VALUE.toDouble()  // [0, 1)
        d *= to - from  // [0, to-from)
        d += from  // [from, to)
        return d
    }
}

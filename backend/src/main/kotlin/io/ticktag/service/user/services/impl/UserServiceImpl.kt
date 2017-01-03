package io.ticktag.service.user.services.impl

import io.ticktag.ApplicationProperties
import io.ticktag.TicktagService
import io.ticktag.library.base64ImageDecoder.Base64ImageDecoder
import io.ticktag.library.hashing.HashingLibrary
import io.ticktag.persistence.member.MemberRepository
import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.user.UserImageRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.persistence.user.entity.Role
import io.ticktag.persistence.user.entity.User
import io.ticktag.persistence.user.entity.UserImage
import io.ticktag.service.*
import io.ticktag.service.user.dto.*
import io.ticktag.service.user.services.UserService
import org.apache.commons.codec.binary.Hex
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.encrypt.BytesEncryptor
import org.springframework.security.crypto.encrypt.Encryptors
import java.awt.Color
import java.awt.Font
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.imageio.ImageIO
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class UserServiceImpl @Inject constructor(
        private val users: UserRepository,
        private val userimages: UserImageRepository,
        private val members: MemberRepository,
        private val projects: ProjectRepository,
        private val hashing: HashingLibrary,
        private val props: ApplicationProperties,
        private val base64ImageDecoder: Base64ImageDecoder,
        private val clock: Clock
) : UserService {
    companion object {
        val IMAGE_ID_VALID_DURATION: Duration = Duration.ofDays(1)
        val MAX_IMAGE_SIZE: Int = 512000 //Byte
    }

    // Authorization is performed via the temp image ids which are kept secret and expire
    @PreAuthorize(AuthExpr.ANONYMOUS)
    override fun getUserImage(imageId: TempImageId): ByteArray {
        val (userId, timestamp) = decodeTempImageId(imageId) ?: throw AccessDeniedException("Invalid id")
        val now = Instant.now(clock)
        val latestValidTime = now.minus(IMAGE_ID_VALID_DURATION)
        if (timestamp.isBefore(latestValidTime)) {
            throw AccessDeniedException("Expired id")
        }
        val user = users.findOne(userId) ?: throw NotFoundException()
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
        return usersToDto(users.findByIds(ids), principal).associateBy { it.id }
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listUsersFuzzy(@P("authProjectId") projectId: UUID, query: String, pageable: Pageable, principal: Principal): List<UserResult> {
        return usersToDto(users.findByProjectIdAndFuzzyAndStatusEnabled(projectId, query, query, query, pageable), principal)
    }

    @PreAuthorize(AuthExpr.ANONYMOUS)
    override fun checkPassword(mail: String, password: String): UserResult? {
        val user = users.findByMailIgnoreCase(mail) ?: return null
        if (user.disabled) return null
        if (hashing.checkPassword(password, user.passwordHash)) {
            // This is the only function that may bypass the userToDto function
            // We just checked the password so we can return all the information
            return UserResult(user, encodeTempImageId(user.id))
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
        if (createUser.image != null) {
            var image: ByteArray? = null
            if (createUser.image.isNotEmpty()) {
                image = base64ImageDecoder.decode(createUser.image).image
                if (image.size > MAX_IMAGE_SIZE) throw TicktagValidationException(listOf(ValidationError("createUser.image", ValidationErrorDetail.Other("maxsize" + MAX_IMAGE_SIZE / 1000 + "KB"))))
            }
            var userimage = userimages.findOne(user.id)
            if (userimage == null && image != null) {
                userimage = UserImage.create(user.id, image)
                userimages.insert(userimage)
            } else if (userimage != null && image != null) {
                userimage.image = image
            }
        }

        return userToDto(user, principal)
    }

    @PreAuthorize(AuthExpr.USER)
    override fun getUser(id: UUID, principal: Principal): UserResult {
        return userToDto((users.findOne(id) ?: throw NotFoundException()), principal)
    }

    @PreAuthorize(AuthExpr.ADMIN) // TODO should probably be more granular
    override fun listUsers(query: String, role: Role?, disabled: Boolean?, principal: Principal, pageable: Pageable): Page<UserResult> {
        val page: Page<User>
        val q = "%" + query.replace("_", "\\_", false).replace("%", "\\%", false).toLowerCase() + "%"
        if (role == null) {
            if (disabled == null) {
                page = users.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrMailContainingIgnoreCase(query, query, query, pageable)
            } else page = users.findAllByStatusAndQuery(q, disabled, pageable)
        } else {
            if (disabled == null) {
                page = users.findAllByRoleAndQuery(q, role, pageable)
            } else page = users.findAllByRoleAndStatusAndQuery(q, disabled, role, pageable)
        }
        val content = usersToDto(page.content, principal)
        return PageImpl(content, pageable, page.totalElements)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listProjectUsers(@P("authProjectId") projectId: UUID, disabled: Boolean?, principal: Principal): List<ProjectUserResult> {
        val project = projects.findOne(projectId) ?: throw NotFoundException()
        val projectMemberships: List<Member>
        if (disabled == null) {
            projectMemberships = members.findByProject(project) ?: throw NotFoundException()
        } else {
            if (disabled) {
                projectMemberships = members.findByProjectAndUserDisabledIsAndRoleIs(project, disabled, ProjectRole.NONE) ?: throw NotFoundException()
            } else {
                projectMemberships = members.findByProjectAndUserDisabledIsAndRoleNot(project, disabled, ProjectRole.NONE) ?: throw NotFoundException()
            }
        }
        val projectUserResult: MutableList<ProjectUserResult> = mutableListOf()
        projectMemberships.map { e -> projectUserResult.add(ProjectUserResult(UserResult(e.user, encodeTempImageId(e.user.id)), e)) }
        return projectUserResult
    }


    @PreAuthorize(AuthExpr.ADMIN) // TODO should probably be- more granular
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

        if (updateUser.disabled != null) {
            if (!principal.isId(id)) { //effectively just usable from the admin since user (self) has no access in this state
                user.disabled = updateUser.disabled
                user.currentToken = UUID.randomUUID()
            } else if (principal.isId(id) && updateUser.disabled != user.disabled) {
                throw TicktagValidationException(listOf(ValidationError("updateUser.disabled", ValidationErrorDetail.Other("notpermitted"))))
            }
        }

        if (updateUser.role != null) {
            if (principal.hasRole(AuthExpr.ROLE_GLOBAL_ADMIN) && !principal.isId(id)) {  //Only Admins can change user roles!
                user.role = updateUser.role
            } else if (principal.isId(id) && updateUser.role != user.role) {
                throw TicktagValidationException(listOf(ValidationError("updateUser.role", ValidationErrorDetail.Other("notpermitted"))))
            }
        }
        if (updateUser.image != null) {
            var image: ByteArray? = null
            if (updateUser.image.isNotEmpty()) {
                image = base64ImageDecoder.decode(updateUser.image).image
                if (image.size > MAX_IMAGE_SIZE) throw TicktagValidationException(listOf(ValidationError("updateUser.image", ValidationErrorDetail.Other("maxsize" + MAX_IMAGE_SIZE + "Bytes"))))
            }

            var userimage = userimages.findOne(user.id)
            if (userimage == null && image != null) {
                userimage = UserImage.create(user.id, image)
                userimages.insert(userimage)
            } else if (userimage != null && image != null) {
                userimage.image = image
            } else if (userimage != null && image == null) {
                userimages.delete(userimage)
            }
        }
        return userToDto(user, principal)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun deleteUser(id: UUID, principal: Principal) {
        if (principal.isId(id)) throw TicktagValidationException(listOf(ValidationError("deleteUser", ValidationErrorDetail.Other("notpermitted"))))
        val userToDelete = users.findOne(id) ?: throw NotFoundException()
        val memberships = members.findByUserAndRoleNot(userToDelete, ProjectRole.NONE) ?: throw NotFoundException()
        memberships.map { m -> m.role = ProjectRole.NONE }
        userToDelete.currentToken = UUID.randomUUID()
        userToDelete.disabled = true
    }

    private fun usersToDto(us: List<User>, principal: Principal): List<UserResult> {
        val allIds = listOf(principal.id) + us.map(User::id)
        val projects = projects.findByUserIds(allIds)

        return us.map { userToDtoInternal(it, projects, principal) }
    }

    private fun userToDto(user: User, principal: Principal): UserResult {
        return usersToDto(listOf(user), principal)[0]
    }

    private fun userToDtoInternal(user: User,
                                  projects: Map<UUID, List<Project>>,
                                  principal: Principal): UserResult {
        val isGlobalObserver = principal.hasRole(AuthExpr.ROLE_GLOBAL_OBSERVER)
        val isSelf = principal.isId(user.id)
        val viewedUserProjects = projects[user.id] ?: emptyList()
        val callingUserProjects = projects[principal.id] ?: emptyList()
        val haveCommonProject = viewedUserProjects.intersect(callingUserProjects).isNotEmpty()

        if (isGlobalObserver || isSelf || haveCommonProject) {
            return UserResult(user, encodeTempImageId(user.id))
        } else {
            return UserResult(user, encodeTempImageId(user.id)).copy(mail = null)
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

    private fun decodeTempImageId(id: TempImageId): Pair<UUID, Instant>? {
        val encryptor = getTempImageIdEncryptor()
        try {
            val decryptedData = encryptor.decrypt(id.data)

            if (decryptedData.size != 28) {
                return null
            }

            val buffer = ByteBuffer.wrap(decryptedData)
            val idMsbs = buffer.getLong(0)
            val idLsbs = buffer.getLong(8)
            val timeSeconds = buffer.getLong(16)
            val timeNanos = buffer.getInt(24)

            // Nanos are stored as an int and the getter returns an int, but the constructor takes a long
            return Pair(UUID(idMsbs, idLsbs), Instant.ofEpochSecond(timeSeconds, timeNanos.toLong()))
        } catch (e: Exception) {
            // Yes we blanket catch exceptions here because encryptor.decrypt() can apparently fail when provided with
            // bad data but we don't know what it can fail with
            return null
        }
    }

    private fun encodeTempImageId(id: UUID, time: Instant = Instant.now(clock)): TempImageId {
        val buffer = ByteBuffer.allocate(16 + 12)
        buffer.putLong(id.mostSignificantBits)
        buffer.putLong(id.leastSignificantBits)
        buffer.putLong(time.epochSecond)
        buffer.putInt(time.nano)
        val bytes = buffer.array()

        val encryptor = getTempImageIdEncryptor()
        val encryptedBytes = encryptor.encrypt(bytes)
        return TempImageId(encryptedBytes)
    }

    private fun getTempImageIdEncryptor(): BytesEncryptor {
        // We don't have a salt and we don't want/need one
        val secret = Hex.encodeHexString(props.serverImageSecret.toByteArray(charset = Charsets.US_ASCII))
        return Encryptors.standard(secret, secret)
    }
}
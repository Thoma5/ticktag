package io.ticktag.service.fallbackadmin.services.impl

import io.ticktag.ApplicationProperties
import io.ticktag.TicktagService
import io.ticktag.library.hashing.HashingLibrary
import io.ticktag.persistence.user.UserRepository
import io.ticktag.persistence.user.entity.Role
import io.ticktag.persistence.user.entity.User
import io.ticktag.service.AuthExpr
import io.ticktag.service.fallbackadmin.services.FallbackAdminService
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject

@TicktagService
open class FallbackAdminServiceImpl @Inject constructor(
        private val properties: ApplicationProperties,
        private val users: UserRepository,
        private val hashing: HashingLibrary
) : FallbackAdminService {
    companion object {
        private val LOG = LoggerFactory.getLogger(FallbackAdminServiceImpl::class.java)
    }

    @PreAuthorize(AuthExpr.INTERNAL)
    override fun ensureAdminExists() {
        LOG.info("Ensuring fallback admin exists")
        val zeroId = UUID(0, 0)

        val mail = properties.adminMail
        val hash = hashing.hashPassword(properties.adminPassword)
        val existingAdmin = users.findOne(zeroId)
        val admin = if (existingAdmin == null) {
            LOG.info("No existing fallback admin, creating a new one (first start?)")
            val newAdmin = User.createWithId(zeroId, mail, hash, "Admin", Role.ADMIN, UUID.randomUUID())
            users.insert(newAdmin)
            newAdmin
        } else {
            existingAdmin
        }
        LOG.info("Resetting fallback admin")
        // Reset mail and password
        admin.mail = mail
        admin.passwordHash = hash
        // Reset projectRole to always keep him admin
        admin.role = Role.ADMIN
        // Invalidate old sessions
        admin.currentToken = UUID.randomUUID()
        LOG.info("Fallback admin set successfully")
    }
}
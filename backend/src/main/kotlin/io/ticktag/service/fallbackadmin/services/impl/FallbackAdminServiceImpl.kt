package io.ticktag.service.fallbackadmin.services.impl

import io.ticktag.ApplicationProperties
import io.ticktag.library.hashing.HashingLibrary
import io.ticktag.persistence.user.UserRepository
import io.ticktag.persistence.user.entity.Role
import io.ticktag.persistence.user.entity.User
import io.ticktag.service.fallbackadmin.services.FallbackAdminService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.inject.Inject

@Component
open class FallbackAdminServiceImpl @Inject constructor(
        private val properties: ApplicationProperties,
        private val users: UserRepository,
        private val hashing: HashingLibrary
) : FallbackAdminService {
    companion object {
        private val LOG = LoggerFactory.getLogger(FallbackAdminServiceImpl::class.java)
    }

    @Transactional
    override fun ensureAdminExists() {
        LOG.info("Ensuring fallback admin exists")
        val zeroId = UUID(0, 0)

        val mail = properties.adminMail
        val hash = hashing.hashPassword(properties.adminPassword)
        val existingAdmin = users.findById(zeroId)
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
        // Reset role to always keep him admin
        admin.role = Role.ADMIN
        // Invalidate old sessions
        admin.currentToken = UUID.randomUUID()
        LOG.info("Fallback admin set successfully")
    }
}
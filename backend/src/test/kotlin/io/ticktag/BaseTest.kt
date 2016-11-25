package io.ticktag

import com.google.common.io.Resources
import io.ticktag.persistence.loggedtime.LoggedTimeRepository
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.member.MemberRepository
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.timecategory.TimeCategoryRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.Principal
import io.ticktag.util.tryw
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.sql.Connection
import java.util.*
import javax.inject.Inject
import javax.sql.DataSource


@RunWith(SpringJUnit4ClassRunner::class)
abstract class BaseTest {
    companion object {
        private var INIT_DB_DONE = false

        private fun initDb(connection: Connection) {
            synchronized(INIT_DB_DONE) {
                if (!INIT_DB_DONE) {
                    INIT_DB_DONE = true

                    execResource(connection, "drop.sql")
                    execResource(connection, "schema.sql")
                }
            }
        }

        private fun readResource(name: String): String? {
            val resUrl = Resources.getResource(name) ?: return null
            return Resources.toString(resUrl, Charsets.UTF_8)
        }

        private fun execResource(connection: Connection, name: String) {
            val sql = readResource(name)!!
            val stmt = connection.prepareStatement(sql)
            stmt.execute()
            stmt.close()
        }
    }

    @Inject lateinit var datasource: DataSource
    @Inject lateinit var users: UserRepository
    @Inject lateinit var members: MemberRepository
    @Inject lateinit var comments: CommentRepository
    @Inject lateinit var assignmenttags: AssignmentTagRepository
    @Inject lateinit var loggedTimes : LoggedTimeRepository
    @Inject lateinit var timeCategories: TimeCategoryRepository


    @Before
    fun setUp() {
        datasource.connection.tryw({
            initDb(it)
            execResource(it, "samples.sql")
        })
    }

    protected fun <T> withUser(userId: UUID, proc: () -> T): T {
        return withUser(userId) { principal ->
            proc()
        }
    }

    protected fun <T> withUser(userId: UUID, proc: (Principal) -> T): T {
        if (SecurityContextHolder.getContext().authentication != null)
            throw RuntimeException("Called withUser even though the security context is already set")

        val user = users.findOne(userId) ?: throw RuntimeException("Called withUser with an unknown user UUID")
        val principal = Principal(user.id, user.role, members, comments, assignmenttags, timeCategories, loggedTimes)

        SecurityContextHolder.getContext().authentication = PreAuthenticatedAuthenticationToken(principal, null, emptySet())
        try {
            return proc(principal)
        } finally {
            SecurityContextHolder.getContext().authentication = null
        }
    }

    protected fun <T> withoutUser(proc: () -> T): T {
        if (SecurityContextHolder.getContext().authentication != null)
            throw RuntimeException("Called withoutUser even though the security context is already set")

        SecurityContextHolder.getContext().authentication = PreAuthenticatedAuthenticationToken(Principal.INTERNAL, null, emptySet())
        try {
            return proc()
        } finally {
            SecurityContextHolder.getContext().authentication = null
        }
    }
}

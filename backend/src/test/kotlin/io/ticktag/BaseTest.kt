package io.ticktag

import com.google.common.io.Resources
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.sql.Connection
import javax.inject.Inject
import javax.sql.DataSource


@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = arrayOf(TicktagTestApplication::class))
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

    @Before
    fun setUp() {
        val connection = datasource.connection
        initDb(connection)

        execResource(connection, "samples.sql")
    }
}

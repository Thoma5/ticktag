package io.ticktag

import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.io.BufferedReader
import java.io.InputStreamReader
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

                    val fpdrop = BufferedReader(InputStreamReader(BaseTest::class.java.classLoader.getResourceAsStream("drop.sql")))
                    val drop = fpdrop.readText()
                    fpdrop.close()

                    val stmtdrop = connection.prepareStatement(drop)
                    stmtdrop.execute()
                    stmtdrop.close()

                    val fp = BufferedReader(InputStreamReader(BaseTest::class.java.classLoader.getResourceAsStream("schema.sql")))
                    val creates = fp.readText()
                    fp.close()

                    val stmt = connection.prepareStatement(creates)
                    stmt.execute()
                    stmt.close()
                }
            }
        }
    }

    @Inject lateinit var datasource: DataSource

    @Before
    fun setUp() {
        val connection = datasource.connection
        initDb(connection)

        val fp = BufferedReader(InputStreamReader(BaseTest::class.java.classLoader.getResourceAsStream("samples.sql")))
        val creates = fp.readText()
        fp.close()

        val stmt = connection.prepareStatement(creates)
        stmt.execute()
        stmt.close()
    }
}

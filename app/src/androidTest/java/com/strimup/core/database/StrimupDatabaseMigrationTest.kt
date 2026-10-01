package com.strimup.core.database

import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StrimupDatabaseMigrationTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        instrumentation = instrumentation,
        file = instrumentation.targetContext.getDatabasePath(TEST_DATABASE_NAME),
        driver = AndroidSQLiteDriver(),
        databaseClass = StrimupDatabase::class,
    )

    @Test
    fun migratingFromTheFirstVersionShouldMatchTheCurrentSchema() = runTest {
        migrationTestHelper.createDatabase(FIRST_VERSION).close()

        migrationTestHelper
            .runMigrationsAndValidate(STRIMUP_DATABASE_VERSION, StrimupDatabaseMigrations.all)
            .close()
    }

    private companion object {
        const val TEST_DATABASE_NAME = "strimup-migration-test"
        const val FIRST_VERSION = 1
    }
}

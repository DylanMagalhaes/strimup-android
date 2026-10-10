package com.strimup.core.database

import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
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

    @Before
    fun deletePreviousTestDatabase() {
        instrumentation.targetContext.deleteDatabase(TEST_DATABASE_NAME)
    }

    @Test
    fun migratingFromTheFirstVersionShouldMatchTheCurrentSchema() = runTest {
        migrationTestHelper.createDatabase(FIRST_VERSION).close()

        migrationTestHelper
            .runMigrationsAndValidate(STRIMUP_DATABASE_VERSION, StrimupDatabaseMigrations.all)
            .close()
    }

    @Test
    fun migratingFrom1To2ShouldKeepExistingFavoritesAndCreateEmptyHomeTables() = runTest {
        migrationTestHelper.createDatabase(FIRST_VERSION).use { connection ->
            connection.execSQL("INSERT INTO favorite (id, userName, imageUrl) VALUES ('42', 'Inox', NULL)")
        }

        migrationTestHelper.runMigrationsAndValidate(2, listOf(StrimupDatabaseMigrations.MIGRATION_1_2)).use { connection ->
            assertThat(connection.count("favorite")).isEqualTo(1)
            assertThat(connection.count("home_banner")).isEqualTo(0)
            assertThat(connection.count("home_streamers")).isEqualTo(0)
        }
    }

    private fun SQLiteConnection.count(table: String): Long =
        prepare("SELECT COUNT(*) FROM `$table`").use { statement ->
            statement.step()
            statement.getLong(0)
        }

    private companion object {
        const val TEST_DATABASE_NAME = "strimup-migration-test"
        const val FIRST_VERSION = 1
    }
}

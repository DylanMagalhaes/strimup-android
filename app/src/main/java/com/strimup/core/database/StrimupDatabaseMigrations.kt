package com.strimup.core.database

import androidx.room3.migration.Migration

const val STRIMUP_DATABASE_NAME = "strimup_database"
const val STRIMUP_DATABASE_VERSION = 1

object StrimupDatabaseMigrations {
    val all: List<Migration> = emptyList()
}

package com.strimup.core.database

import androidx.room3.migration.Migration
import androidx.sqlite.execSQL

const val STRIMUP_DATABASE_NAME = "strimup_database"
const val STRIMUP_DATABASE_VERSION = 3

object StrimupDatabaseMigrations {

    val MIGRATION_1_2: Migration = Migration(startVersion = 1, endVersion = 2) { connection ->
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `home_banner` (" +
                "`orderIndex` INTEGER NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, " +
                "`imageUrl` TEXT NOT NULL, `position` INTEGER NOT NULL, `linkUrl` TEXT NOT NULL, " +
                "`type` TEXT NOT NULL, `avatarUrl` TEXT, `streamerId` TEXT, PRIMARY KEY(`orderIndex`))"
        )
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `home_streamers` (" +
                "`id` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, `userName` TEXT NOT NULL, " +
                "`imageUrl` TEXT, `socials` TEXT NOT NULL, `tags` TEXT NOT NULL, " +
                "`personality` TEXT, `personalitySecondary` TEXT, PRIMARY KEY(`id`))"
        )
    }

    val MIGRATION_2_3: Migration = Migration(startVersion = 2, endVersion = 3) { connection ->
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `recent_streamers` (" +
                "`id` TEXT NOT NULL, `userName` TEXT NOT NULL, `imageUrl` TEXT, " +
                "`viewedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        )
    }

    val all: List<Migration> = listOf(MIGRATION_1_2, MIGRATION_2_3)
}

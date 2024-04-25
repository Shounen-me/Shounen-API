package src.main.kotlin.database.postgres

import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database

object DatabaseSingleton {
    fun connect(config: ApplicationConfig) {
        // TODO: Fix property not found
//        val driverClassName = config.property("storage.driverClassName").getString()
//        val jdbcURL = config.property("storage.jdbcURL").getString()
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://db:5432/shounen?user=postgres"
        Database.connect(jdbcURL, driverClassName)
    }
}

package src.main.kotlin.database.postgres

import org.jetbrains.exposed.sql.Database
import java.nio.file.Files
import java.nio.file.Path

private val login: List<String> = Files.readAllLines(Path.of("src/main/resources/secrets/DB_Login.txt"))

fun connect() = Database.connect(login[0], driver = login[1], user = login[2], password = login[3])

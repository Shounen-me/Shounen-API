package src.main.kotlin.utils

import java.nio.file.Files
import java.nio.file.Path

object SecretUtils {
    val clientId: String = Files.readAllLines(Path.of("src/main/resources/secrets/mal.txt"))[0]
    val clientSecret: String = Files.readAllLines(Path.of("src/main/resources/secrets/mal.txt"))[1]
    val authorized_token: List<String> = Files.readAllLines(Path.of("src/main/resources/secrets/authorized_keys.txt"))
}
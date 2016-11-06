package io.ticktag

interface ApplicationProperties {
    val dbUrl: String
    val dbUser: String
    val dbPassword: String

    val httpPort: Int

    val serverSecret: String
    val serverNumber: Int

    val adminMail: String
    val adminPassword: String
}
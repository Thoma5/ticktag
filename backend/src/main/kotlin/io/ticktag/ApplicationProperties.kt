package io.ticktag

interface ApplicationProperties {
    val dbUrl: String
    val dbUser: String
    val dbPassword: String
    val dbValidate: Boolean

    val httpPort: Int
    val httpSlow: Boolean

    val serverSecret: String
    val serverNumber: Int

    val adminMail: String
    val adminPassword: String
}
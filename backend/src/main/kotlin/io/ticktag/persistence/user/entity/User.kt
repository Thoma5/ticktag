package io.ticktag.persistence.user.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "user")
open class User {
    @Id
    @Column(name = "id")
    open val id: UUID = UUID.randomUUID()

    @Column(name = "mail")
    open val mail: String = ""

    @Column(name = "password_hash")
    open val passwordHash: String = ""
}

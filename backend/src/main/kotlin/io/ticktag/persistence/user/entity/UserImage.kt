package io.ticktag.persistence.user.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_image")
open class UserImage protected constructor() {
    @Id
    @Column(name = "user_id")
    protected lateinit open var userId: UUID

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    lateinit open var user: User

    @Column(name = "image", nullable = false)
    lateinit open var image: ByteArray
}

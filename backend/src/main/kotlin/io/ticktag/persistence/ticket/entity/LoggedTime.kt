package io.ticktag.persistence.ticket.entity

import java.time.Duration
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "logged_time")
open class LoggedTime protected constructor() {
    companion object {
        fun create(time: Duration, comment: Comment, category: TimeCategory): LoggedTime {
            val o = LoggedTime()
            o.id = UUID.randomUUID()
            o.time = time
            o.comment = comment
            o.category = category
            return o
        }
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @Column(name = "time")
    lateinit open var time: Duration

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id", nullable = false)
    lateinit open var comment: Comment

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    lateinit open var category: TimeCategory
}

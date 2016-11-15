package io.ticktag.persistence.ticket.entity

import io.ticktag.persistence.user.entity.User
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "comment")
open class Comment protected constructor() {
    companion object {
        fun create(createTime: Instant, text: String, user: User, ticket: Ticket): Comment {
            val o = Comment()
            o.id = UUID.randomUUID()
            o.createTime = createTime
            o.text = text
            o.user = user
            o.ticket = ticket
            o.describedTicket = null
            o.mentionedTickets = mutableListOf()
            o.loggedTimes = mutableListOf()
            return o
        }
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @Column(name = "create_time", nullable = false)
    lateinit open var createTime: Instant

    @Column(name = "text", nullable = false)
    lateinit open var text: String

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    lateinit open var user: User

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id", nullable = false)
    lateinit open var ticket: Ticket

    @ManyToMany
    @JoinTable(
            name = "mentioned_ticket",
            joinColumns = arrayOf(JoinColumn(name = "comment_id", referencedColumnName = "id")),
            inverseJoinColumns = arrayOf(JoinColumn(name = "mentioned_ticket_id", referencedColumnName = "id"))
    )
    lateinit open var mentionedTickets: MutableList<Ticket>
        protected set

    @OneToOne(mappedBy = "descriptionComment", optional = true)
    open var describedTicket: Ticket? = null

    @OneToMany(mappedBy = "comment")
    lateinit open var loggedTimes: MutableList<LoggedTime>
        protected set
}

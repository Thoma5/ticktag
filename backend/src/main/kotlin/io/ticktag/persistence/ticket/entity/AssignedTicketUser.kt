package io.ticktag.persistence.ticket.entity

import io.ticktag.persistence.user.entity.User
import java.io.Serializable
import javax.persistence.*

@Embeddable
open class AssignedTicketUserKey protected constructor() : Serializable {
    companion object {
        fun create(ticket: Ticket, tag: AssignmentTag, user: User): AssignedTicketUserKey {
            val k = AssignedTicketUserKey()
            k.ticket = ticket
            k.tag = tag
            k.user = user
            return k
        }
    }

    lateinit open var ticket: Ticket
        protected set

    lateinit open var tag: AssignmentTag
        protected set

    lateinit open var user: User
        protected set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as AssignedTicketUserKey

        if (ticket.id != other.ticket.id) return false
        if (tag.id != other.tag.id) return false
        if (user.id != other.user.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ticket.id.hashCode()
        result = 31 * result + tag.id.hashCode()
        result = 31 * result + user.id.hashCode()
        return result
    }
}

@Entity
@Table(name = "assigned_ticket_user")
@IdClass(AssignedTicketUserKey::class)
open class AssignedTicketUser protected constructor() {
    companion object {
        fun create(ticket: Ticket, tag: AssignmentTag, user: User): AssignedTicketUser {
            val o = AssignedTicketUser()
            o.ticket = ticket
            o.tag = tag
            o.user = user
            return o
        }
    }

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id", nullable = false)
    lateinit open var ticket: Ticket
        protected set

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_tag_id", referencedColumnName = "id", nullable = false)
    lateinit open var tag: AssignmentTag
        protected set

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    lateinit open var user: User
        protected set
}


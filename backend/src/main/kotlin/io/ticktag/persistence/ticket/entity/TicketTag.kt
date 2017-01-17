package io.ticktag.persistence.ticket.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ticket_tag")
open class TicketTag protected constructor() {
    companion object {
        fun create(name: String, normalizedName: String, color: String, order: Int, ticketTagGroup: TicketTagGroup): TicketTag {
            val o = TicketTag()
            o.id = UUID.randomUUID()
            o.name = name
            o.normalizedName = normalizedName
            o.color = color
            o.order = order
            o.ticketTagGroup = ticketTagGroup
            o.tickets = mutableListOf()
            o.tagAddedEvents = mutableListOf()
            o.tagRemovedEvents = mutableListOf()
            o.disabled = false
            return o
        }

        const val COLOR_REGEX = "^(?:[0-9a-fA-F]{3}){1,2}$"
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @Column(name = "name", nullable = false)
    lateinit open var name: String

    @Column(name = "normalized_name", nullable = false)
    lateinit open var normalizedName: String

    @Column(name = "color", nullable = false)
    lateinit open var color: String

    @Column(name = "order", nullable = false)
    open var order: Int = -1

    @Column(name = "disabled", nullable = false)
    open var disabled: Boolean = false

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_tag_group_id", referencedColumnName = "id", nullable = false)
    lateinit open var ticketTagGroup: TicketTagGroup

    @ManyToMany(mappedBy = "tags")
    lateinit open var tickets: MutableList<Ticket>
        protected set

    @OneToMany(mappedBy = "tag")
    lateinit open var tagAddedEvents: MutableList<TicketEventTagAdded>
        protected set

    @OneToMany(mappedBy = "tag")
    lateinit open var tagRemovedEvents: MutableList<TicketEventTagRemoved>
        protected set
}


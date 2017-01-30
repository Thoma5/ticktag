package io.ticktag.persistence.kanban.entity

import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketTag
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "kanban_cell")
open class KanbanCell protected constructor() {
    companion object {
        fun create(ticket: Ticket, ticketTag: TicketTag, order: Int): KanbanCell {
            val o = KanbanCell()
            o.id = UUID.randomUUID()
            o.ticket = ticket
            o.tag = ticketTag
            o.order = order
            return o
        }
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    lateinit open var ticket: Ticket

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_tag_id", referencedColumnName = "id")
    lateinit open var tag: TicketTag

    @Column(name = "order", nullable = false)
    open var order: Int = -1

}
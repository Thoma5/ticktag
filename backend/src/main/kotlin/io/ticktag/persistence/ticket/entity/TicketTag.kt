package io.ticktag.persistence.ticket.entity

import io.ticktag.persistence.project.entity.Project
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ticket_tag")
open class TicketTag protected constructor() {
    companion object {
        fun create(name: String, color: String, groupID: UUID, order: Int, project: Project): TicketTag {
            val o = TicketTag()
            o.id = UUID.randomUUID()
            o.name = name
            o.color = color
            o.groupId = groupID
            o.order = order
            o.project = project
            o.tickets = mutableListOf()
            o.tagAddedEvents = mutableListOf()
            o.tagRemovedEvents = mutableListOf()
            return o
        }
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @Column(name = "name", nullable = false)
    lateinit open var name: String

    @Column(name = "color", nullable = false)
    lateinit open var color: String

    @Column(name = "group_id", nullable = false)
    lateinit open var groupId: UUID

    @Column(name = "order", nullable = false)
    open var order: Int = -1

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    lateinit open var project: Project

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


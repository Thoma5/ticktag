package io.ticktag.persistence.ticket.entity

import io.ticktag.persistence.project.entity.Project
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ticket_tag")
open class TicketTag protected constructor() {
    companion object {
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    lateinit open var project: Project

    @ManyToMany(mappedBy = "tags")
    lateinit open var tickets: MutableList<Ticket>
        protected set
}


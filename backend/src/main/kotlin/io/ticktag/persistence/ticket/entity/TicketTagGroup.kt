package io.ticktag.persistence.ticket.entity

import io.ticktag.persistence.project.entity.Project
import org.hibernate.annotations.Formula
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ticket_tag_group")
open class TicketTagGroup protected constructor() {
    companion object {
        fun create(name: String, exclusive: Boolean, project: Project): TicketTagGroup {
            val o = TicketTagGroup()
            o.id = UUID.randomUUID()
            o.name = name
            o.exclusive = exclusive
            o.project = project
            o.defaultTicketTag = null
            o.ticketTags = mutableListOf()
            return o
        }
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @Column(name = "name", nullable = false)
    lateinit open var name: String

    @Column(name = "exclusive", nullable = false)
    open var exclusive: Boolean = false

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    lateinit open var project: Project

    @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.REMOVE))
    @JoinColumn(name = "default_ticket_tag_id", referencedColumnName = "id", nullable = true)
    open var defaultTicketTag: TicketTag? = null

    @Formula("default_ticket_tag_id IS NOT NULL")
    open var required: Boolean = false
        protected set

    @OneToMany(mappedBy = "ticketTagGroup", cascade = arrayOf(CascadeType.REMOVE))
    lateinit open var ticketTags: MutableList<TicketTag>
        protected set
}
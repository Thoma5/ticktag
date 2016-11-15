package io.ticktag.persistence.ticket.entity

import io.ticktag.persistence.project.entity.Project
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "assignment_tag")
open class AssignmentTag protected constructor() {
    companion object {
        fun create(name: String, color: String, project: Project): AssignmentTag {
            val o = AssignmentTag()
            o.id = UUID.randomUUID()
            o.name = name
            o.color = color
            o.project = project
            o.assignedTicketUsers = mutableListOf()
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    lateinit open var project: Project

    @OneToMany(mappedBy = "tag")
    lateinit open var assignedTicketUsers: MutableList<AssignedTicketUser>
        protected set
}


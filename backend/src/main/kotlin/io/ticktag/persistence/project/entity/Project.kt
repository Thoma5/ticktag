package io.ticktag.persistence.project.entity

import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.ticket.entity.AssignmentTag
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketTagGroup
import io.ticktag.persistence.ticket.entity.TimeCategory
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "project")
open class Project protected constructor() {
    companion object {
        fun create(name: String, description: String, creationDate: Date, iconMimeInfo: String?, icon: ByteArray?): Project {
            val p = Project()
            p.id = UUID.randomUUID()
            p.name = name
            p.description = description
            p.disabled = false
            p.creationDate = creationDate
            p.iconMimeInfo = iconMimeInfo
            p.icon = icon
            p.members = mutableListOf()
            p.tickets = mutableListOf()
            p.ticketTagGroups = mutableListOf()
            p.assignmentTags = mutableListOf()
            p.timeCategories = mutableListOf()
            return p
        }
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @Column(name = "name", nullable = false)
    lateinit open var name: String

    @Column(name = "description", nullable = false)
    lateinit open var description: String

    @Column(name = "creation_date", nullable = false)
    lateinit open var creationDate: Date

    @Column(name = "disabled", nullable = false)
    open var disabled: Boolean = false

    @Column(name = "icon_mime_info", nullable = true)
    open var iconMimeInfo: String? = null

    @Column(name = "icon", nullable = true)
    open var icon: ByteArray? = null

    @OneToMany(mappedBy = "project")
    lateinit open var members: MutableList<Member>
        protected set

    @OneToMany(mappedBy = "project")
    lateinit open var tickets: MutableList<Ticket>
        protected set

    @OneToMany(mappedBy = "project")
    lateinit open var ticketTagGroups: MutableList<TicketTagGroup>
        protected set

    @OneToMany(mappedBy = "project")
    lateinit open var assignmentTags: MutableList<AssignmentTag>
        protected set

    @OneToMany(mappedBy = "project")
    lateinit open var timeCategories: MutableList<TimeCategory>
        protected set
}
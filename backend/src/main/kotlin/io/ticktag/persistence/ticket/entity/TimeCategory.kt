package io.ticktag.persistence.ticket.entity

import io.ticktag.persistence.project.entity.Project
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "time_category")
open class TimeCategory protected constructor() {
    companion object {
        fun create(name: String, normalizedName: String, project: Project): TimeCategory {
            val o = TimeCategory()
            o.id = UUID.randomUUID()
            o.name = name
            o.normalizedName = normalizedName
            o.project = project
            o.loggedTimes = mutableListOf()
            o.loggedTimeAddedEvents = mutableListOf()
            o.loggedTimeRemovedEvents = mutableListOf()
            return o
        }
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @Column(name = "name", nullable = false)
    lateinit open var name: String

    @Column(name = "normalized_name", nullable = false)
    lateinit open var normalizedName: String

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", referencedColumnName = "id", nullable = false)
    lateinit open var project: Project

    @OneToMany(mappedBy = "category")
    lateinit open var loggedTimes: MutableList<LoggedTime>
        protected set

    @OneToMany(mappedBy = "category")
    lateinit open var loggedTimeAddedEvents: MutableList<TicketEventLoggedTimeAdded>
        protected set

    @OneToMany(mappedBy = "category")
    lateinit open var loggedTimeRemovedEvents: MutableList<TicketEventLoggedTimeRemoved>
        protected set
}


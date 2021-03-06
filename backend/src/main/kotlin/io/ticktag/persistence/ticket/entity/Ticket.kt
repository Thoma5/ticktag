package io.ticktag.persistence.ticket.entity

import io.ticktag.persistence.kanban.entity.KanbanCell
import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.user.entity.User
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ticket")
open class Ticket protected constructor() {
    companion object {
        fun create(number: Int, createTime: Instant, title: String, open: Boolean, storyPoints: Int?,
                   initialEstimatedTime: Duration?, currentEstimatedTime: Duration?, dueDate: Instant?,
                   parentTicket: Ticket?, project: Project, createdBy: User): Ticket {
            val o = Ticket()
            o.id = UUID.randomUUID()
            o.number = number
            o.createTime = createTime
            o.title = title
            o.open = open
            o.storyPoints = storyPoints
            o.initialEstimatedTime = initialEstimatedTime ?: currentEstimatedTime
            o.currentEstimatedTime = currentEstimatedTime ?: initialEstimatedTime
            o.dueDate = dueDate
            o.parentTicket = parentTicket
            o.subTickets = mutableListOf()
            o.project = project
            o.createdBy = createdBy
            o.tags = mutableListOf()
            o.mentioningComments = mutableSetOf()
            o.comments = mutableSetOf()
            o.assignedTicketUsers = mutableListOf()
            o.events = mutableListOf()
            o.parentChangedEventsDst = mutableListOf()
            o.parentChangedEventsSrc = mutableListOf()
            o.mentionAddedEvents = mutableListOf()
            o.mentionRemovedEvents = mutableListOf()
            o.kanbanCells = mutableSetOf()
            return o
        }
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @Column(name = "number", nullable = false)
    open var number: Int = -1

    @Column(name = "create_time", nullable = false)
    lateinit open var createTime: Instant

    @Column(name = "title", nullable = false)
    lateinit open var title: String

    @Column(name = "open", nullable = false)
    open var open: Boolean = false


    @Column(name = "story_points", nullable = true)
    open var storyPoints: Int? = null

    @Column(name = "initial_estimated_time", nullable = true)
    open var initialEstimatedTime: Duration? = null

    @Column(name = "current_estimated_time", nullable = true)
    open var currentEstimatedTime: Duration? = null

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "ticket_id", nullable = false)
    open var progress: Progress? = null

    @Column(name = "due_date", nullable = true)
    open var dueDate: Instant? = null

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_ticket_id", referencedColumnName = "id", nullable = true)
    open var parentTicket: Ticket? = null

    @OneToMany(mappedBy = "parentTicket", cascade = arrayOf(CascadeType.REMOVE))
    lateinit open var subTickets: MutableList<Ticket>
        protected set

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    lateinit open var project: Project

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
    lateinit open var createdBy: User

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.REMOVE))
    @JoinColumn(name = "description_comment_id", referencedColumnName = "id", nullable = true)
    lateinit open var descriptionComment: Comment

    @ManyToMany
    @JoinTable(
            name = "assigned_ticket_tag",
            joinColumns = arrayOf(JoinColumn(name = "ticket_id", referencedColumnName = "id")),
            inverseJoinColumns = arrayOf(JoinColumn(name = "ticket_tag_id", referencedColumnName = "id"))
    )
    lateinit open var tags: MutableList<TicketTag>

    @ManyToMany(mappedBy = "mentionedTickets")
    lateinit open var mentioningComments: MutableSet<Comment>
        protected set

    @OneToMany(mappedBy = "ticket", cascade = arrayOf(CascadeType.REMOVE))
    lateinit open var comments: MutableSet<Comment>
        protected set

    @OneToMany(mappedBy = "ticket", cascade = arrayOf(CascadeType.REMOVE))
    lateinit open var assignedTicketUsers: MutableList<AssignedTicketUser>

    @OneToMany(mappedBy = "ticket")
    lateinit open var events: MutableList<TicketEvent>
        protected set

    @OneToMany(mappedBy = "srcParent", cascade = arrayOf(CascadeType.REMOVE))
    lateinit open var parentChangedEventsSrc: MutableList<TicketEventParentChanged>
        protected set

    @OneToMany(mappedBy = "dstParent", cascade = arrayOf(CascadeType.REMOVE))
    lateinit open var parentChangedEventsDst: MutableList<TicketEventParentChanged>
        protected set

    @OneToMany(mappedBy = "mentionedTicket")
    lateinit open var mentionAddedEvents: MutableList<TicketEventMentionAdded>
        protected set

    @OneToMany(mappedBy = "mentionedTicket")
    lateinit open var mentionRemovedEvents: MutableList<TicketEventMentionRemoved>
        protected set

    @OneToMany(mappedBy = "ticket", cascade = arrayOf(CascadeType.REMOVE))
    lateinit open var kanbanCells: MutableSet<KanbanCell>

}
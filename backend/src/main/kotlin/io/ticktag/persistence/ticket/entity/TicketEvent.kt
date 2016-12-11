package io.ticktag.persistence.ticket.entity

import io.ticktag.persistence.user.entity.User
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ticket_event")
@Inheritance(strategy = InheritanceType.JOINED)
open class TicketEvent protected constructor() {
    @Id
    @Column(name = "id", nullable = false)
    lateinit open var id: UUID
        protected set

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id", nullable = false)
    lateinit open var ticket: Ticket

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    lateinit open var user: User

    @Column(name = "time", nullable = false)
    lateinit open var time: Instant

    fun setSuperValues(ticket: Ticket, user: User) {
        this.id = UUID.randomUUID()
        this.ticket = ticket
        this.user = user
        this.time = Instant.now()
    }
}

@Entity
@Table(name = "ticket_event_parent_changed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventParentChanged protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, srcParent: Ticket?, dstParent: Ticket?): TicketEventParentChanged {
            val o = TicketEventParentChanged()
            o.setSuperValues(ticket, user)
            o.srcParent = srcParent
            o.dstParent = dstParent
            return o
        }
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "src_parent_id", referencedColumnName = "id", nullable = true)
    open var srcParent: Ticket? = null

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "dst_parent_id", referencedColumnName = "id", nullable = true)
    open var dstParent: Ticket? = null
}

@Entity
@Table(name = "ticket_event_title_changed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventTitleChanged protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, srcTitle: String, dstTitle: String): TicketEventTitleChanged {
            val o = TicketEventTitleChanged()
            o.setSuperValues(ticket, user)
            o.srcTitle = srcTitle
            o.dstTitle = dstTitle
            return o
        }
    }

    @Column(name = "src_title", nullable = false)
    lateinit open var srcTitle: String

    @Column(name = "dst_title", nullable = false)
    lateinit open var dstTitle: String
}

@Entity
@Table(name = "ticket_event_state_changed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventStateChanged protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, srcState: Boolean, dstState: Boolean): TicketEventStateChanged {
            val o = TicketEventStateChanged()
            o.setSuperValues(ticket, user)
            o.srcState = srcState
            o.dstState = dstState
            return o
        }
    }

    @Column(name = "src_state", nullable = false)
    open var srcState: Boolean = false

    @Column(name = "dst_state", nullable = false)
    open var dstState: Boolean = false
}

@Entity
@Table(name = "ticket_event_story_points_changed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventStoryPointsChanged protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, srcStoryPoints: Int?, dstStoryPoints: Int?): TicketEventStoryPointsChanged {
            val o = TicketEventStoryPointsChanged()
            o.setSuperValues(ticket, user)
            o.srcStoryPoints = srcStoryPoints
            o.dstStoryPoints = dstStoryPoints
            return o
        }
    }

    @Column(name = "src_story_points", nullable = true)
    open var srcStoryPoints: Int? = null

    @Column(name = "dst_story_points", nullable = true)
    open var dstStoryPoints: Int? = null
}

@Entity
@Table(name = "ticket_event_initial_estimated_time_changed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventInitialEstimatedTimeChanged protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, srcInitialEstimatedTime: Duration?, dstInitialEstimatedTime: Duration?): TicketEventInitialEstimatedTimeChanged {
            val o = TicketEventInitialEstimatedTimeChanged()
            o.setSuperValues(ticket, user)
            o.srcInitialEstimatedTime = srcInitialEstimatedTime
            o.dstInitialEstimatedTime = dstInitialEstimatedTime
            return o
        }
    }

    @Column(name = "src_initial_estimated_time", nullable = true)
    open var srcInitialEstimatedTime: Duration? = null

    @Column(name = "dst_initial_estimated_time", nullable = true)
    open var dstInitialEstimatedTime: Duration? = null
}

@Entity
@Table(name = "ticket_event_current_estimated_time_changed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventCurrentEstimatedTimeChanged protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, srcCurrentEstimatedTime: Duration?, dstCurrentEstimatedTime: Duration?): TicketEventCurrentEstimatedTimeChanged {
            val o = TicketEventCurrentEstimatedTimeChanged()
            o.setSuperValues(ticket, user)
            o.srcCurrentEstimatedTime = srcCurrentEstimatedTime
            o.dstCurrentEstimatedTime = dstCurrentEstimatedTime
            return o
        }
    }

    @Column(name = "src_current_estimated_time", nullable = true)
    open var srcCurrentEstimatedTime: Duration? = null

    @Column(name = "dst_current_estimated_time", nullable = true)
    open var dstCurrentEstimatedTime: Duration? = null
}


@Entity
@Table(name = "ticket_event_due_date_changed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventDueDateChanged protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, srcDueDate: Instant?, dstDueDate: Instant?): TicketEventDueDateChanged {
            val o = TicketEventDueDateChanged()
            o.setSuperValues(ticket, user)
            o.srcDueDate = srcDueDate
            o.dstDueDate = dstDueDate
            return o
        }
    }

    @Column(name = "src_due_date", nullable = true)
    open var srcDueDate: Instant? = null

    @Column(name = "dst_due_date", nullable = true)
    open var dstDueDate: Instant? = null
}

@Entity
@Table(name = "ticket_event_comment_text_changed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventCommentTextChanged protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, comment: Comment, srcText: String, dstText: String): TicketEventCommentTextChanged {
            val o = TicketEventCommentTextChanged()
            o.setSuperValues(ticket, user)
            o.comment = comment
            o.srcText = srcText
            o.dstText = dstText
            return o
        }
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id", nullable = false)
    lateinit open var comment: Comment

    @Column(name = "src_text", nullable = false)
    lateinit open var srcText: String

    @Column(name = "dst_text", nullable = false)
    lateinit open var dstText: String
}

@Entity
@Table(name = "ticket_event_tag_added")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventTagAdded protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, tag: TicketTag): TicketEventTagAdded {
            val o = TicketEventTagAdded()
            o.setSuperValues(ticket, user)
            o.tag = tag
            return o
        }
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_tag_id", referencedColumnName = "id", nullable = false)
    lateinit open var tag: TicketTag
}

@Entity
@Table(name = "ticket_event_tag_removed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventTagRemoved protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, tag: TicketTag): TicketEventTagRemoved {
            val o = TicketEventTagRemoved()
            o.setSuperValues(ticket, user)
            o.tag = tag
            return o
        }
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_tag_id", referencedColumnName = "id", nullable = false)
    lateinit open var tag: TicketTag
}

@Entity
@Table(name = "ticket_event_user_added")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventUserAdded protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, addedUser: User, tag: AssignmentTag): TicketEventUserAdded {
            val o = TicketEventUserAdded()
            o.setSuperValues(ticket, user)
            o.addedUser = addedUser
            o.tag = tag
            return o
        }
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    lateinit open var addedUser: User

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_tag_id", referencedColumnName = "id", nullable = false)
    lateinit open var tag: AssignmentTag
}

@Entity
@Table(name = "ticket_event_user_removed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventUserRemoved protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, removedUser: User, tag: AssignmentTag): TicketEventUserRemoved {
            val o = TicketEventUserRemoved()
            o.setSuperValues(ticket, user)
            o.removedUser = removedUser
            o.tag = tag
            return o
        }
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    lateinit open var removedUser: User

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_tag_id", referencedColumnName = "id", nullable = false)
    lateinit open var tag: AssignmentTag
}

@Entity
@Table(name = "ticket_event_mention_added")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventMentionAdded protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, comment: Comment, mentionedTicket: Ticket): TicketEventMentionAdded {
            val o = TicketEventMentionAdded()
            o.setSuperValues(ticket, user)
            o.comment = comment
            o.mentionedTicket = mentionedTicket
            return o
        }
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id", nullable = false)
    lateinit open var comment: Comment

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id", nullable = false)
    lateinit open var mentionedTicket: Ticket
}

@Entity
@Table(name = "ticket_event_mention_removed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventMentionRemoved protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, comment: Comment, mentionedTicket: Ticket): TicketEventMentionRemoved {
            val o = TicketEventMentionRemoved()
            o.setSuperValues(ticket, user)
            o.comment = comment
            o.mentionedTicket = mentionedTicket
            return o
        }
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id", nullable = false)
    lateinit open var comment: Comment

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id", nullable = false)
    lateinit open var mentionedTicket: Ticket
}

@Entity
@Table(name = "ticket_event_logged_time_added")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventLoggedTimeAdded protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, comment: Comment, category: TimeCategory, loggedTime: Duration): TicketEventLoggedTimeAdded {
            val o = TicketEventLoggedTimeAdded()
            o.setSuperValues(ticket, user)
            o.comment = comment
            o.category = category
            o.loggedTime = loggedTime
            return o
        }
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id", nullable = false)
    lateinit open var comment: Comment

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "time_category_id", referencedColumnName = "id", nullable = false)
    lateinit open var category: TimeCategory

    @Column(name = "time", nullable = false)
    lateinit open var loggedTime: Duration
}

@Entity
@Table(name = "ticket_event_logged_time_removed")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
open class TicketEventLoggedTimeRemoved protected constructor() : TicketEvent() {
    companion object {
        fun create(ticket: Ticket, user: User, comment: Comment, category: TimeCategory, loggedTime: Duration): TicketEventLoggedTimeRemoved {
            val o = TicketEventLoggedTimeRemoved()
            o.setSuperValues(ticket, user)
            o.comment = comment
            o.category = category
            o.loggedTime = loggedTime
            return o
        }
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id", nullable = false)
    lateinit open var comment: Comment

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "time_category_id", referencedColumnName = "id", nullable = false)
    lateinit open var category: TimeCategory

    @Column(name = "time", nullable = false)
    lateinit open var loggedTime: Duration
}

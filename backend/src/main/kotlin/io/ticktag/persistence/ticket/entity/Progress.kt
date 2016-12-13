package io.ticktag.persistence.ticket.entity

import org.hibernate.annotations.Immutable
import java.time.Duration
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Immutable
@Entity
@Table(name = "view_progress")
open class Progress protected constructor() {
    @Id
    @Column(name = "ticket_id")
    lateinit open var id: UUID
        protected set

    @Column(name = "logged_time")
    open var loggedTime: Duration? = null
        protected set

    @Column(name = "initial_estimated_time")
    open var initalEstimatedTime: Duration? = null
        protected set

    @Column(name = "current_estimated_time")
    open var currentEstimatedTime: Duration? = null
        protected set

    @Column(name = "initial_progress")
    open var initialProgress: Float? = null
        protected set

    @Column(name = "progress")
    open var progress: Float? = null
        protected set

}

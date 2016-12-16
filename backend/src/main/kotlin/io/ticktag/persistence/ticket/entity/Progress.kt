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
    open lateinit var loggedTime: Duration
        protected set

    @Column(name = "total_logged_time")
    open lateinit var totalLoggedTime: Duration
        protected set

    @Column(name = "total_initial_estimated_time")
    open lateinit var totalInitialEstimatedTime: Duration
        protected set

    @Column(name = "total_current_estimated_time")
    open lateinit var currentEstimatedTime: Duration
        protected set

    @Column(name = "total_initial_progress")
    open var totalInitialProgress: Float? = null
        protected set

    @Column(name = "total_progress")
    open var totalProgress: Float? = null
        protected set

}

package io.ticktag.service.ticketevent.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.ticket.TicketEventRepository
import io.ticktag.persistence.ticket.entity.*
import io.ticktag.service.AuthExpr
import io.ticktag.service.ticketevent.dto.*
import io.ticktag.service.ticketevent.services.TicketEventService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject

@TicktagService
open class TicketEventServiceImpl @Inject constructor(
        val ticketEvents: TicketEventRepository
) : TicketEventService {

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun listTicketEvents(@P("authTicketId") ticketId: UUID): List<TicketEventResult> {
        return ticketEvents.findByTicketIdOrderByTimeAsc(ticketId).map { e ->
            when (e) {
                is TicketEventTitleChanged -> TicketEventTitleChangedResult(e)
                is TicketEventCommentTextChanged -> TicketEventCommentTextChangedResult(e)
                is TicketEventCurrentEstimatedTimeChanged -> TicketEventCurrentEstimatedTimeChangedResult(e)
                is TicketEventDueDateChanged -> TicketEventDueDateChangedResult(e)
                is TicketEventInitialEstimatedTimeChanged -> TicketEventInitialEstimatedTimeChangedResult(e)
                is TicketEventLoggedTimeAdded -> TicketEventLoggedTimeAddedResult(e)
                is TicketEventLoggedTimeRemoved -> TicketEventLoggedTimeRemovedResult(e)
                is TicketEventMentionAdded -> TicketEventMentionAddedResult(e)
                is TicketEventMentionRemoved -> TicketEventMentionRemovedResult(e)
                is TicketEventParentChanged -> TicketEventParentChangedResult(e)
                is TicketEventStateChanged -> TicketEventStateChangedResult(e)
                is TicketEventStoryPointsChanged -> TicketEventStoryPointsChangedResult(e)
                is TicketEventTagAdded -> TicketEventTagAddedResult(e)
                is TicketEventTagRemoved -> TicketEventTagRemovedResult(e)
                is TicketEventUserAdded -> TicketEventUserAddedResult(e)
                is TicketEventUserRemoved -> TicketEventUserRemovedResult(e)
                else -> throw RuntimeException()
            }
        }
    }
}
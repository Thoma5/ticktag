package io.ticktag.service.kanbanBoard.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.tickettaggroup.TicketTagGroupRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.kanbanBoard.dto.KanbanBoardResult
import io.ticktag.service.kanbanBoard.dto.KanbanColumnResult
import io.ticktag.service.kanbanBoard.services.KanbanService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject

@TicktagService
open class KanbanServiceImpl @Inject constructor(
        private val ticketTagGroups: TicketTagGroupRepository,
        private val ticketTagRepository: TicketTagRepository
) : KanbanService {
    @PreAuthorize(AuthExpr.READ_TICKET_TAG_FOR_GROUP)
    override fun listColumns(@P("authTicketTagGroupId") kanbanBoardId: UUID): List<KanbanColumnResult> {
        return ticketTagRepository.findByTicketTagGroupId(kanbanBoardId).map(::KanbanColumnResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listBoards(@P("authProjectId") projectId: UUID): List<KanbanBoardResult> {
        return ticketTagGroups.findExclusiveTicketTagGroupsByProjectId(projectId).map(::KanbanBoardResult)
    }
}
package io.ticktag.restinterface.kanbanboard.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.kanbanboard.schema.KanbanBoardReslutJson
import io.ticktag.restinterface.kanbanboard.schema.KanbanColumnResultJson
import io.ticktag.restinterface.kanbanboard.schema.UpdateKanbanColumnJson
import io.ticktag.service.Principal
import io.ticktag.service.kanbanBoard.services.KanbanService
import io.ticktag.service.tickettaggroup.service.TicketTagGroupService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject
import  io.ticktag.service.kanbanBoard.dto.UpdateKanbanColumn
import org.springframework.security.core.annotation.AuthenticationPrincipal

@TicktagRestInterface
@RequestMapping("/board")
@Api(tags = arrayOf("board"), description = "kanban board management")
open class KanbanBoardController @Inject constructor(
        private val kanbanService: KanbanService
) {

    @GetMapping(value = "/")
    open fun listKanbanBoards(@RequestParam(name = "projectId", required = true) projectId: UUID): List<KanbanBoardReslutJson> {
        return kanbanService.listBoards(projectId).map(::KanbanBoardReslutJson)
    }

    @GetMapping(value = "/{id}/columns")
    open fun listKanbanColumns(@PathVariable id: UUID): List<KanbanColumnResultJson> {
        return kanbanService.listColumns(id).map(::KanbanColumnResultJson)
    }

    @GetMapping(value = "/{id}")
    open fun getKanbanBoard(@PathVariable id:UUID): KanbanBoardReslutJson{
        return KanbanBoardReslutJson(kanbanService.getBoard(id))
    }

    @PutMapping(value = "/{id}")
    open fun updateKanbanBoards(@PathVariable id: UUID,@RequestBody(required = true) columns: List<UpdateKanbanColumnJson>, @AuthenticationPrincipal principal: Principal): List<KanbanColumnResultJson> {
        return kanbanService.updateKanbanBoard(columns.map(::UpdateKanbanColumn),principal).map(::KanbanColumnResultJson)
    }
}
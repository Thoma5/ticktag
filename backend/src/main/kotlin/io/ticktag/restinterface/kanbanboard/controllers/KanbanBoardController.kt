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
import java.time.Instant

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
    open fun listKanbanColumns(@PathVariable id: UUID,
                               @RequestParam(name = "number", required = false) number: Int?,
                               @RequestParam(name = "title", required = false) title: String?,
                               @RequestParam(name = "tags", required = false) tags: List<String>?,
                               @RequestParam(name = "users",  required = false) user: List<String>?,
                               @RequestParam(name = "progressOne", required = false) progressOne: Float?,
                               @RequestParam(name = "progressTwo",  required = false) progressTwo: Float?,
                               @RequestParam(name = "progressGreater", required = false) progressGreater: Boolean?,
                               @RequestParam(name = "dueDateOne",  required = false) dueDateOne: Instant?,
                               @RequestParam(name = "dueDateTwo",  required = false) dueDateTwo: Instant?,
                               @RequestParam(name = "dueDateGreater", required = false) dueDateGreater: Boolean?,
                               @RequestParam(name = "storyPointsOne",  required = false) storyPointsOne: Int?,
                               @RequestParam(name = "storyPointsTwo",  required = false) storyPointsTwo: Int?,
                               @RequestParam(name = "storyPointsGreater", required = false) storyPointsGreater: Boolean?,
                               @RequestParam(name = "open", required = false) open: Boolean?): List<KanbanColumnResultJson> {
        return kanbanService.listColumns(id, number, title, tags, user, progressOne, progressTwo, progressGreater, dueDateOne, dueDateTwo, dueDateGreater, storyPointsOne, storyPointsTwo, storyPointsGreater, open).map(::KanbanColumnResultJson)
    }

    @GetMapping(value = "/{id}")
    open fun getKanbanBoard(@PathVariable id:UUID): KanbanBoardReslutJson{
        return KanbanBoardReslutJson(kanbanService.getBoard(id))
    }

    @PutMapping(value = "/{id}")
    open fun updateKanbanBoards(@PathVariable id: UUID,@RequestBody(required = true) columns: List<UpdateKanbanColumnJson>, @AuthenticationPrincipal principal: Principal) {
        kanbanService.updateKanbanBoard(columns.map(::UpdateKanbanColumn),principal)
    }
}
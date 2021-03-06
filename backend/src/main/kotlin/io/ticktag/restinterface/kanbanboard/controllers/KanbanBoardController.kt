package io.ticktag.restinterface.kanbanboard.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.kanbanboard.schema.KanbanBoardResultJson
import io.ticktag.restinterface.kanbanboard.schema.KanbanColumnResultJson
import io.ticktag.restinterface.kanbanboard.schema.UpdateKanbanColumnJson
import io.ticktag.service.Principal
import io.ticktag.service.kanbanboard.dto.UpdateKanbanColumn
import io.ticktag.service.kanbanboard.services.KanbanService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/board")
@Api(tags = arrayOf("board"), description = "kanban board management")
open class KanbanBoardController @Inject constructor(
        private val kanbanService: KanbanService
) {

    @GetMapping(value = "/")
    open fun listKanbanBoards(@RequestParam(name = "projectId", required = true) projectId: UUID): List<KanbanBoardResultJson> {
        return kanbanService.listBoards(projectId).map(::KanbanBoardResultJson)
    }

    @GetMapping(value = "/{id}/columns")
    open fun listKanbanColumns(@PathVariable id: UUID,
                               @RequestParam(name = "numbers", required = false) numbers: List<Int>?,
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
                               @RequestParam(name = "open", required = false) open: Boolean?,
                               @RequestParam(name = "parent", required = false) parent: Int?
    ): List<KanbanColumnResultJson> {
        return kanbanService.listColumns(id, numbers, title, tags, user, progressOne, progressTwo, progressGreater, dueDateOne, dueDateTwo, dueDateGreater, storyPointsOne, storyPointsTwo, storyPointsGreater, open, parent).map(::KanbanColumnResultJson)
    }

    @GetMapping(value = "/{id}")
    open fun getKanbanBoard(@PathVariable id:UUID): KanbanBoardResultJson {
        return KanbanBoardResultJson(kanbanService.getBoard(id))
    }

    @PutMapping(value = "/{id}")
    open fun updateKanbanBoards(@PathVariable id: UUID,@RequestBody(required = true) column: UpdateKanbanColumnJson, @AuthenticationPrincipal principal: Principal) {
        kanbanService.updateKanbanBoard(UpdateKanbanColumn(id = column.id, ticketIds = column.ticketIds,ticketIdToUpdate = column.ticketIdToUpdate),principal,column.id)
    }

    @PutMapping(value = "/{id}/collect")
    open fun collectSubTickets(@PathVariable id: UUID,@AuthenticationPrincipal principal: Principal, @RequestBody tagId:UUID) {
        kanbanService.collectSubticket(id,tagId,principal)
    }

}
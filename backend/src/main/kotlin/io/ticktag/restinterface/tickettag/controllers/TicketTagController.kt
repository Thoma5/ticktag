package io.ticktag.restinterface.tickettag.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.tickettag.schema.CreateTicketTagRequestJson
import io.ticktag.restinterface.tickettag.schema.TicketTagResultJson
import io.ticktag.restinterface.tickettag.schema.UpdateTicketTagRequestJson
import io.ticktag.service.tickettag.dto.CreateTicketTag
import io.ticktag.service.tickettag.dto.UpdateTicketTag
import io.ticktag.service.tickettag.services.TicketTagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/tickettag")
@Api(tags = arrayOf("tickettag"), description = "tickettag management")
open class TicketTagController @Inject constructor(
        private val ticketTagService: TicketTagService
) {
    @PostMapping
    open fun createTicketTag(@RequestBody req: CreateTicketTagRequestJson): TicketTagResultJson {
        val ticketTag = ticketTagService.createTicketTag(CreateTicketTag(req.name, req.color, req.order, req.autoClose), req.ticketTagGroupId)
        return TicketTagResultJson(ticketTag)
    }

    @PutMapping(value = "/{id}")
    open fun updateTicketTag(@PathVariable(name = "id") id: UUID,
                             @RequestBody req: UpdateTicketTagRequestJson
    ): TicketTagResultJson {
        val ticketTag = ticketTagService.updateTicketTag(id, UpdateTicketTag(req.name, req.color, req.order, req.ticketTagGroupId, req.autoClose))
        return TicketTagResultJson(ticketTag)
    }

    @GetMapping(value = "/{id}")
    open fun getTicketTag(@PathVariable(name = "id") id: UUID): TicketTagResultJson {
        return TicketTagResultJson(ticketTagService.getTicketTag(id))
    }

    @DeleteMapping(value = "/{id}")
    open fun deleteTicketTag(@PathVariable(name = "id") id: UUID) {
        ticketTagService.deleteTicketTag(id)
    }

    @GetMapping(value = "/")
    open fun listTicketTags(@RequestParam(name = "ticketTagGroupId", required = false) ticketTagGroupId: UUID?,
                            @RequestParam(name = "projectId", required = false) projectId: UUID?): ResponseEntity<List<TicketTagResultJson>> {
        val bothParamSet = projectId != null && ticketTagGroupId != null
        val noParamSet = projectId == null && ticketTagGroupId == null
        if (bothParamSet || noParamSet) {
            return ResponseEntity.badRequest().body(null)
        } else if (ticketTagGroupId != null) {
            val list = ticketTagService.listTicketTagsInGroup(ticketTagGroupId).map(::TicketTagResultJson)
            return ResponseEntity.ok(list)
        } else if (projectId != null) {
            val list = ticketTagService.listTicketTagsInProject(projectId).map(::TicketTagResultJson)
            return ResponseEntity.ok(list)
        } else { // else is unnecessary
            return ResponseEntity.badRequest().body(null)
        }
    }
}
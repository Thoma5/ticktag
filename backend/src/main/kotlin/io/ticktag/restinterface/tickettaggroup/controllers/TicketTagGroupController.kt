package io.ticktag.restinterface.tickettaggroup.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.tickettag.schema.TicketTagResultJson
import io.ticktag.restinterface.tickettaggroup.schema.CreateTicketTagGroupRequestJson
import io.ticktag.restinterface.tickettaggroup.schema.TicketTagGroupResultJson
import io.ticktag.restinterface.tickettaggroup.schema.UpdateTicketTagGroupRequestJson
import io.ticktag.service.tickettag.services.TicketTagService
import io.ticktag.service.tickettaggroup.dto.CreateTicketTagGroup
import io.ticktag.service.tickettaggroup.dto.UpdateTicketTagGroup
import io.ticktag.service.tickettaggroup.service.TicketTagGroupService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/tickettaggroup")
@Api(tags = arrayOf("tickettaggroup"), description = "tickettaggroup management")
open class TicketTagGroupController @Inject constructor(
        private val ticketTagGroupService: TicketTagGroupService,
        private val ticketTagService: TicketTagService
) {
    @PostMapping
    open fun createTicketTagGroup(@RequestBody req: CreateTicketTagGroupRequestJson): TicketTagGroupResultJson {
        val ticketTagGroup = ticketTagGroupService.createTicketTagGroup(CreateTicketTagGroup(req.name, req.exclusive), req.projectId)
        return TicketTagGroupResultJson(ticketTagGroup)
    }

    @PutMapping(value = "/{id}")
    open fun updateTicketTagGroup(@PathVariable(name = "id") id: UUID,
                                  @RequestBody req: UpdateTicketTagGroupRequestJson
    ): TicketTagGroupResultJson {
        val ticketTagGroup = ticketTagGroupService.updateTicketTagGroup(id, UpdateTicketTagGroup(req.name, req.exclusive, req.defaultTicketTagId))
        return TicketTagGroupResultJson(ticketTagGroup)
    }

    @GetMapping(value = "/{id}")
    open fun getTicketTagGroup(@PathVariable(name = "id") id: UUID): TicketTagGroupResultJson {
        return TicketTagGroupResultJson(ticketTagGroupService.getTicketTagGroup(id))
    }

    @DeleteMapping(value = "/{id}")
    open fun deleteTicketTagGroup(@PathVariable(name = "id") id: UUID) {
        ticketTagGroupService.deleteTicketTagGroup(id)
    }

    @GetMapping(value = "/{id}/tickettag")
    open fun listTicketTags(@PathVariable(name = "id") id: UUID): List<TicketTagResultJson> {
        return ticketTagService.listTicketTagsInGroup(id).map(::TicketTagResultJson)
    }
}
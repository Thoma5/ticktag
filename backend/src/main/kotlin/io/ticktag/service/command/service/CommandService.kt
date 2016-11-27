package io.ticktag.service.command.service

import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.service.Principal
import io.ticktag.service.command.dto.Command

interface CommandService {
    /**
     * The caller is expected to have performed authorization
     */
    fun applyCommands(comment: Comment, commands: List<Command>, principal: Principal)
}
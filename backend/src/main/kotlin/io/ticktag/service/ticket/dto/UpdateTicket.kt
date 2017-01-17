package io.ticktag.service.ticket.dto

import io.ticktag.service.UpdateValue
import io.ticktag.service.command.dto.Command
import io.ticktag.util.PositiveDuration
import org.hibernate.validator.valuehandling.UnwrapValidatedValue
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Size

data class UpdateTicket(
        @field:Size(min = 1, max = 100)
        @field:UnwrapValidatedValue
        val title: UpdateValue<String>?,

        @field:UnwrapValidatedValue
        val open: UpdateValue<Boolean>?,

        @field:DecimalMin("0")
        @field:UnwrapValidatedValue
        val storyPoints: UpdateValue<Int?>?,

        @field:PositiveDuration
        @field:UnwrapValidatedValue
        val initialEstimatedTime: UpdateValue<Duration?>?,

        @field:PositiveDuration
        @field:UnwrapValidatedValue
        val currentEstimatedTime: UpdateValue<Duration?>?,

        @field:UnwrapValidatedValue
        val dueDate: UpdateValue<Instant?>?,

        @field:Size(min = 0, max = 50000)
        @field:UnwrapValidatedValue
        val description: UpdateValue<String>?,

        @field:UnwrapValidatedValue
        val parentTicket: UpdateValue<UUID?>?,

        @field:Valid
        val commands: List<Command>?
)

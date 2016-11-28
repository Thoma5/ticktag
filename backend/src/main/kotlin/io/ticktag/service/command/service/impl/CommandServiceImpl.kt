package io.ticktag.service.command.service.impl

import io.ticktag.TicktagService
import io.ticktag.library.unicode.NameNormalizationLibrary
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.timecategory.TimeCategoryRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.*
import io.ticktag.service.command.dto.Command
import io.ticktag.service.command.service.CommandService
import io.ticktag.service.loggedtime.dto.CreateLoggedTime
import io.ticktag.service.loggedtime.service.LoggedTimeService
import io.ticktag.service.ticket.dto.UpdateTicket
import io.ticktag.service.ticket.service.TicketService
import io.ticktag.service.ticketassignment.services.TicketAssignmentService
import io.ticktag.service.tickettagrelation.services.TicketTagRelationService
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import javax.inject.Inject

@TicktagService
open class CommandServiceImpl(
        private val tickets: TicketRepository,
        private val users: UserRepository,
        private val loggedTimeService: LoggedTimeService,
        private val ticketTags: TicketTagRepository,
        private val timeCategories: TimeCategoryRepository,
        private val assignmentTags: AssignmentTagRepository,
        private val nn: NameNormalizationLibrary,
        private val ticketTagRelationService: TicketTagRelationService,
        private val ticketAssignmentService: TicketAssignmentService
) : CommandService {
    @Inject()
    private lateinit var ticketService: TicketService

    @PreAuthorize(AuthExpr.USER)
    override fun applyCommands(comment: Comment, commands: List<Command>, principal: Principal) {
        val errors = mutableListOf<ValidationError>()

        if (!(principal.hasRole(AuthExpr.ROLE_GLOBAL_ADMIN) || principal.hasProjectRoleForComment(comment.id, AuthExpr.ROLE_PROJECT_USER))) {
            throw AccessDeniedException("Access is denied")
        }

        val ticket = comment.ticket

        for ((index, command) in commands.withIndex()) {
            when (command) {
                is Command.Assign -> {
                    val assignUser = users.findByUsername(command.user)
                    val assignTag = assignmentTags.findByNormalizedNameAndProjectId(nn.normalize(command.tag), ticket.project.id)
                    if (assignUser != null && assignTag != null) {
                        tryCommand(errors, index) {
                            ticketAssignmentService.createOrGetIfExistsTicketAssignment(ticket.id, assignTag.id, assignUser.id, principal)
                        }
                    } else {
                        failedCommand(errors, index)
                    }
                }
                is Command.Unassign -> {
                    val removeUser = users.findByUsername(command.user)
                    if (removeUser != null) {
                        if (command.tag == null) {
                            tryCommand(errors, index) {
                                ticketAssignmentService.deleteTicketAssignments(ticket.id, removeUser.id, principal)
                            }
                        } else {
                            val removeTag = assignmentTags.findByNormalizedNameAndProjectId(nn.normalize(command.tag), ticket.project.id)
                            if (removeTag != null) {
                                tryCommand(errors, index) {
                                    ticketAssignmentService.deleteTicketAssignment(ticket.id, removeTag.id, removeUser.id, principal)
                                }
                            } else {
                                failedCommand(errors, index)
                            }
                        }
                    } else {
                        failedCommand(errors, index)
                    }
                }
                is Command.Close -> {
                    tryCommand(errors, index) {
                        ticketService.updateTicket(UpdateTicket(null, false, null, null, null, null, null, null), ticket.id, principal)
                    }
                }
                is Command.Reopen -> {
                    tryCommand(errors, index) {
                        ticketService.updateTicket(UpdateTicket(null, true, null, null, null, null, null, null), ticket.id, principal)
                    }
                }
                is Command.Tag -> {
                    val tag = ticketTags.findByNormalizedNameAndProjectId(nn.normalize(command.tag), ticket.project.id)
                    if (tag != null) {
                        tryCommand(errors, index) {
                            ticketTagRelationService.createOrGetIfExistsTicketTagRelation(ticket.id, tag.id)
                        }
                    } else {
                        failedCommand(errors, index)
                    }
                }
                is Command.Untag -> {
                    val tag = ticketTags.findByNormalizedNameAndProjectId(nn.normalize(command.tag), ticket.project.id)
                    if (tag != null) {
                        tryCommand(errors, index) {
                            ticketTagRelationService.deleteTicketTagRelation(ticket.id, tag.id)
                        }
                    } else {
                        failedCommand(errors, index)
                    }
                }
                is Command.Est -> {
                    tryCommand(errors, index) {
                        ticketService.updateTicket(UpdateTicket(null, null, null, null, command.duration, null, null, null), ticket.id, principal)
                    }
                }
                is Command.Time -> {
                    val cat = timeCategories.findByNormalizedNameAndProjectId(nn.normalize(command.category), ticket.project.id)
                    if (cat != null) {
                        tryCommand(errors, index) {
                            // TODO why needs createLoggedTime the comment id twice???
                            loggedTimeService.createLoggedTime(CreateLoggedTime(command.duration, comment.id, cat.id), comment.id)
                        }
                    } else {
                        failedCommand(errors, index)
                    }
                }
                is Command.RefTicket -> {
                    val refNum = command.ticketNumber
                    val referencedTicket = tickets.findByNumber(command.ticketNumber)
                    if (referencedTicket != null) {
                        comment.mentionedTickets.add(referencedTicket)
                    } else {
                        errors.add(ValidationError("createComment.mentionedTicketNumbers", ValidationErrorDetail.Other("$refNum")))
                    }
                }
            }
        }

        if (errors.isNotEmpty()) {
            throw TicktagValidationException(errors)
        }
    }

    private fun failedCommand(errors: MutableList<ValidationError>, index: Int) {
        errors.add(ValidationError("createComment.commands", ValidationErrorDetail.Other("$index")))
    }

    private fun tryCommand(errors: MutableList<ValidationError>, index: Int, fn: () -> Unit) {
        try {
            fn()
        } catch (ex: TicktagValidationException) {
            failedCommand(errors, index)
        } catch (ex: NotFoundException) {
            failedCommand(errors, index)
        }
    }
}
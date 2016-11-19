package io.ticktag.restinterface.ticketassignment.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.service.timecategory.TicketAssignmentService
import org.springframework.web.bind.annotation.RequestMapping
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/tickass")
@Api(tags = arrayOf("tickass"), description = "project tickass")
open class TicketAssignmentController @Inject constructor(
        private val ticketAssignmentService: TicketAssignmentService
) {

}
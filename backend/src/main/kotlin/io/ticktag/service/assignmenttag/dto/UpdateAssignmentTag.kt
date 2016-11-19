package  io.ticktag.service.assignmenttag.dto

import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class UpdateAssignmentTag(
        @field:Size(min = 1, max = 30) val name: String?,
        @field:Pattern(regexp = "^(?:[0-9a-fA-F]{3}){1,2}$") val color: String? //RGB HEX Color
)
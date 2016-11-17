package io.ticktag.service.comment.dto
import javax.validation.constraints.Size

data class UpdateComment(
        @field:Size(min = 1, max = 500) val text: String
) {}
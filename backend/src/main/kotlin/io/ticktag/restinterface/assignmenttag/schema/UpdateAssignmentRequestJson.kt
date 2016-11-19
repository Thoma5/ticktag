package  io.ticktag.restinterface.assignmenttag.schema

import io.ticktag.service.assignmenttag.dto.UpdateAssignmentTag

data class UpdateAssignmentRequestJson(
        val name: String?,
        val color: String?
) {
    constructor(a: UpdateAssignmentTag) : this(name = a.name, color = a.color)
}
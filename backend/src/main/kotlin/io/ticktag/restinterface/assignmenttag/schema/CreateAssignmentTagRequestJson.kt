package  io.ticktag.restinterface.assignmenttag.schema

import java.util.*

data class CreateAssignmentTagRequestJson(
        val projectId: UUID,
        val name: String,
        val color: String
)
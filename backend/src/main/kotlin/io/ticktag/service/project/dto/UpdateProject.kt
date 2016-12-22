package io.ticktag.service.project.dto

import java.util.*
import javax.validation.constraints.Size

data class UpdateProject(
        @field:Size(min = 3, max = 30) val name: String?,
        @field:Size(min = 3, max = 255) val description: String?,
        @field:Size(max = 255) val iconMimeInfo: String?,
        @field:Size(max = 204800) val icon: ByteArray?
){
    constructor(name: String?, description: String?, icon: String?): this(name = name, description = description, iconMimeInfo = if(icon == null) null else icon.split(",")[0],icon = if(icon == null) null else Base64.getDecoder().decode(icon.split(",")[1]))
}
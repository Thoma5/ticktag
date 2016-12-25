package io.ticktag.service.project.dto

import java.util.*
import javax.validation.constraints.Size

data class UpdateProject(
        @field:Size(min = 3, max = 30) val name: String?,
        @field:Size(min = 3, max = 255) val description: String?,
        val disabled: Boolean?,
        @field:Size(max = 255) val iconMimeInfo: String?,
        @field:Size(max = 204800) val icon: ByteArray?
){
    constructor(name: String?, description: String?, disabled: Boolean?, icon: String?) : this(name = name, description = description, disabled = disabled, iconMimeInfo = if (icon.isNullOrEmpty() || icon?.indexOf(',') ?: -1 < 0) null else icon?.split(",")?.get(0), icon = if (icon.isNullOrEmpty() || icon?.indexOf(',') ?: -1 < 0) ByteArray(0) else Base64.getDecoder()?.decode(icon?.split(",")?.get(1)))
}
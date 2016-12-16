package io.ticktag.service.user.dto

import java.util.*

data class TempImageId(
        val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as TempImageId

        if (!Arrays.equals(data, other.data)) return false
        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(data)
    }
}

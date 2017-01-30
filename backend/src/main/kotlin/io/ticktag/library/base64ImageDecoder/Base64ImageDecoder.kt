package io.ticktag.library.base64ImageDecoder

import io.ticktag.TicktagLibrary
import io.ticktag.service.TicktagValidationException
import io.ticktag.service.ValidationError
import io.ticktag.service.ValidationErrorDetail
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.net.URLConnection
import java.util.*

@TicktagLibrary
open class Base64ImageDecoder {

    public fun decode(b64img: String): Image {
        var img : ByteArray
        try {
            img = Base64.getDecoder().decode(b64img)
        } catch (e: IllegalArgumentException) {
            throw TicktagValidationException(listOf(ValidationError("image", ValidationErrorDetail.Other("invalidValue"))))
        }
        var mimeType : String? = ""
        BufferedInputStream(ByteArrayInputStream(img)).use {
            mimeType = URLConnection.guessContentTypeFromStream(it)
            if(mimeType != null && !listOf("image/png", "image/gif", "image/jpeg").contains(mimeType as String)){
                throw TicktagValidationException(listOf(ValidationError("image", ValidationErrorDetail.Other("invalidFormat"))))
            }
        }
        return Image(img , (mimeType?:""))

    }
}
package io.ticktag.restinterface

import org.springframework.core.convert.converter.Converter
import java.time.Instant

class InstantConverter : Converter<String, Instant> {
    override fun convert(source: String): Instant {
        return Instant.ofEpochMilli(source.toLong())
    }

}
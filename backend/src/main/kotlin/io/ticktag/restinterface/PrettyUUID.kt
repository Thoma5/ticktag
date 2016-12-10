package io.ticktag.restinterface

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import org.apache.commons.codec.binary.Base64
import org.springframework.core.convert.converter.Converter
import java.nio.ByteBuffer
import java.util.*

class PrettyUUIDConverter() : Converter<String, UUID> {
    override fun convert(source: String?): UUID {
        if (source == null) throw IllegalArgumentException("Source must not be null")
        return uuidFromString(source) ?: throw IllegalArgumentException("Could not convert pretty uuid")
    }
}

class PrettyUUIDModule() : SimpleModule("PrettyUuid") {
    init {
        addDeserializer(UUID::class.java, PrettyUUIDDeserializer())
        addSerializer(UUID::class.java, PrettyUUIDSerializer())
        addKeyDeserializer(UUID::class.java, PrettyUUIDKeyDeserializer())
        addKeySerializer(UUID::class.java, PrettyUUIDKeySerializer())
    }
}

class PrettyUUIDSerializer : JsonSerializer<UUID>() {
    override fun serialize(value: UUID, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(uuidToString(value))
    }
}

class PrettyUUIDDeserializer : JsonDeserializer<UUID>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): UUID {
        return uuidFromString(p.text) ?: throw JsonParseException(p, "Could not parse compressed uuid")
    }
}

class PrettyUUIDKeySerializer : JsonSerializer<UUID>() {
    override fun serialize(value: UUID, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeFieldName(uuidToString(value))
    }
}

class PrettyUUIDKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String, ctxt: DeserializationContext): Any {
        return uuidFromString(key) ?: throw JsonParseException(ctxt.parser, "Could not parse compressed uuid")
    }
}

private fun uuidToString(uuid: UUID): String {
    return Base64.encodeBase64URLSafeString(uuidToBytes(uuid))
}

private fun uuidFromString(string: String): UUID? {
    val data = Base64.decodeBase64(string)
    return bytesToUUID(data)
}

private fun bytesToUUID(bytes: ByteArray): UUID? {
    if (bytes.size != 16) return null

    val buffer = ByteBuffer.wrap(bytes)
    val hi = buffer.getLong(0)
    val lo = buffer.getLong(8)

    return UUID(hi, lo)
}

private fun uuidToBytes(uuid: UUID): ByteArray {
    val buffer = ByteBuffer.allocate(16)
    buffer.putLong(uuid.mostSignificantBits)
    buffer.putLong(uuid.leastSignificantBits)
    return buffer.array()
}

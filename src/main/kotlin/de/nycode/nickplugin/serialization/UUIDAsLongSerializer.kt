package de.nycode.nickplugin.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object UUIDAsLongSerializer : KSerializer<UUID> {
    override fun deserialize(decoder: Decoder): UUID {
        val mostSigBits = decoder.decodeLong()
        val leastSigBits = decoder.decodeLong()
        return UUID(mostSigBits, leastSigBits)
    }

    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.BYTE)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeLong(value.mostSignificantBits)
        encoder.encodeLong(value.leastSignificantBits)
    }

}
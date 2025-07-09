package org.wip.womtoolkit.utils.serializers

import javafx.scene.paint.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ColorSerializer : KSerializer<Color> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("color", PrimitiveKind.STRING)
	override fun serialize(encoder: Encoder, value: Color) = encoder.encodeString("#${value.toString().substring(2, 8)}")
	override fun deserialize(decoder: Decoder): Color = Color.web(decoder.decodeString())
}


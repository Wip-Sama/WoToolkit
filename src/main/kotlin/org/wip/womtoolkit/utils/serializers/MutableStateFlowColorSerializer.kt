package org.wip.womtoolkit.utils.serializers

import javafx.scene.paint.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MutableStateFlowColorSerializer : KSerializer<MutableStateFlow<Color>> {
    override val descriptor: SerialDescriptor = ColorSerializer.descriptor

    override fun serialize(encoder: Encoder, value: MutableStateFlow<Color>) {
        encoder.encodeSerializableValue(ColorSerializer, value.value)
    }

    override fun deserialize(decoder: Decoder): MutableStateFlow<Color> {
        val color = decoder.decodeSerializableValue(ColorSerializer)
        return MutableStateFlow(color)
    }
}


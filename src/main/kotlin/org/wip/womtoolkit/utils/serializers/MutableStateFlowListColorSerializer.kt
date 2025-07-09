package org.wip.womtoolkit.utils.serializers

import javafx.scene.paint.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MutableStateFlowListColorSerializer : KSerializer<MutableStateFlow<List<Color>>> {
    private val listSerializer = ListSerializer(ColorSerializer)
    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: MutableStateFlow<List<Color>>) {
        encoder.encodeSerializableValue(listSerializer, value.value)
    }

    override fun deserialize(decoder: Decoder): MutableStateFlow<List<Color>> {
        val list = decoder.decodeSerializableValue(listSerializer)
        return MutableStateFlow(list)
    }
}

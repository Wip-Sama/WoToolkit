package org.wip.womtoolkit.utils.serializers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MutableStateFlowListStringSerializer : KSerializer<MutableStateFlow<List<String>>> {
    private val listSerializer = ListSerializer(String.serializer())
    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: MutableStateFlow<List<String>>) {
        encoder.encodeSerializableValue(listSerializer, value.value)
    }

    override fun deserialize(decoder: Decoder): MutableStateFlow<List<String>> {
        val list = decoder.decodeSerializableValue(listSerializer)
        return MutableStateFlow(list)
    }
}

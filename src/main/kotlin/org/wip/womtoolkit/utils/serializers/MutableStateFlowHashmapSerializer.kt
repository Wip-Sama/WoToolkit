package org.wip.womtoolkit.utils.serializers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.model.services.modulesManagment.Platform

object MutableStateFlowHashmapSerializer : KSerializer<MutableStateFlow<HashMap<String, Platform>>> {
    private val hashmapSerializer = MapSerializer(String.serializer(), Platform.serializer())
    override val descriptor: SerialDescriptor = hashmapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: MutableStateFlow<HashMap<String, Platform>>) {
        encoder.encodeSerializableValue(hashmapSerializer, value.value)
    }

    override fun deserialize(decoder: Decoder): MutableStateFlow<HashMap<String, Platform>> {
        val hashmap = HashMap(decoder.decodeSerializableValue(hashmapSerializer))
        return MutableStateFlow(hashmap)
    }
}

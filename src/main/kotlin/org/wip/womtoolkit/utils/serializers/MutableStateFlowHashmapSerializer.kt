package org.wip.womtoolkit.utils.serializers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.model.services.modulesManagment.platform

object MutableStateFlowHashmapSerializer : KSerializer<MutableStateFlow<HashMap<String, platform>>> {
    private val hashmapSerializer = MapSerializer(String.serializer(), platform.serializer())
    override val descriptor: SerialDescriptor = hashmapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: MutableStateFlow<HashMap<String, platform>>) {
        encoder.encodeSerializableValue(hashmapSerializer, value.value)
    }

    override fun deserialize(decoder: Decoder): MutableStateFlow<HashMap<String, platform>> {
        val hashmap = HashMap(decoder.decodeSerializableValue(hashmapSerializer))
        return MutableStateFlow(hashmap)
    }
}

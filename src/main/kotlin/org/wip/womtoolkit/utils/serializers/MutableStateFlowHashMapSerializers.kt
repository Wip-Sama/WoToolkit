package org.wip.womtoolkit.utils.serializers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class MutableStateFlowMapSerializer<K, V>(
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>
) : KSerializer<MutableStateFlow<HashMap<K, V>>> {
    private val mapSerializer = MapSerializer(keySerializer, valueSerializer)
    override val descriptor: SerialDescriptor = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: MutableStateFlow<HashMap<K, V>>) {
        encoder.encodeSerializableValue(mapSerializer, value.value)
    }

    override fun deserialize(decoder: Decoder): MutableStateFlow<HashMap<K, V>> {
        val hashmap = HashMap(decoder.decodeSerializableValue(mapSerializer))
        return MutableStateFlow(hashmap)
    }
}


//HashMap<String, String>
object MutableStateFlowHashmapStringStringSerializer : KSerializer<MutableStateFlow<HashMap<String, String>>> {
    private val hashmapSerializer = MapSerializer(String.serializer(), String.serializer())
    override val descriptor: SerialDescriptor = hashmapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: MutableStateFlow<HashMap<String, String>>) {
        encoder.encodeSerializableValue(hashmapSerializer, value.value)
    }

    override fun deserialize(decoder: Decoder): MutableStateFlow<HashMap<String, String>> {
        val hashmap = HashMap(decoder.decodeSerializableValue(hashmapSerializer))
        return MutableStateFlow(hashmap)
    }
}

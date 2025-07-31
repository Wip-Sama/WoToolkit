package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
data class ModuleInterface (
	@Serializable(with = MutableStateFlowSerializer::class) val type: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val command: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val location: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val expectedResult: MutableStateFlow<String>? = null,
)

//HashMap<String, ModuleInterface>
object MutableStateFlowHashmapStringModuleInterfaceSerializer : KSerializer<MutableStateFlow<HashMap<String, ModuleInterface>>> {
	private val hashmapSerializer = MapSerializer(String.serializer(), ModuleInterface.serializer())
	override val descriptor: SerialDescriptor = hashmapSerializer.descriptor

	override fun serialize(encoder: Encoder, value: MutableStateFlow<HashMap<String, ModuleInterface>>) {
		encoder.encodeSerializableValue(hashmapSerializer, value.value)
	}

	override fun deserialize(decoder: Decoder): MutableStateFlow<HashMap<String, ModuleInterface>> {
		val hashmap = HashMap(decoder.decodeSerializableValue(hashmapSerializer))
		return MutableStateFlow(hashmap)
	}
}
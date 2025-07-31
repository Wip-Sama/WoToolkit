package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.utils.serializers.MutableStateFlowHashmapStringStringSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowListSerializer

@Serializable
class ModulePlatform {
	@Serializable(with = MutableStateFlowHashmapStringStringSerializer::class) val dependencies: MutableStateFlow<HashMap<String, String>> = MutableStateFlow(hashMapOf())
	@Serializable(with = MutableStateFlowListSerializer::class) val installation: MutableStateFlow<List<ModuleInstallation>> = MutableStateFlow(listOf())
	@Serializable(with = MutableStateFlowListSerializer::class) val validation: MutableStateFlow<List<ModuleValidation>> = MutableStateFlow(listOf())
	@Serializable(with = MutableStateFlowHashmapStringModulePlatformSerializer::class) val interfaces: MutableStateFlow<HashMap<String, ModuleInterface>> = MutableStateFlow(hashMapOf())
}

//HashMap<String, ModulePlatform>
object MutableStateFlowHashmapStringModulePlatformSerializer : KSerializer<MutableStateFlow<HashMap<String, ModulePlatform>>> {
	private val hashmapSerializer = MapSerializer(String.serializer(), ModulePlatform.serializer())
	override val descriptor: SerialDescriptor = hashmapSerializer.descriptor

	override fun serialize(encoder: Encoder, value: MutableStateFlow<HashMap<String, ModulePlatform>>) {
		encoder.encodeSerializableValue(hashmapSerializer, value.value)
	}

	override fun deserialize(decoder: Decoder): MutableStateFlow<HashMap<String, ModulePlatform>> {
		val hashmap = HashMap(decoder.decodeSerializableValue(hashmapSerializer))
		return MutableStateFlow(hashmap)
	}
}
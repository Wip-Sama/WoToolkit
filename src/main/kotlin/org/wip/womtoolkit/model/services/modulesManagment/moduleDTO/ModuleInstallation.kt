package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
data class ModuleInstallation (
	@Serializable(with = MutableStateFlowSerializer::class) val type: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val command: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val expectedResult: MutableStateFlow<ModuleExpectedResult> = MutableStateFlow(ModuleExpectedResult()),
)

//List<ModuleInstallation>
object MutableStateFlowListStringModulePlatformSerializer : KSerializer<MutableStateFlow<List<ModuleInstallation>>> {
	private val listSerializer = ListSerializer(ModuleInstallation.serializer())
	override val descriptor: SerialDescriptor = listSerializer.descriptor

	override fun serialize(encoder: Encoder, value: MutableStateFlow<List<ModuleInstallation>>) {
		encoder.encodeSerializableValue(listSerializer, value.value)
	}

	override fun deserialize(decoder: Decoder): MutableStateFlow<List<ModuleInstallation>> {
		return MutableStateFlow(decoder.decodeSerializableValue(listSerializer))
	}
}
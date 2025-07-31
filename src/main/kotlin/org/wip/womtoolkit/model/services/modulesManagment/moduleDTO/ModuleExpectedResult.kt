package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.utils.serializers.MutableStateFlowListSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
data class ModuleExpectedResult(
	@Serializable(with = MutableStateFlowSerializer::class) val type: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val command: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val regex: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val hash: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val search: MutableStateFlow<List<String>> = MutableStateFlow(listOf()),
	@Serializable(with = MutableStateFlowSerializer::class) val expectedResult: MutableStateFlow<ModuleExpectedResult>? = null,
)

object MutableStateFlowModuleExpectedResultSerializer : KSerializer<MutableStateFlow<ModuleExpectedResult>> {
	override val descriptor: SerialDescriptor = ModuleExpectedResult.serializer().descriptor
	override fun serialize(encoder: Encoder, value: MutableStateFlow<ModuleExpectedResult>) {
		encoder.encodeSerializableValue(ModuleExpectedResult.serializer(), value.value)
	}
	override fun deserialize(decoder: Decoder): MutableStateFlow<ModuleExpectedResult> {
		return MutableStateFlow(decoder.decodeSerializableValue(ModuleExpectedResult.serializer()))
	}
}


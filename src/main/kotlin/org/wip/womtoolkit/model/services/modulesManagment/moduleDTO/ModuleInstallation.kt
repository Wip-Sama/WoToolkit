package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.model.services.activityMonitor.ActivityMonitorService
import org.wip.womtoolkit.utils.CommandRunner
import org.wip.womtoolkit.utils.FileUtiles
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer
import kotlin.io.path.Path

@Serializable
data class ModuleInstallation (
	@Serializable(with = MutableStateFlowSerializer::class) val type: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val command: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val expectedResult: MutableStateFlow<ModuleExpectedResult> = MutableStateFlow(ModuleExpectedResult()),
) {
	fun execute(): Boolean {
		val newCommand = CommandRunner.replaceDependenciesInCommand(command.value)
//		ActivityMonitorService["ModuleManagement"].addActivity(
//			"Executing command: $newCommand",
//			Globals.logger
//		)
		val output = CommandRunner.runSimpleCommand(newCommand)

		if (output.second.isNotEmpty()) {
			Globals.logger.warning("Command '${newCommand}' failed with error: ${output.second}")
			return false
		}

		return expectedResult.value.validateResult(output.first)
	}
}

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
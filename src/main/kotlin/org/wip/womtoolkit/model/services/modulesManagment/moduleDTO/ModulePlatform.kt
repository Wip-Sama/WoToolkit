package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.model.services.modulesManagment.ModuleManagementService
import org.wip.womtoolkit.utils.CommandRunner
import org.wip.womtoolkit.utils.FileUtiles
import org.wip.womtoolkit.utils.Version
import org.wip.womtoolkit.utils.serializers.MutableStateFlowHashmapStringStringSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.io.path.Path

@Serializable
data class ModulePlatform (
	@Serializable(with = MutableStateFlowHashmapStringStringSerializer::class) val dependencies: MutableStateFlow<HashMap<String, String>> = MutableStateFlow(hashMapOf()),
	@Serializable(with = MutableStateFlowSerializer::class) val installation: MutableStateFlow<List<ModuleInstallation>> = MutableStateFlow(listOf()),
	@Serializable(with = MutableStateFlowSerializer::class) val validation: MutableStateFlow<List<ModuleValidation>> = MutableStateFlow(listOf()),
	@Serializable(with = MutableStateFlowSerializer::class) val interfaces: MutableStateFlow<HashMap<String, ModuleInterface>> = MutableStateFlow(hashMapOf()),
) {
	fun validateDependencies(): Boolean {
		return dependencies.value.all { (name, version) ->
			if (!ModuleManagementService.modules.containsKey(name)) {
				Globals.logger.info("Module $name is missing dependency $name")
				false
			}

			val module = ModuleManagementService.modules[name]!!

			if ((module.minVersion?.let { it >= Version.fromString(version) } == true) ||
				(module.maxVersion?.let { it <= Version.fromString(version) } == true)) {
				Globals.logger.info("Module $name has incompatible dependency $name with version $version")
				false
			}

			if (!module.validate()) {
				Globals.logger.info("Module $name has unvalidated dependency $name")
				false
			}
			true
		}
	}

	/**
	 * Validates the installation steps of the module platform.
	 * It checks if the files exist and are readable, and if the commands return the expected results.
	 * @throws RuntimeException if a file does not exist or is not readable, or if a command does not return the expected result.
	 */
	fun validateInstallation(): Boolean {
		return validation.value.all { it.validate() }
	}

	fun installModule(): Boolean {
		return installation.value.all { step -> step.execute() }
	}
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
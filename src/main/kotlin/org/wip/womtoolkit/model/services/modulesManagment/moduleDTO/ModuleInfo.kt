package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.model.enums.Platforms
import org.wip.womtoolkit.model.services.modulesManagment.ModuleManagementService
import org.wip.womtoolkit.utils.CommandRunner
import org.wip.womtoolkit.utils.FileUtiles
import org.wip.womtoolkit.utils.Version
import org.wip.womtoolkit.utils.serializers.MutableStateFlowListSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer
import java.io.File
import kotlin.io.path.Path

@Serializable
data class ModuleInfo(
	// Should contain all the information about a module and the functions to manage it (update/install/remove/validate)
	@Serializable(with = MutableStateFlowSerializer::class) val name: MutableStateFlow<String> = MutableStateFlow( ""),
	@Serializable(with = MutableStateFlowSerializer::class) val description: MutableStateFlow<String> = MutableStateFlow( ""),
	@Serializable(with = MutableStateFlowSerializer::class) val version: MutableStateFlow<String> = MutableStateFlow( ""),
	@Serializable(with = MutableStateFlowSerializer::class) val toolkitVersion: MutableStateFlow<String> = MutableStateFlow( ""),
	// this version follows this format "epoch.major.minor.patch"
	@Serializable(with = MutableStateFlowSerializer::class) val source: MutableStateFlow<String> = MutableStateFlow( ""),
	@Serializable(with = MutableStateFlowSerializer::class) val authors: MutableStateFlow<List<String>> = MutableStateFlow(listOf()),
	@Serializable(with = MutableStateFlowHashmapStringModulePlatformSerializer::class) val supportedPlatforms: MutableStateFlow<HashMap<String, ModulePlatform>> = MutableStateFlow(hashMapOf())
) {
	// get latest version (from source)
	// get latest compatible version (from source) (optional)

	val _supportedPlatforms
		get() = supportedPlatforms.value

	val compatibleWithPlatform
		get() = supportedPlatforms.value.containsKey(Globals.PLATFORM.name.lowercase())

	val compatibleWithToolkitVersion: Boolean
		get() {
			val versions = toolkitVersion.value.split("-")
			println(toolkitVersion.value)
			println("_ciao".split("_"))
			if (versions[0].isEmpty()) //only max
				return Version.fromString(versions[1]) <= Globals.TOOLKIT_VERSION
			if (versions[1].isEmpty()) //only min
				return Version.fromString(versions[0]) >= Globals.TOOLKIT_VERSION
			return Version.fromString(versions[0]) >= Globals.TOOLKIT_VERSION &&
					Version.fromString(versions[1]) <= Globals.TOOLKIT_VERSION
		}

	val minVersion: Version?
		get() {
			val versions = toolkitVersion.value.split("-")
			if (versions[0].isEmpty()) return null // only max
			return Version.fromString(versions[0])
		}

	val maxVersion: Version?
		get() {
			val versions = toolkitVersion.value.split("-")
			if (versions[1].isEmpty()) return null // only min
			return Version.fromString(versions[1])
		}

	fun validate(): Boolean {
		if (!compatibleWithPlatform) return false
		supportedPlatforms.value[Globals.PLATFORM.name.lowercase()]?.let { platform ->
			// all dependencies must be installed and validated
			for ((dependency, version) in platform.dependencies.value) {
				if (!ModuleManagementService.modules.containsKey(dependency)) {
					Globals.logger.info("Module $name is missing dependency $dependency")
					return false
				}
				val depModule = ModuleManagementService.modules[dependency]!!
				if (depModule.minVersion!! > Version.fromString(version) || depModule.maxVersion!! < Version.fromString(version)) {
					Globals.logger.info("Module $name has incompatible dependency $dependency with version $version")
					return false
				}
				if (!depModule.validate()) {
					Globals.logger.info("Module $name has unvalidated dependency $dependency")
					return false
				}
			}
			platform.validation.value.forEach { step ->
				when (step.type.value) {
					"hash" -> {
//						step.hash
//						step.file
						val filePath = Path("${System.getProperty("user.dir")}/Modules/${name.value}/${step.file.value}")
						if (!FileUtiles.checkItsFileAndICanRead(filePath)) {
							Globals.logger.info("Module $name has a non accessible required file ${step.file.value}")
							return false
						}
						// TODO: hash check
					}
					"command" -> {
						step.command
						step.expectedResult

						val runIn = when (Globals.PLATFORM) {
							Platforms.LINUX, Platforms.MACOS -> {
								CommandRunner.runInBash()
							}
							Platforms.WINDOWS -> {
								CommandRunner.runInPowershell()
							}
							else -> {
								Globals.logger.warning("Unsupported platform for command validation: ${Globals.PLATFORM.name}")
								return false
							}
						}

						//resolve dependency if present
						val dependencies = Regex("\\{([^}]*)}").findAll(step.command.value)
						var newCommand = step.command.value
						dependencies.forEach { matchResult ->
							val parts = matchResult.value.removePrefix("{").removeSuffix("}").split('.')
							if (parts.size != 2) {
								Globals.logger.warning("Unsupported dependency syntax ${step.command.value}")
								return false
							}
							val dependencyName = parts[0]
							val dependencyInterface = parts[1]
							ModuleManagementService.modules[dependencyName]?.let { dependencyModule ->
								val dependencyInterface =
									dependencyModule.supportedPlatforms.value[Globals.PLATFORM.name.lowercase()]!!.interfaces.value[dependencyInterface]!!
								when (dependencyInterface.type.value) {
									"location" -> {
										val filePath =
											Path("${System.getProperty("user.dir")}/Modules/${dependencyModule.name.value}/${dependencyInterface.location.value}")
										if (!FileUtiles.checkItsSomethingAndICanRead(filePath)) {
											Globals.logger.warning("Dependency $dependencyName has a non accessible required location ${dependencyInterface.location.value}")
											return false
										}
										newCommand = newCommand.replace(matchResult.value, filePath.toString())
									}

									"command" -> {
										//TODO: IDK what to do here, maybe run the command and check the output?
									}

									else -> {
										Globals.logger.warning("Unsupported dependency interface type ${dependencyInterface.type} for dependency $dependencyName")
										return false
									}
								}
							}
						}

						println("something debug ${
							runIn.apply {
								add(newCommand)
							}
						}")

						CommandRunner.customProcessBuilder(runIn.apply {
							add(newCommand)
						})

						//I have a command now I need to run it and validate the output
					}
				}
			}
		}
		return true
	}
}
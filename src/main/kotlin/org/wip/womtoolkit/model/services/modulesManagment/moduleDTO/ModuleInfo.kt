package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.utils.Version
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer
import kotlin.properties.Delegates

@Serializable
data class ModuleInfo(
	// Should contain all the information about a module and the functions to manage it (update/install/remove/validate)
	@Serializable(with = MutableStateFlowSerializer::class) val name: MutableStateFlow<String> = MutableStateFlow( ""),
	@Serializable(with = MutableStateFlowSerializer::class) val description: MutableStateFlow<String> = MutableStateFlow( ""),
	@Serializable(with = MutableStateFlowSerializer::class) val version: MutableStateFlow<String> = MutableStateFlow( ""),
	@Serializable(with = MutableStateFlowSerializer::class) val toolkitVersion: MutableStateFlow<String> = MutableStateFlow( ""),
	// this version follows this format "epoch.major.minor.patch"
	@Serializable(with = MutableStateFlowSerializer::class) val icon: MutableStateFlow<String> = MutableStateFlow( ""),
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

	var isValidated: Boolean = false
		private set

	var isValid: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
		isValidated = true
	}
		private set

	var isInstalled: Boolean = false
		private set

	var isUpdateAvailable: Boolean = false
		private set

	fun validate(): Boolean {
		isValidated = true
		if (!compatibleWithPlatform) {
			isValid = false
			return false
		}
		supportedPlatforms.value[Globals.PLATFORM.name.lowercase()]?.let { platform ->
			platform.validateDependencies()
			platform.validateInstallation()
		}
		Globals.logger.info("validated module ${name.value} successfully")
		isValid = true
		return true
	}

	fun install() {
		supportedPlatforms.value[Globals.PLATFORM.name.lowercase()]?.installModule()
		isInstalled = true
	}

	fun update() {
		//TODO()
	}

	fun uninstall() {
		//TODO()
	}

	fun searchForUpdates() {
		isUpdateAvailable = !isUpdateAvailable
	}
}
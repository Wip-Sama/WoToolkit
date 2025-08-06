package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

	private val _isValidated: MutableStateFlow<Boolean> = MutableStateFlow(false)
	val isValidated: StateFlow<Boolean>
		get() = _isValidated.asStateFlow()

	private val _isValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
	val isValid: StateFlow<Boolean>
		get() = _isValid.asStateFlow()

	private val _isInstalled: MutableStateFlow<Boolean> = MutableStateFlow(false)
	val isInstalled: StateFlow<Boolean>
		get() = _isInstalled.asStateFlow()

	private val _isUpdateAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)
	val isUpdateAvailable: StateFlow<Boolean>
		get() = _isUpdateAvailable.asStateFlow()

	fun validate(): Boolean {
		_isValidated.value = true
		if (!compatibleWithPlatform) {
			_isValid.value = false
			return false
		}
		supportedPlatforms.value[Globals.PLATFORM.name.lowercase()]?.let { platform ->
			platform.validateDependencies()
			platform.validateInstallation()
		}
		Globals.logger.info("validated module ${name.value} successfully")
		_isValid.value = true
		return true
	}

	fun install() {
		supportedPlatforms.value[Globals.PLATFORM.name.lowercase()]?.installModule()
		_isInstalled.value = true
	}

	fun searchForUpdates() {
		_isUpdateAvailable.value = !_isUpdateAvailable.value
	}

	fun update() {
		//TODO()
	}

	fun uninstall() {
		//TODO()
	}
}
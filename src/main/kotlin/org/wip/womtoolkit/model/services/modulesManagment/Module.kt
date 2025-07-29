package org.wip.womtoolkit.model.services.modulesManagment

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.utils.Version
import org.wip.womtoolkit.utils.serializers.MutableStateFlowHashmapSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowListStringSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
class Module() {
	// Should contain all the information about a module and the functions to manage it (update/install/remove/validate)
	@Serializable(with = MutableStateFlowSerializer::class) val name: MutableStateFlow<String> = MutableStateFlow( "")
	@Serializable(with = MutableStateFlowSerializer::class) val version: MutableStateFlow<String> = MutableStateFlow( "")
	@Serializable(with = MutableStateFlowSerializer::class) val description: MutableStateFlow<String> = MutableStateFlow( "")
	@Serializable(with = MutableStateFlowSerializer::class) val toolkitVersion: MutableStateFlow<String> = MutableStateFlow( "")
	// version follows this format "epoch.major.minor.patch"
	// min - max
	// min - (opt)
	// (opt) - max
	@Serializable(with = MutableStateFlowSerializer::class) val source: MutableStateFlow<String> = MutableStateFlow( "")
	@Serializable(with = MutableStateFlowListStringSerializer::class) val authors: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
	@Serializable(with = MutableStateFlowHashmapSerializer::class) val supportedPlatforms: MutableStateFlow<HashMap<String, Platform>> = MutableStateFlow(hashMapOf())

	// get latest version (from source)
	// get latest compatible version (from source) (optional)



	val compatibleWithPlatform
		get() = supportedPlatforms.value.containsKey(Globals.PLATFORM.name)

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
}
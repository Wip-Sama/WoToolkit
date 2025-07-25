package org.wip.womtoolkit.model.services.modulesManagment

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.utils.serializers.MutableStateFlowHashmapSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowListStringSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
class module {
	// Should contain all the information about a module and the functions to manage it (update/install/remove/validate)
	@Serializable(with = MutableStateFlowSerializer::class) val name: MutableStateFlow<String> = MutableStateFlow( "")
	@Serializable(with = MutableStateFlowSerializer::class) val version: MutableStateFlow<String> = MutableStateFlow( "")
	@Serializable(with = MutableStateFlowSerializer::class) val description: MutableStateFlow<String> = MutableStateFlow( "")
	@Serializable(with = MutableStateFlowSerializer::class) val toolkit_version: MutableStateFlow<String> = MutableStateFlow( "")
	@Serializable(with = MutableStateFlowSerializer::class) val source: MutableStateFlow<String> = MutableStateFlow( "")
	@Serializable(with = MutableStateFlowListStringSerializer::class) val authors: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
	@Serializable(with = MutableStateFlowHashmapSerializer::class) val supported_platforms: MutableStateFlow<HashMap<String, platform>> = MutableStateFlow(hashMapOf())
}
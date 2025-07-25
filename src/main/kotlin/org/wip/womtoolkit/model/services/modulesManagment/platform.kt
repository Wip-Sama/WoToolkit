package org.wip.womtoolkit.model.services.modulesManagment

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.utils.serializers.MutableStateFlowListStringSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
class platform {
	@Serializable(with = MutableStateFlowListStringSerializer::class) val dependencies: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
	//validation installation
}
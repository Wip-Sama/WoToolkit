package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
data class ModuleValidation (
	@Serializable(with = MutableStateFlowSerializer::class) val type: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val command: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val hash: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val expectedResult: MutableStateFlow<String> = MutableStateFlow(""),
)
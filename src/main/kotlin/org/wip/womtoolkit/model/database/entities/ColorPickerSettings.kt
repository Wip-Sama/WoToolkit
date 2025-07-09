package org.wip.womtoolkit.model.database.entities

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
class ColorPickerSettings {
	@Serializable(with = MutableStateFlowSerializer::class)
	val hueSelector = MutableStateFlow(false)
	@Serializable(with = MutableStateFlowSerializer::class)
	var showAlpha = MutableStateFlow(false)
	@Serializable(with = MutableStateFlowSerializer::class)
	var advancedMode = MutableStateFlow(false)
}

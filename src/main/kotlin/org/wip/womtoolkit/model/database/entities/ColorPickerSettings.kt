package org.wip.womtoolkit.model.database.entities

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.utils.serializers.MutableStateFlowBooleanSerializer

@Serializable
class ColorPickerSettings {
	@Serializable(with = MutableStateFlowBooleanSerializer::class)
	val hueSelector = MutableStateFlow(false)
	@Serializable(with = MutableStateFlowBooleanSerializer::class)
	var showAlpha = MutableStateFlow(false)
	@Serializable(with = MutableStateFlowBooleanSerializer::class)
	var advancedMode = MutableStateFlow(false)
}

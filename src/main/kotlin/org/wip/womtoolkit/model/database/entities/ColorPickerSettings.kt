package org.wip.womtoolkit.model.database.entities

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
class ColorPickerSettings {
	@Serializable(with = MutableStateFlowSerializer::class)	val selectorMode: MutableStateFlow<Boolean> = MutableStateFlow(false) // true = image, false = hue
	@Serializable(with = MutableStateFlowSerializer::class)	val alphaAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)
}

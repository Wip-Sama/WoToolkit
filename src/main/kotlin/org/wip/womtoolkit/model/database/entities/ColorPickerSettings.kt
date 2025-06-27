package org.wip.womtoolkit.model.database.entities

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
internal data class ColorPickerSettingsData(
	var hueSelector: Boolean = true,
	var showAlpha: Boolean = false,
	var advancedMode: Boolean = false
)

@Serializable
class ColorPickerSettings {
	private val _hueSelectorFlow: MutableStateFlow<Boolean> by lazy { MutableStateFlow(false) }
	val hueSelectorFlow: MutableStateFlow<Boolean> get() = _hueSelectorFlow
	var hueSelector: Boolean
		get() = _hueSelectorFlow.value
		set(value) {
			_hueSelectorFlow.value = value
		}

	private val _showAlphaFlow: MutableStateFlow<Boolean> by lazy { MutableStateFlow(false) }
	val showAlphaFlow: MutableStateFlow<Boolean> get() = _showAlphaFlow
	var showAlpha: Boolean
		get() = _showAlphaFlow.value
		set(value) {
			_showAlphaFlow.value = value
		}

	private val _advancedModeFlow: MutableStateFlow<Boolean> by lazy { MutableStateFlow(false) }
	val advancedModeFlow: MutableStateFlow<Boolean> get() = _advancedModeFlow
	var advancedMode: Boolean
		get() = _advancedModeFlow.value
		set(value) {
			_advancedModeFlow.value = value
		}
}

object ColorPickerSettingsSerializer : KSerializer<ColorPickerSettings> {
	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorPickerSettings") {
		element<Boolean>("hueSelector")
		element<Boolean>("showAlpha")
		element<Boolean>("advancedMode")
	}

	override fun serialize(encoder: Encoder, value: ColorPickerSettings) {
		val data = ColorPickerSettingsData(
			hueSelector = value.hueSelector,
			showAlpha = value.showAlpha,
			advancedMode = value.advancedMode
		)
		encoder.encodeSerializableValue(ColorPickerSettingsData.serializer(), data)
	}

	override fun deserialize(decoder: Decoder): ColorPickerSettings {
		val data = decoder.decodeSerializableValue(ColorPickerSettingsData.serializer())
		return ColorPickerSettings().apply {
			hueSelector = data.hueSelector
			showAlpha = data.showAlpha
			advancedMode = data.advancedMode
		}
	}
}
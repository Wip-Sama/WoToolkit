package org.wip.womtoolkit.model.database.entities

import javafx.scene.paint.Color
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
internal data class UserSettingsData(
	var theme: String,
	var accent: String,
	var accentHistory: List<String>,
	var localization: String,
	var startingPage: String,
	var colorPickerSettings: ColorPickerSettings
)


@Serializable(with = UserSettingsSerializer::class)
class UserSettings {
	private val scope = MainScope()

	private val _themeFlow: MutableStateFlow<String> by lazy { MutableStateFlow("dark") }
	val themeFlow: StateFlow<String> get() = _themeFlow
	var theme: String
		get() = _themeFlow.value
		set(value) {
			_themeFlow.value = value
		}

	private val _accentFlow: MutableStateFlow<Color> by lazy { MutableStateFlow(Color.web("#ff0000ff")) }
	val accentFlow: StateFlow<Color> get() = _accentFlow
	var accent: Color
		get() = _accentFlow.value
		set(value) {
			_accentFlow.value = value
		}

	private val _accentHistoryFlow: MutableStateFlow<List<Color>> by lazy { MutableStateFlow(emptyList()) }
	val accentHistoryFlow: StateFlow<List<Color>> get() = _accentHistoryFlow
	var accentHistory: List<Color>
		get() = _accentHistoryFlow.value
		set(value) {
			_accentHistoryFlow.value = value
		}

	private val _localizationFlow: MutableStateFlow<String> by lazy { MutableStateFlow("enEN") }
	val localizationFlow: StateFlow<String> get() = _localizationFlow
	var localization: String
		get() = _localizationFlow.value
		set(value) {
			_localizationFlow.value = value
		}

	private val _startingPageFlow: MutableStateFlow<String> by lazy { MutableStateFlow("None") }
	val startingPageFlow: StateFlow<String> get() = _startingPageFlow
	var startingPage: String
		get() = _startingPageFlow.value
		set(value) {
			_startingPageFlow.value = value
		}

	private val _colorPickerSettingsFlow: MutableStateFlow<ColorPickerSettings> by lazy { MutableStateFlow(ColorPickerSettings()) }
	val colorPickerSettingsFlow: StateFlow<ColorPickerSettings> get() = _colorPickerSettingsFlow.asStateFlow()
	var colorPickerSettings: ColorPickerSettings
		get() = _colorPickerSettingsFlow.value
		set(value) {
			_colorPickerSettingsFlow.value = value
		}

	init {
		scope.launch {
			colorPickerSettingsFlow.onEach { newValue ->
				if (colorPickerSettings !== newValue) { //si potrebbe usare un controllo per puntatore !==
					colorPickerSettings = newValue
				}
			}.collect()
		}
	}
}

object UserSettingsSerializer : KSerializer<UserSettings> {
	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("UserSettings") {
		element<String>("theme")
		element<String>("accent")
		element<List<String>>("accentHistory")
		element<String>("localization")
		element<String>("startingPage")
		element<ColorPickerSettings>("colorPickerSettings")
	}

	override fun serialize(encoder: Encoder, value: UserSettings) {
		val data = UserSettingsData(
			theme = value.theme,
			accent = value.accent.toString().replace("0x", "#"),
			accentHistory = value.accentHistory.map { it.toString().replace("0x", "#") },
			localization = value.localization,
			startingPage = value.startingPage,
			colorPickerSettings = value.colorPickerSettings
		)
		UserSettingsData.serializer().serialize(encoder, data)
	}

	override fun deserialize(decoder: Decoder): UserSettings {
		val data = UserSettingsData.serializer().deserialize(decoder)
		return UserSettings().apply {
			theme = data.theme
			accent = Color.web(data.accent)
			accentHistory = data.accentHistory.map { Color.web(it) }
			localization = data.localization
			startingPage = data.startingPage
			colorPickerSettings = data.colorPickerSettings
		}
	}
}
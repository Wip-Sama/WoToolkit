package org.wip.womtoolkit.model.database.entities

import javafx.scene.paint.Color
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class UserSettings {
	@Transient
	private val scope = MainScope()

	@Transient
	val theme = MutableStateFlow("dark")

	@Transient
	val accent: MutableStateFlow<Color> = MutableStateFlow(Color.web("#ff0000ff")!!)

	@Transient
	val accentHistory: MutableStateFlow<List<Color>> = MutableStateFlow(emptyList())

	@Transient
	val localization = MutableStateFlow("enEN")

	@Transient
	val startingPage = MutableStateFlow("None")

	@Transient
	val colorPickerSettings = MutableStateFlow(ColorPickerSettings())

	@Transient
	var disableAnimations = MutableStateFlow(false)

	// Serializable properties for persistence
	var themeValue: String = "dark"
	var accentValue: String = "#ff0000ff"
	var accentHistoryValue: List<String> = emptyList()
	var localizationValue: String = "enEN"
	var startingPageValue: String = "None"
	var disableAnimationsValue: Boolean = false

	init {
		// Initialize StateFlows from serializable values
		theme.value = themeValue
		accent.value = Color.web(accentValue)!!
		accentHistory.value = accentHistoryValue.map { Color.web(it)!! }
		localization.value = localizationValue
		startingPage.value = startingPageValue
		disableAnimations.value = disableAnimationsValue

		scope.launch {
			colorPickerSettings.onEach { newValue ->
				if (colorPickerSettings.value !== newValue) {
					colorPickerSettings.value = newValue
				}
			}.collect()
		}

		scope.launch {
			accent.collectLatest {
				accentHistory.value = accentHistory.value.toMutableList().apply {
					if (!contains(it)) {
						add(it)
					}
				}.takeLast(5)
			}

			accentHistory.collectLatest { history ->
				if (history.size > 5) {
					accentHistory.value = history.takeLast(5)
				}
			}
		}

		// Sync StateFlow changes back to serializable properties
		scope.launch {
			theme.collectLatest { themeValue = it }
		}
		scope.launch {
			accent.collectLatest { accentValue = it.toString() }
		}
		scope.launch {
			accentHistory.collectLatest { accentHistoryValue = it.map { color -> color.toString() } }
		}
		scope.launch {
			localization.collectLatest { localizationValue = it }
		}
		scope.launch {
			startingPage.collectLatest { startingPageValue = it }
		}
		scope.launch {
			disableAnimations.collectLatest { disableAnimationsValue = it }
		}
	}
}

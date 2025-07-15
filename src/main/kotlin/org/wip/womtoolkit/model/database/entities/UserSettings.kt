package org.wip.womtoolkit.model.database.entities

import javafx.scene.paint.Color
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.utils.serializers.MutableStateFlowColorSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowListColorSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
class UserSettings {
	@Serializable(MutableStateFlowSerializer::class) val theme = MutableStateFlow("dark")
	@Serializable(MutableStateFlowColorSerializer::class) val accent: MutableStateFlow<Color> = MutableStateFlow(Color.web("#ff0000ff")!!)
	@Serializable(MutableStateFlowListColorSerializer::class) val accentHistory: MutableStateFlow<List<Color>> = MutableStateFlow(emptyList())
	@Serializable(MutableStateFlowSerializer::class)	val localization = MutableStateFlow("enEN")
	@Serializable(MutableStateFlowSerializer::class)	val startingPage = MutableStateFlow("None")
	@Serializable(MutableStateFlowSerializer::class)	val disableAnimations = MutableStateFlow(false)
	@Serializable val colorPickerSettings = ColorPickerSettings()
	@Serializable val notificationSettings = NotificationSettings()

	init {
		val scope = MainScope()
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
	}
}

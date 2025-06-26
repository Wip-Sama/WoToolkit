package org.wip.womtoolkit

import javafx.scene.paint.Color
import java.util.logging.Level
import java.util.logging.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonObject
import org.wip.womtoolkit.model.database.Database

object Globals {
	const val LOCALES_PATH: String = "/locales/"

	lateinit var jsonDatabase: JsonObject

	val logger: Logger? = Logger.getLogger(javaClass.getName())

	val isDebug: Boolean by lazy {
		java.lang.management.ManagementFactory.getRuntimeMXBean()
			.inputArguments.any { it.contains("-agentlib:jdwp") }
	}

	private val _themeFlow: MutableStateFlow<String> by lazy { MutableStateFlow(Database.loadTheme()) }
	val themeFlow: StateFlow<String> get() = _themeFlow
	var theme: String
		get() = _themeFlow.value
		set(value) {
			_themeFlow.value = value
			Database.writeTheme(value)
		}

	private val _accentFlow: MutableStateFlow<Color> by lazy { MutableStateFlow(Database.loadAccentColor()) }
	val accentFlow: StateFlow<Color> get() = _accentFlow
	var accent: Color
		get() = _accentFlow.value
		set(value) {
			_accentFlow.value = value
			Database.writeAccentColor(value)
		}

	init {
		logger?.level = Level.WARNING
	}
}
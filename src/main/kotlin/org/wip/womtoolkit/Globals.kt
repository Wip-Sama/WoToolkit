package org.wip.womtoolkit

import javafx.scene.paint.Color
import java.util.logging.Level
import java.util.logging.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Globals {
	const val LOCALES_PATH: String = "/locales/"
//	val logger: Logger? = Logger.getLogger(WomToolkit::class.java.getName())
	val logger: Logger? = Logger.getLogger(javaClass.getName())
	val isDebug: Boolean by lazy {
		java.lang.management.ManagementFactory.getRuntimeMXBean()
			.inputArguments.any { it.contains("-agentlib:jdwp") }
	}

	private val _themeFlow = MutableStateFlow("dark")
	val themeFlow: StateFlow<String> get() = _themeFlow
	var theme: String
		get() = _themeFlow.value
		set(value) { _themeFlow.value = value }

	private val _accentFlow = MutableStateFlow(Color.DARKRED) // Default accent color
	val accentFlow: StateFlow<Color> get() = _accentFlow
	var accent: Color
		get() = _accentFlow.value
		set(value) { _accentFlow.value = value }

	init {
		logger?.level = Level.WARNING
	}
}
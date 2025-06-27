package org.wip.womtoolkit.model

import java.util.*

//TODO: Add documentation
//TODO: Log missing localization files and localizations

class LocalizationMap(private val language: String) {
	private val properties = Properties()

	init {
		javaClass.getResourceAsStream("${Globals.LOCALES_PATH}$language.properties").use {
			if (it == null) {
				Globals.logger?.warning("Localization file for language: '$language' not found")
				throw IllegalArgumentException("Localization file for language: '$language' not found")
			}
			properties.load(it)
		}
	}

	fun getLocale(key: String?): String {
		val value = properties.getProperty(key) ?: "__MISSING_${key}_${language}__"
		if (value == "__MISSING__") {
			Globals.logger?.warning("Missing property: $key in $language")
		}
		return value
	}
}
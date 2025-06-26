package org.wip.womtoolkit.model.database

import javafx.scene.paint.Color
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import org.wip.womtoolkit.Globals

object Database {
	fun loadTheme(): String {
		return Globals.jsonDatabase["theme"]?.jsonPrimitive?.content ?: "dark"
	}

	fun writeTheme(theme: String) {
		Globals.jsonDatabase = JsonObject(Globals.jsonDatabase.toMutableMap().apply {
			put("theme", JsonPrimitive(theme))
		})
	}

	fun loadAccentColor(): Color {
		return Color.web(Globals.jsonDatabase["accentColor"]?.jsonPrimitive?.content ?: "#FFFFFF")
	}

	fun writeAccentColor(color: Color) {
		Globals.jsonDatabase = JsonObject(Globals.jsonDatabase.toMutableMap().apply {
			put("accentColor", JsonPrimitive(color.toString().replace("0x", "#")))
		})
	}

	fun loadLocale(): String? {
		return Globals.jsonDatabase["locale"]?.jsonPrimitive?.content
	}

	fun writeLocale(locale: String) {
		Globals.jsonDatabase = JsonObject(Globals.jsonDatabase.toMutableMap().apply {
			put("locale", JsonPrimitive(locale))
		})
	}
}
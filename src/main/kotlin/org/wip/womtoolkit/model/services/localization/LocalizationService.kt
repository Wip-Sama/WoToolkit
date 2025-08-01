package org.wip.womtoolkit.model.services.localization

import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import org.wip.womtoolkit.model.Globals
import java.io.File
import java.net.JarURLConnection
import java.util.concurrent.Callable
import kotlin.sequences.forEach

//TODO: if possibile remove it's dependency on javafx
/**
 * @author Wip
 * */
object LocalizationService {
	private const val DEFAULT_LANGUAGE: String = "enEN"
	private val availableLocales = mutableSetOf<String>(DEFAULT_LANGUAGE)
	val locales : Set<String>
		get() = availableLocales

	val currentLocaleProperty = SimpleStringProperty(DEFAULT_LANGUAGE)
	var currentLocale: String
		get() = currentLocaleProperty.get()
		set(value) {
			if (value !in availableLocales) {
				Globals.logger.warning("Language '$value' not found, falling back to '${currentLocaleProperty.get()}'")
				currentLocaleProperty.set(currentLocaleProperty.get())
			} else {
				Globals.logger.info("Language changed to '$value'")
				localizations.putIfAbsent(value, LocalizationMap(value))
				currentLocaleProperty.set(value)
			}
		}

	private val localizations = mutableMapOf<String, LocalizationMap>(Pair(currentLocale, LocalizationMap(currentLocale)))

	init {
		val resourceUrl = javaClass.getResource(Globals.LOCALES_PATH)
		if (resourceUrl != null) {
			when (resourceUrl.protocol) {
				"file" -> {
					File(resourceUrl.toURI()).listFiles()?.forEach {
						if (it.isFile && it.extension == "properties") {
							availableLocales.add(it.nameWithoutExtension)
						}
					}
				}
				"jar" -> {
					val jarConnection = resourceUrl.openConnection() as JarURLConnection
					val jarFile = jarConnection.jarFile
					val path = Globals.LOCALES_PATH.removePrefix("/")
					jarFile.entries().asSequence()
						.filter { it.name.startsWith(path) && it.name.endsWith(".properties") }
						.forEach {
							val fileName = File(it.name).nameWithoutExtension
							availableLocales.add(fileName)
						}
				}
				else -> {
					Globals.logger?.warning("Resource protocol '${resourceUrl.protocol}' not supported")
				}
			}
		} else {
			Globals.logger?.severe("Locale folder '${Globals.LOCALES_PATH}' not found")
		}

		val our = File(javaClass.getResource(Globals.LOCALES_PATH)!!.file)
			.listFiles()
		our?.forEach { if (it.isFile && it.extension == "properties") availableLocales.add(it.nameWithoutExtension) }
	}

	private fun getLocaleOrDefault(key: String?): String {
		return if (key == null || key.isBlank()) {
			throw IllegalArgumentException("Key cannot be null or blank")
		} else {
			localizations[currentLocale]?.getLocale(key)
				?: localizations[DEFAULT_LANGUAGE]?.getLocale(key)
				?: "__MISSING_${key}__"
		}
	}

	fun localizedStringBinding(key: String?): StringBinding {
		return Bindings.createStringBinding(Callable {
			var localizedString: String = getLocaleOrDefault(key)

			if (localizedString.startsWith("__MISSING_") && !Globals.isDebug) {
				localizedString = ""
			}

			//TODO: This should be recursive
			Regex("\\[(.*?)]").findAll(localizedString).map { it.groupValues[1] }
				.forEach { localizedString = localizedString.replace("[$it]", getLocaleOrDefault(it)) }
			localizedString
		}, currentLocaleProperty)
	}

	fun lsb(key: String?): StringBinding {
		return localizedStringBinding(key)
	}

	//Parametrized version of localizedStringBinding
	fun localizedStringBinding(key: String?, vararg args: ObservableValue<String?>?): StringBinding {
		val observables = arrayOf<Observable>(currentLocaleProperty, *args.map { it as Observable }.toTypedArray())

		return Bindings.createStringBinding(Callable {
			var localizedString: String = getLocaleOrDefault(key)

			if (localizedString.startsWith("__MISSING_") && !Globals.isDebug) {
				localizedString = ""
			}

			args.indices.forEach {
				if (args[it] == null) {
					Globals.logger?.warning("Argument at index $it is null for key '$key'")
				} else {
					localizedString = localizedString.replace("{$it}", args[it]!!.getValue()!!)
				}
			}

			//TODO: This should be recursive
			Regex("\\[(.*?)]").findAll(localizedString).map { it.groupValues[1] }
				.forEach { localizedString = localizedString.replace("[$it]", getLocaleOrDefault(it)) }

			localizedString
		}, *observables)
	}

	fun lsb(key: String?, vararg arg: ObservableValue<String?>?): StringBinding {
		return localizedStringBinding(key, *arg)
	}

}

object Lsp {
	fun localizedStringProperty(key: String?): ObservableValue<String> {
		return LocalizationService.localizedStringBinding(key)
	}

	fun lsb(key: String?): ObservableValue<String> {
		return localizedStringProperty(key)
	}

	fun localizedStringProperty(key: String?, vararg args: ObservableValue<String?>?): ObservableValue<String> {
		return LocalizationService.localizedStringBinding(key, *args)
	}

	fun lsb(key: String?, vararg args: ObservableValue<String?>?): ObservableValue<String> {
		return localizedStringProperty(key, *args)
	}
}
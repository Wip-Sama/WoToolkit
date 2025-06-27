package org.wip.womtoolkit.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.model.database.entities.UserSettings

@Serializable
internal data class ApplicationSettingsData(
	val userSettings: UserSettings
)

@Serializable(with = ApplicationSettingsSerializer::class)
object ApplicationSettings {
	val userSettings = UserSettings()
}

object AS {
	val userSettings: UserSettings
		get() = ApplicationSettings.userSettings

	val uS: UserSettings
		get() = ApplicationSettings.userSettings
}

object ApplicationSettingsSerializer : KSerializer<ApplicationSettings> {
	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorPickerSettings") {
		element<UserSettings>("userSettings")
	}

	override fun serialize(encoder: Encoder, value: ApplicationSettings) {
		val data = ApplicationSettingsData(
			userSettings = value.userSettings
		)
		encoder.encodeSerializableValue(ApplicationSettingsData.serializer(), data)
	}

	override fun deserialize(decoder: Decoder): ApplicationSettings {
		val data = decoder.decodeSerializableValue(ApplicationSettingsData.serializer())
		return ApplicationSettings.apply {
			//TODO: probably should do a from data inside UserSettings
			userSettings.apply {
				theme = data.userSettings.theme
				accent = data.userSettings.accent
				accentHistory = data.userSettings.accentHistory
				localization = data.userSettings.localization
				startingPage = data.userSettings.startingPage
				colorPickerSettings = data.userSettings.colorPickerSettings
			}
		}
	}
}
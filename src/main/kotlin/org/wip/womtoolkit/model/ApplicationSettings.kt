package org.wip.womtoolkit.model

import javafx.scene.paint.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.model.database.entities.UserSettings
import org.wip.womtoolkit.utils.serializers.ColorSerializer

//@Serializable
//internal data class ApplicationSettingsData(
////	val userSettings: UserSettings
//	@Serializable(with = ColorSerializer::class)
//	var color: Color,
//	val userSettings: UserSettings
//)

@Serializable
object ApplicationSettings {
	val userSettings = UserSettings()
	var color: Color = Color.RED
}

object AS {
	val userSettings: UserSettings
		get() = ApplicationSettings.userSettings

	val uS: UserSettings
		get() = ApplicationSettings.userSettings
}

//object ApplicationSettingsSerializer : KSerializer<ApplicationSettings> {
//	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorPickerSettings") {
//		element<UserSettings>("userSettings")
//	}
//
//	override fun serialize(encoder: Encoder, value: ApplicationSettings) {
//		val data = ApplicationSettingsData(
//			userSettings = value.userSettings,
//			color = value.color
//		)
//		encoder.encodeSerializableValue(ApplicationSettingsData.serializer(), data)
//	}
//
//	override fun deserialize(decoder: Decoder): ApplicationSettings {
//		val data = decoder.decodeSerializableValue(ApplicationSettingsData.serializer())
//		return ApplicationSettings.apply {
//			userSettings.apply {
//				theme.value = data.userSettings.theme.value
//				accent.value = data.userSettings.accent.value
//				accentHistory.value = data.userSettings.accentHistory.value
//				localization.value = data.userSettings.localization.value
//				startingPage.value = data.userSettings.startingPage.value
//				colorPickerSettings.value = data.userSettings.colorPickerSettings.value
//			}
//			color = data.color
//		}
//	}
//}
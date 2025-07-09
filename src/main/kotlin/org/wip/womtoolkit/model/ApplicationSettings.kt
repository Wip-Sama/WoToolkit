package org.wip.womtoolkit.model

import javafx.scene.paint.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.model.database.entities.UserSettings
import org.wip.womtoolkit.utils.serializers.ColorSerializer

object ApplicationSettingsSerializer : KSerializer<ApplicationSettings> {
	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ApplicationSettings") {
		element("userSettings", UserSettings.serializer().descriptor)
		element("color", ColorSerializer.descriptor)
	}

	override fun serialize(encoder: Encoder, value: ApplicationSettings) {
		val composite = encoder.beginStructure(descriptor)
		composite.encodeSerializableElement(descriptor, 0, UserSettings.serializer(), value.userSettings)
		composite.encodeSerializableElement(descriptor, 1, ColorSerializer, value.color)
		composite.endStructure(descriptor)
	}

	override fun deserialize(decoder: Decoder): ApplicationSettings {
		val dec = decoder.beginStructure(descriptor)
		var userSettings: UserSettings? = null
		var color: Color? = null
		loop@ while (true) {
			when (val index = dec.decodeElementIndex(descriptor)) {
				0 -> userSettings = dec.decodeSerializableElement(descriptor, 0, UserSettings.serializer())
				1 -> color = dec.decodeSerializableElement(descriptor, 1, ColorSerializer)
				CompositeDecoder.DECODE_DONE -> break@loop
				else -> throw IllegalStateException("Unexpected index: $index")
			}
		}
		dec.endStructure(descriptor)
		val settings = ApplicationSettings
		userSettings?.let { settings.userSettings.theme.value = it.theme.value }
		color?.let { settings.color = it }
		return settings
	}
}

@Serializable(with = ApplicationSettingsSerializer::class)
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

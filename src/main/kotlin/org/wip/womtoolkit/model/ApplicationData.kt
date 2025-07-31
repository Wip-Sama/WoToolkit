package org.wip.womtoolkit.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.wip.womtoolkit.model.database.entities.SlicerSettings
import org.wip.womtoolkit.model.database.entities.UserSettings
import kotlin.reflect.full.memberProperties

object ApplicationDataSerializer : KSerializer<ApplicationData> {
	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ApplicationData") {
		element("userSettings", UserSettings.serializer().descriptor)
		element("slicerSettings", SlicerSettings.serializer().descriptor)
	}

	override fun serialize(encoder: Encoder, value: ApplicationData) {
		val composite = encoder.beginStructure(descriptor)
		composite.encodeSerializableElement(descriptor, 0, UserSettings.serializer(), value.userSettings)
		composite.encodeSerializableElement(descriptor, 1, SlicerSettings.serializer(), value.slicerSettings)
		composite.endStructure(descriptor)
	}

	override fun deserialize(decoder: Decoder): ApplicationData {
		val dec = decoder.beginStructure(descriptor)
		var userSettings: UserSettings? = null
		var slicerSettings: SlicerSettings? = null
		loop@while (true) {
			when (val index = dec.decodeElementIndex(descriptor)) {
				0 -> userSettings = dec.decodeSerializableElement(descriptor, 0, UserSettings.serializer())
				1 -> slicerSettings = dec.decodeSerializableElement(descriptor, 1, SlicerSettings.serializer())
				CompositeDecoder.DECODE_DONE -> break@loop
				else -> throw IllegalStateException("Unexpected index: $index")
			}
		}
		dec.endStructure(descriptor)

		userSettings?.let { nonNullUserSettings ->
			ApplicationData.userSettings.copyStateFlowsFrom(nonNullUserSettings)
		}
		slicerSettings?.let { nonNullSlicerSettings ->
			ApplicationData.slicerSettings.copyStateFlowsFrom(nonNullSlicerSettings)
		}
		return ApplicationData
	}
}

fun <T : Any> T.copyStateFlowsFrom(other: T) {
	val thisClass = this::class

	thisClass.memberProperties.forEach { prop ->
		val thisValue = prop.getter.call(this)
		val otherValue = prop.getter.call(other)

		when {
			thisValue is MutableStateFlow<*> && otherValue is MutableStateFlow<*> -> {
				try {
					@Suppress("UNCHECKED_CAST")
					(thisValue as MutableStateFlow<Any?>).value = otherValue.value
				} catch (e: Exception) {
					Globals.logger.warning("Error in copyStateFlowsFrom: ${thisClass.qualifiedName}, proprietà: ${prop.name}, errore: ${e.message}")
				}
			}
			// Se la proprietà è una classe serializzabile custom, copia ricorsivamente
			thisValue != null && otherValue != null &&
					thisValue::class.annotations.any { it.annotationClass.simpleName == "Serializable" } -> {
				try {
					thisValue.copyStateFlowsFrom(otherValue)
				} catch (_: Exception) {}
			}
		}
	}
}

@Serializable(with = ApplicationDataSerializer::class)
object ApplicationData {
	val userSettings = UserSettings()
	val slicerSettings = SlicerSettings()
}

object AS {
	val userSettings: UserSettings
		get() = ApplicationData.userSettings

	val uS: UserSettings
		get() = ApplicationData.userSettings
}

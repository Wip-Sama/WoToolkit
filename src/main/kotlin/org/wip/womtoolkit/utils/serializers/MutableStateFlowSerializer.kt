package org.wip.womtoolkit.utils.serializers

import javafx.scene.paint.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class MutableStateFlowSerializer<T>(private val dataSerializer: KSerializer<T>) : KSerializer<MutableStateFlow<T>> {
	override val descriptor: SerialDescriptor = dataSerializer.descriptor
	override fun serialize(encoder: Encoder, value: MutableStateFlow<T>) = dataSerializer.serialize(encoder, value.value)
	override fun deserialize(decoder: Decoder) = MutableStateFlow(dataSerializer.deserialize(decoder))
}

class MutableStateFlowBooleanSerializer : KSerializer<MutableStateFlow<Boolean>> {
	override val descriptor = buildClassSerialDescriptor("MutableStateFlowBoolean") {
		element<Boolean>("value")
	}

	override fun serialize(encoder: Encoder, value: MutableStateFlow<Boolean>) {
		encoder.encodeBoolean(value.value)
	}

	override fun deserialize(decoder: Decoder): MutableStateFlow<Boolean> {
		return MutableStateFlow(decoder.decodeBoolean())
	}
}

class MutableStateFlowStringSerializer : KSerializer<MutableStateFlow<String>> {
	override val descriptor = buildClassSerialDescriptor("MutableStateFlowString") {
		element<String>("value")
	}

	override fun serialize(encoder: Encoder, value: MutableStateFlow<String>) {
		encoder.encodeString(value.value)
	}

	override fun deserialize(decoder: Decoder): MutableStateFlow<String> {
		return MutableStateFlow(decoder.decodeString())
	}
}

class MutableStateFlowColorSerializer : KSerializer<MutableStateFlow<Color>> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MutableColor", PrimitiveKind.STRING)
	override fun serialize(encoder: Encoder, value: MutableStateFlow<Color>) {
		encoder.encodeSerializableValue(ColorSerializer(), value.value)
	}
	override fun deserialize(decoder: Decoder): MutableStateFlow<Color> {
		val color = decoder.decodeSerializableValue(ColorSerializer())
		return MutableStateFlow(color)
	}
}

class MutableStateFlowListColorSerializer : KSerializer<MutableStateFlow<List<Color>>> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MutableColorList", PrimitiveKind.STRING)
	override fun serialize(encoder: Encoder, value: MutableStateFlow<List<Color>>) {
		encoder.encodeSerializableValue(ListSerializer(ColorSerializer()), value.value)
	}
	override fun deserialize(decoder: Decoder): MutableStateFlow<List<Color>> {
		val list = decoder.decodeSerializableValue(ListSerializer(ColorSerializer()))
		return MutableStateFlow(list)
	}
}
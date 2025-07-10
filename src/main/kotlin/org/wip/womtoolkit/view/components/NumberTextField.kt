package org.wip.womtoolkit.view.components

import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import java.util.function.UnaryOperator
import kotlin.text.toDouble
import kotlin.text.toInt

class NumberTextField : TextField() {
	var allowNegative = false
		get() = field
		set(value) {
			field = value
		}
	var allowDecimal = false
		get() = field
		set(value) {
			field = value
		}

	var minimum: Double? = null
		get() {
			return if (field == null) {
				if (allowNegative)
					return if (allowDecimal) Double.NEGATIVE_INFINITY else Int.MIN_VALUE.toDouble()
				else
					return 0.0
			} else {
				field
			}
		}
		set(value) {
			field = value
		}

	var maximum: Double = Double.MAX_VALUE
		get() = field
		set(value) {
			field = value
		}

	var defaultValue: Double = 0.0
		get() = field
		set(value) {
			field = value
			text = value.toString()
		}

	val value: Number
		get() = try {
			if (allowDecimal)
				text.toDouble()
			else
				text.toInt()
		} catch (e: NumberFormatException) {
			if (allowDecimal)
				defaultValue.toDouble()
			else
				defaultValue.toInt()
		}

	val regex: Regex
		get() = "^${if (allowNegative) "-?" else ""}\\d*${if (allowDecimal) "\\.?" else ""}\\d*$".toRegex()


	var regexLimiter: UnaryOperator<TextFormatter.Change?> = UnaryOperator { change: TextFormatter.Change? ->
		val newText = change?.controlNewText
		newText?.matches(regex).let { isValid ->
			if (isValid == true) {
				change
			} else {
				null
			}
		}
	}

	val limiter = UnaryOperator { change: TextFormatter.Change? ->
		if (change == null) {
			change
		} else {
			val newText = change.controlNewText

			// Permetti campo vuoto o solo il segno negativo
			if (newText.isEmpty() || (newText == "-" && allowNegative)) {
				change
			} else {
				try {
					val value = newText.toDouble()

					// Verifica i vincoli e restituisce null per rifiutare la modifica
					when {
						!allowNegative && value < 0 -> null
						!allowDecimal && newText.contains('.') -> null
						else -> change
					}
				} catch (e: NumberFormatException) {
					null
				}
			}
		}
	}

	init {
		textFormatter = TextFormatter<String>(regexLimiter)

		focusedProperty().addListener { _, _, hasFocus ->
			if (!hasFocus && text.isNotEmpty()) {
				val currentValue = text.toDoubleOrNull()
				if (currentValue != null) {
					val clampedValue = currentValue.coerceIn(minimum, maximum)
					val finalText = if (allowDecimal) clampedValue.toString() else clampedValue.toInt().toString()
					if (text != finalText) {
						text = finalText
					}
				} else if (text != "-" || text.isEmpty()) {
					// Se il testo non è un numero valido e non è solo il segno negativo, usa il default
					val defaultText = if (allowDecimal) defaultValue.toDouble().toString() else defaultValue.toInt().toString()
					text = defaultText
				}
			}
		}
	}

}
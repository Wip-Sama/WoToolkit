package org.wip.womtoolkit.utils

import org.wip.womtoolkit.Assets

object cssReader {
	fun getValueFromCssFile(cssPath: String, property: String): String? {
		val regex = Regex("-$property:\\s*([^;]+);")
		javaClass.getResourceAsStream(cssPath)
		val css = javaClass.getResourceAsStream(cssPath)?.reader(Charsets.UTF_8).use { it?.readText() ?: "" }
		println(css)
		return regex.find(css)?.groupValues?.get(1)?.trim()
	}
}
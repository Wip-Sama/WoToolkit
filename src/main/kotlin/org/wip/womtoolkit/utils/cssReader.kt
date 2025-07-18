package org.wip.womtoolkit.utils

import java.awt.Color
import java.awt.Color.HSBtoRGB
import kotlin.and
import kotlin.shr
import kotlin.text.get

//TODO: Document this class
object cssReader {
	val knownColors = mapOf(
		"aliceblue" to Color.decode("#f0f8ff"),
		"antiquewhite" to Color.decode("#faebd7"),
		"aqua" to Color.decode("#00ffff"),
		"aquamarine" to Color.decode("#7fffd4"),
		"azure" to Color.decode("#f0ffff"),
		"beige" to Color.decode("#f5f5dc"),
		"bisque" to Color.decode("#ffe4c4"),
		"black" to Color.decode("#000000"),
		"blanchedalmond" to Color.decode("#ffebcd"),
		"blue" to Color.decode("#0000ff"),
		"blueviolet" to Color.decode("#8a2be2"),
		"brown" to Color.decode("#a52a2a"),
		"burlywood" to Color.decode("#deb887"),
		"cadetblue" to Color.decode("#5f9ea0"),
		"chartreuse" to Color.decode("#7fff00"),
		"chocolate" to Color.decode("#d2691e"),
		"coral" to Color.decode("#ff7f50"),
		"cornflowerblue" to Color.decode("#6495ed"),
		"cornsilk" to Color.decode("#fff8dc"),
		"crimson" to Color.decode("#dc143c"),
		"cyan" to Color.decode("#00ffff"),
		"darkblue" to Color.decode("#00008b"),
		"darkcyan" to Color.decode("#008b8b"),
		"darkgoldenrod" to Color.decode("#b8860b"),
		"darkgray" to Color.decode("#a9a9a9"),
		"darkgreen" to Color.decode("#006400"),
		"darkgrey" to Color.decode("#a9a9a9"),
		"darkkhaki" to Color.decode("#bdb76b"),
		"darkmagenta" to Color.decode("#8b008b"),
		"darkolivegreen" to Color.decode("#556b2f"),
		"darkorange" to Color.decode("#ff8c00"),
		"darkorchid" to Color.decode("#9932cc"),
		"darkred" to Color.decode("#8b0000"),
		"darksalmon" to Color.decode("#e9967a"),
		"darkseagreen" to Color.decode("#8fbc8f"),
		"darkslateblue" to Color.decode("#483d8b"),
		"darkslategray" to Color.decode("#2f4f4f"),
		"darkslategrey" to Color.decode("#2f4f4f"),
		"darkturquoise" to Color.decode("#00ced1"),
		"darkviolet" to Color.decode("#9400d3"),
		"deeppink" to Color.decode("#ff1493"),
		"deepskyblue" to Color.decode("#00bfff"),
		"dimgray" to Color.decode("#696969"),
		"dimgrey" to Color.decode("#696969"),
		"dodgerblue" to Color.decode("#1e90ff"),
		"firebrick" to Color.decode("#b22222"),
		"floralwhite" to Color.decode("#fffaf0"),
		"forestgreen" to Color.decode("#228b22"),
		"fuchsia" to Color.decode("#ff00ff"),
		"gainsboro" to Color.decode("#dcdcdc"),
		"ghostwhite" to Color.decode("#f8f8ff"),
		"gold" to Color.decode("#ffd700"),
		"goldenrod" to Color.decode("#daa520"),
		"gray" to Color.decode("#808080"),
		"green" to Color.decode("#008000"),
		"greenyellow" to Color.decode("#adff2f"),
		"grey" to Color.decode("#808080"),
		"honeydew" to Color.decode("#f0fff0"),
		"hotpink" to Color.decode("#ff69b4"),
		"indianred" to Color.decode("#cd5c5c"),
		"indigo" to Color.decode("#4b0082"),
		"ivory" to Color.decode("#fffff0"),
		"khaki" to Color.decode("#f0e68c"),
		"lavender" to Color.decode("#e6e6fa"),
		"lavenderblush" to Color.decode("#fff0f5"),
		"lawngreen" to Color.decode("#7cfc00"),
		"lemonchiffon" to Color.decode("#fffacd"),
		"lightblue" to Color.decode("#add8e6"),
		"lightcoral" to Color.decode("#f08080"),
		"lightcyan" to Color.decode("#e0ffff"),
		"lightgoldenrodyellow" to Color.decode("#fafad2"),
		"lightgray" to Color.decode("#d3d3d3"),
		"lightgreen" to Color.decode("#90ee90"),
		"lightgrey" to Color.decode("#d3d3d3"),
		"lightpink" to Color.decode("#ffb6c1"),
		"lightsalmon" to Color.decode("#ffa07a"),
		"lightseagreen" to Color.decode("#20b2aa"),
		"lightskyblue" to Color.decode("#87cefa"),
		"lightslategray" to Color.decode("#778899"),
		"lightslategrey" to Color.decode("#778899"),
		"lightsteelblue" to Color.decode("#b0c4de"),
		"lightyellow" to Color.decode("#ffffe0"),
		"lime" to Color.decode("#00ff00"),
		"limegreen" to Color.decode("#32cd32"),
		"linen" to Color.decode("#faf0e6"),
		"magenta" to Color.decode("#ff00ff"),
		"maroon" to Color.decode("#800000"),
		"mediumaquamarine" to Color.decode("#66cdaa"),
		"mediumblue" to Color.decode("#0000cd"),
		"mediumorchid" to Color.decode("#ba55d3"),
		"mediumpurple" to Color.decode("#9370db"),
		"mediumseagreen" to Color.decode("#3cb371"),
		"mediumslateblue" to Color.decode("#7b68ee"),
		"mediumspringgreen" to Color.decode("#00fa9a"),
		"mediumturquoise" to Color.decode("#48d1cc"),
		"mediumvioletred" to Color.decode("#c71585"),
		"midnightblue" to Color.decode("#191970"),
		"mintcream" to Color.decode("#f5fffa"),
		"mistyrose" to Color.decode("#ffe4e1"),
		"moccasin" to Color.decode("#ffe4b5"),
		"navajowhite" to Color.decode("#ffdead"),
		"navy" to Color.decode("#000080"),
		"oldlace" to Color.decode("#fdf5e6"),
		"olive" to Color.decode("#808000"),
		"olivedrab" to Color.decode("#6b8e23"),
		"orange" to Color.decode("#ffa500"),
		"orangered" to Color.decode("#ff4500"),
		"orchid" to Color.decode("#da70d6"),
		"palegoldenrod" to Color.decode("#eee8aa"),
		"palegreen" to Color.decode("#98fb98"),
		"paleturquoise" to Color.decode("#afeeee"),
		"palevioletred" to Color.decode("#db7093"),
		"papayawhip" to Color.decode("#ffefd5"),
		"peachpuff" to Color.decode("#ffdab9"),
		"peru" to Color.decode("#cd853f"),
		"pink" to Color.decode("#ffc0cb"),
		"plum" to Color.decode("#dda0dd"),
		"powderblue" to Color.decode("#b0e0e6"),
		"purple" to Color.decode("#800080"),
		"red" to Color.decode("#ff0000"),
		"rosybrown" to Color.decode("#bc8f8f"),
		"royalblue" to Color.decode("#4169e1"),
		"saddlebrown" to Color.decode("#8b4513"),
		"salmon" to Color.decode("#fa8072"),
		"sandybrown" to Color.decode("#f4a460"),
		"seagreen" to Color.decode("#2e8b57"),
		"seashell" to Color.decode("#fff5ee"),
		"sienna" to Color.decode("#a0522d"),
		"silver" to Color.decode("#c0c0c0"),
		"skyblue" to Color.decode("#87ceeb"),
		"slateblue" to Color.decode("#6a5acd"),
		"slategray" to Color.decode("#708090"),
		"slategrey" to Color.decode("#708090"),
		"snow" to Color.decode("#fffafa"),
		"springgreen" to Color.decode("#00ff7f"),
		"steelblue" to Color.decode("#4682b4"),
		"tan" to Color.decode("#d2b48c"),
		"teal" to Color.decode("#008080"),
		"thistle" to Color.decode("#d8bfd8"),
		"tomato" to Color.decode("#ff6347"),
		"turquoise" to Color.decode("#40e0d0"),
		"violet" to Color.decode("#ee82ee"),
		"wheat" to Color.decode("#f5deb3"),
		"white" to Color.decode("#ffffff"),
		"whitesmoke" to Color.decode("#f5f5f5"),
		"yellow" to Color.decode("#ffff00"),
		"yellowgreen" to Color.decode("#9acd32"),
		"transparent" to Color(0, 0, 0, 0)
	)

	fun getValueFromCssFile(cssPath: String, property: String): String? {
		val regex = Regex("-$property:\\s*([^;]+);")
		val css = javaClass.getResourceAsStream(cssPath)?.reader(Charsets.UTF_8).use { it?.readText() ?: "" }
		return regex.find(css)?.groupValues?.get(1)?.trim()
	}

	fun getColorFromCssFile(cssPath: String, property: String): Color? {
		val value = getValueFromCssFile(cssPath, property) ?: return null
		return when {
			value.startsWith("rgb") -> {
				val rgb = value.replaceFirst(Regex("^\\s*rgba?\\("), "").removeSuffix(")").split(",")
				fun parseComponent(comp: String): Int {
					val c = comp.trim()
					return if (c.endsWith("%")) (c.removeSuffix("%").toFloat() * 2.55f).toInt() else c.toInt()
				}
				Color(
					parseComponent(rgb[0]),
					parseComponent(rgb[1]),
					parseComponent(rgb[2]),
					if (rgb.size > 3) (rgb[3].trim().toFloat() * 255).toInt() else 255
				)
			}
			value in knownColors -> knownColors[value]
			value.startsWith("hsb") -> {
				val hsb = value.replaceFirst(Regex("^\\s*hsba?\\("), "").removeSuffix(")").split(",")
				val h = hsb[0].trim().toFloat() / 360f
				val s = hsb[1].trim().removeSuffix("%").toFloat() / 100f
				val b = hsb[2].trim().removeSuffix("%").toFloat() / 100f
				val a = if (hsb.size > 3) (hsb[3].trim().toFloat() * 255).toInt() else 255
				val rgb = HSBtoRGB(h, s, b)
				Color(
					rgb shr 16 and 0xFF,
					rgb shr 8 and 0xFF,
					rgb and 0xFF,
					a
				)
			}
			else -> null
		}
	}

	fun getHexFromCssFile(cssPath: String, property: String): String? {
		val awtColor = getColorFromCssFile(cssPath, property) ?: return null
		return "#%02x%02x%02x%02x".format(awtColor.red, awtColor.green, awtColor.blue, awtColor.alpha)
	}
}
package org.wip.womtoolkit.utils

data class Version(
	val epoch: Int = 0,
	val major: Int = 0,
	val minor: Int = 0,
	val patch: Int = 0,
) {
	override fun toString(): String {
		return "$epoch.$major.$minor.$patch"
	}

	fun equals(other: Version): Boolean {
		return epoch == other.epoch && major == other.major && minor == other.minor && patch == other.patch
	}

	operator fun compareTo(other: Version): Int {
		if (epoch != other.epoch) return if (epoch > other.epoch) 1 else -1
		if (major != other.major) return if (major > other.major) 1 else -1
		if (minor != other.minor) return if (minor > other.minor) 1 else -1
		if (patch != other.patch) return if (patch > other.patch) 1 else -1
		return 0 // Versions are equal
	}

	companion object {
		fun fromString(versionString: String): Version {
			val parts = versionString.split(".")
			return Version(
				epoch = parts.getOrNull(0)?.toIntOrNull() ?: 0,
				major = parts.getOrNull(1)?.toIntOrNull() ?: 0,
				minor = parts.getOrNull(2)?.toIntOrNull() ?: 0,
				patch = parts.getOrNull(3)?.toIntOrNull() ?: 0
			)
		}
	}
}

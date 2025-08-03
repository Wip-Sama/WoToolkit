package org.wip.womtoolkit.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object FileUtiles {
	fun checkItsFileAndICanReadWrite(path: Path): Boolean {
		if (!Files.exists(path)) {
			return false
		}
		if (!Files.isRegularFile(path)) {
			return false
		}
		if (!Files.isReadable(path) || !Files.isWritable(path)) {
			return false
		}
		return true
	}

	fun checkItsDirectoryAndICanReadWrite(path: Path): Boolean {
		if (!Files.exists(path)) {
			return false
		}
		if (!Files.isDirectory(path)) {
			return false
		}
		if (!Files.isReadable(path) || !Files.isWritable(path)) {
			return false
		}
		return true
	}


	fun checkItsFileAndICanRead(path: Path): Boolean {
		if (!Files.exists(path)) {
			return false
		}
		if (!Files.isRegularFile(path)) {
			return false
		}
		if (!Files.isReadable(path)) {
			return false
		}
		return true
	}

	fun checkItsSomethingAndICanRead(path: Path): Boolean {
		if (!Files.exists(path)) {
			return false
		}
		if (!Files.isReadable(path)) {
			return false
		}
		return true
	}


}
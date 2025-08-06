package org.wip.womtoolkit.model.services.modulesManagment.moduleDTO

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.utils.CommandRunner
import org.wip.womtoolkit.utils.FileUtiles
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer
import kotlin.io.path.Path

@Serializable
data class ModuleValidation (
	@Serializable(with = MutableStateFlowSerializer::class) val type: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val hash: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val file: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val command: MutableStateFlow<String> = MutableStateFlow(""),
	@Serializable(with = MutableStateFlowSerializer::class) val expectedResult: MutableStateFlow<ModuleExpectedResult>? = null,
) {
	@Transient private val _isValidated: MutableStateFlow<Boolean> = MutableStateFlow(false)
	@Transient var isValidated: StateFlow<Boolean> = _isValidated

	@Transient private val _isValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
	@Transient val isValid: StateFlow<Boolean> = _isValid

	fun validate(): Boolean {
		_isValidated.value = true
		when (type.value) {
			"hash" -> {
				val filePath = Path("${System.getProperty("user.dir")}/${file.value}")
				if (!FileUtiles.checkItsFileAndICanRead(filePath)) {
					throw RuntimeException("File at $filePath does not exist or is not readable.")
				}
				// TODO: hash check
				_isValid.value = true
			}

			"command" -> {
				val newCommand = CommandRunner.replaceDependenciesInCommand(command.value)
				val output = CommandRunner.runSimpleCommand(newCommand)

				if (output.second.isNotEmpty()) {
					Globals.logger.warning("Command '${command.value}' failed with error: ${output.second}")
					return false
				}

				if (expectedResult?.value?.validateResult(output.first) == false) _isValid.value = false
				_isValid.value = true
			}
			else -> _isValid.value = false
		}
		return _isValid.value
	}
}
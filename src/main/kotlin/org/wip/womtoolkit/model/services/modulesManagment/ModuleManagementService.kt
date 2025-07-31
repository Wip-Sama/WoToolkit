package org.wip.womtoolkit.model.services.modulesManagment

import org.wip.womtoolkit.model.services.modulesManagment.moduleDTO.ModuleInfo

object ModuleManagementService {
	//Could be a MutableStateFlow
	private val _modules = mutableMapOf<String, ModuleInfo>()
	val modules: Map<String, ModuleInfo>
		get() = _modules

	fun addOrUpdateModule(module: ModuleInfo) {
		_modules[module.name.value] = module
	}
}
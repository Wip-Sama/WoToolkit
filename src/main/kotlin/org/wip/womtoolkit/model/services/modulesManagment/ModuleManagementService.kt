package org.wip.womtoolkit.model.services.modulesManagment

import org.wip.womtoolkit.model.services.modulesManagment.moduleDTO.ModuleInfo

object ModuleManagementService {
	private val _modules = mutableMapOf<String, ModuleInfo>()
	val modules: Map<String, ModuleInfo>
		get() = _modules


}
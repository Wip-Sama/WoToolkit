package org.wip.womtoolkit.model.services.modulesManagment

import org.wip.womtoolkit.model.services.activityMonitor.ActivityContainer

object ModuleManagementService {
	private val _modules = mutableMapOf<String, Module>()
	val modules: Map<String, Module>
		get() = _modules


}
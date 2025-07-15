package org.wip.womtoolkit.model.database.entities

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
class NotificationSettings {
	@Serializable(with = MutableStateFlowSerializer::class) val enabled: MutableStateFlow<Boolean> = MutableStateFlow(true)
	@Serializable(with = MutableStateFlowSerializer::class) val showInfo: MutableStateFlow<Boolean> = MutableStateFlow(true)
	@Serializable(with = MutableStateFlowSerializer::class) val showWarning: MutableStateFlow<Boolean> = MutableStateFlow(true)
	@Serializable(with = MutableStateFlowSerializer::class) val showError: MutableStateFlow<Boolean> = MutableStateFlow(true)
	@Serializable(with = MutableStateFlowSerializer::class) val showSuccess: MutableStateFlow<Boolean> = MutableStateFlow(true)
	@Serializable(with = MutableStateFlowSerializer::class) val autoDismiss: MutableStateFlow<Boolean> = MutableStateFlow(true)
	@Serializable(with = MutableStateFlowSerializer::class) val autoDismissTime: MutableStateFlow<Int> = MutableStateFlow(5)
}
package org.wip.womtoolkit.model.database.entities

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

@Serializable
data class SlicerSettings(
	@Serializable(with = MutableStateFlowSerializer::class)
	val minimumHeight: MutableStateFlow<Int> = MutableStateFlow(1000),
	@Serializable(with = MutableStateFlowSerializer::class)
	val desiredHeight: MutableStateFlow<Int> = MutableStateFlow(10000),
	@Serializable(with = MutableStateFlowSerializer::class)
	val maximumHeight: MutableStateFlow<Int> = MutableStateFlow(10000),

	@Serializable(with = MutableStateFlowSerializer::class)
	val saveInSubFolder: MutableStateFlow<Boolean> = MutableStateFlow(true),
	@Serializable(with = MutableStateFlowSerializer::class)
	val subFolderName: MutableStateFlow<String> = MutableStateFlow("sliced"),

	@Serializable(with = MutableStateFlowSerializer::class)
	val saveInArchive: MutableStateFlow<Boolean> = MutableStateFlow(true),
	@Serializable(with = MutableStateFlowSerializer::class)
	val archiveName: MutableStateFlow<String> = MutableStateFlow("sliced"),
	@Serializable(with = MutableStateFlowSerializer::class)
	val archiveFormat: MutableStateFlow<String> = MutableStateFlow("zip"),

	@Serializable(with = MutableStateFlowSerializer::class)
	val parallelExecution: MutableStateFlow<Boolean> = MutableStateFlow(false),

	@Serializable(with = MutableStateFlowSerializer::class)
	val outputFormat: MutableStateFlow<String> = MutableStateFlow("png"),

	@Serializable(with = MutableStateFlowSerializer::class)
	val cutTolerance: MutableStateFlow<Int> = MutableStateFlow(10),

	@Serializable(with = MutableStateFlowSerializer::class)
	val searchDirection: MutableStateFlow<Boolean> = MutableStateFlow(false), // false = to the minimum, true = to the maximum
)
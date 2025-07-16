package org.wip.womtoolkit.model.processing.slicer

import kotlinx.serialization.Serializable

@Serializable
data class SlicerDTO(
	@Serializable val images: MutableList<String>,
	@Serializable var minimumHeight: Int,
	@Serializable var desiredHeight: Int,
	@Serializable var maximumHeight: Int,
	@Serializable var cutTolerance: Int,
	@Serializable var searchDirection: Boolean, // false = to the minimum, true = to the maximum
)

package org.wip.womtoolkit.model.processing.slicer

import org.wip.womtoolkit.model.ApplicationData

data class SlicerSingleUseSettings(
	var minimumHeight: Int = ApplicationData.slicerSettings.minimumHeight.value,
	var desiredHeight: Int = ApplicationData.slicerSettings.desiredHeight.value,
	var maximumHeight: Int = ApplicationData.slicerSettings.maximumHeight.value,
	var searchDirection: Boolean = ApplicationData.slicerSettings.searchDirection.value,
	var saveInSubfolder: Boolean = ApplicationData.slicerSettings.saveInSubFolder.value,
	var subfolderName: String = ApplicationData.slicerSettings.subFolderName.value,
	var saveAsArchive: Boolean = ApplicationData.slicerSettings.saveInArchive.value,
	var archiveName: String = ApplicationData.slicerSettings.archiveName.value,
	var archiveFormat: String = ApplicationData.slicerSettings.archiveFormat.value,
	var parallelExecution: Boolean = ApplicationData.slicerSettings.parallelExecution.value,
	var outputFormat: String = ApplicationData.slicerSettings.outputFormat.value,
	var cutTolerance: Int = ApplicationData.slicerSettings.cutTolerance.value
)

package org.wip.womtoolkit.model.processing

import org.wip.womtoolkit.model.ApplicationSettings

data class SlicerSingleUseSettings(
	var minimumHeight: Int = ApplicationSettings.slicerSettings.minimumHeight.value,
	var desiredHeight: Int = ApplicationSettings.slicerSettings.desiredHeight.value,
	var maximumHeight: Int = ApplicationSettings.slicerSettings.maximumHeight.value,
	var searchDirection: Boolean = ApplicationSettings.slicerSettings.searchDirection.value,
	var saveInSubfolder: Boolean = ApplicationSettings.slicerSettings.saveInSubFolder.value,
	var subfolderName: String = ApplicationSettings.slicerSettings.subFolderName.value,
	var saveAsArchive: Boolean = ApplicationSettings.slicerSettings.saveInArchive.value,
	var archiveName: String = ApplicationSettings.slicerSettings.archiveName.value,
	var archiveFormat: String = ApplicationSettings.slicerSettings.archiveFormat.value,
	var parallelExecution: Boolean = ApplicationSettings.slicerSettings.parallelExecution.value,
	var outputFormat: String = ApplicationSettings.slicerSettings.outputFormat.value,
	var cutTolerance: Int = ApplicationSettings.slicerSettings.cutTolerance.value
)

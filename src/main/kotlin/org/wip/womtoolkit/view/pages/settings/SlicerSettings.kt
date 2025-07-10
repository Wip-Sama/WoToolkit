package org.wip.womtoolkit.view.pages.settings

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Separator
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.view.components.LabelWithLocalization
import org.wip.womtoolkit.view.components.NumberTextField
import org.wip.womtoolkit.view.components.SettingElement
import org.wip.womtoolkit.view.components.SingleSettingGrid
import org.wip.womtoolkit.view.components.Switch

class SlicerSettings : VBox() {

	@FXML lateinit var heightSetting: SettingElement
	@FXML lateinit var subFolderSetting: SettingElement
	@FXML lateinit var archiveSetting: SettingElement
	@FXML lateinit var parallelExecutionSetting: SettingElement
	@FXML lateinit var outputFormatSetting: SettingElement
	@FXML lateinit var cutToleranceSetting: SettingElement
	@FXML lateinit var searchDirectionSetting: SettingElement
	@FXML lateinit var lineEvaluationSetting: SettingElement

	init {
		FXMLLoader(javaClass.getResource("/view/pages/settings/slicerSettings.fxml")).apply {
			setRoot(this@SlicerSettings)
			setController(this@SlicerSettings)
			load()
		}
	}

	@FXML
	fun initialize() {
		heightSetting.apply {
			expandableContent = FXMLLoader(javaClass.getResource("/view/pages/settings/slicerExpansablePanes/height.fxml")).load()
			expandableContent.apply {
				val minimumHeightField = lookup("#minimumHeightField") as NumberTextField
				val desiredHeightField = lookup("#desiredHeightField") as NumberTextField
				val maximumHeightField = lookup("#maximumHeightField") as NumberTextField
				minimumHeightField.text = ApplicationSettings.slicerSettings.minimumHeight.value.toString()
				desiredHeightField.text = ApplicationSettings.slicerSettings.desiredHeight.value.toString()
				maximumHeightField.text = ApplicationSettings.slicerSettings.maximumHeight.value.toString()
				minimumHeightField.focusedProperty().addListener { _, _, newValue ->
					if (!newValue) {
						ApplicationSettings.slicerSettings.minimumHeight.value = minimumHeightField.value.toInt()
					}
				}
				desiredHeightField.focusedProperty().addListener { _, _, newValue ->
					if (!newValue) {
						ApplicationSettings.slicerSettings.desiredHeight.value = desiredHeightField.value.toInt()
					}
				}
				maximumHeightField.focusedProperty().addListener { _, _, newValue ->
					if (!newValue) {
						ApplicationSettings.slicerSettings.maximumHeight.value = maximumHeightField.value.toInt()
					}
				}
			}
		}
		subFolderSetting.apply {
			quickSetting = Switch(ApplicationSettings.slicerSettings.saveInSubFolder.value).apply {
				trueLocalization = "settingsPage.slicer.subFolder.enabled"
				falseLocalization = "settingsPage.slicer.subFolder.disabled"
				stateProperty.addListener { _, _, newValue ->
					ApplicationSettings.slicerSettings.saveInSubFolder.value = newValue
				}
			}
			expandableContent = SingleSettingGrid().apply {
				add(LabelWithLocalization().apply {
					localizationKey = "settingsPage.slicer.subFolder.folderName"
				}, 0, 0)
				add(TextField().apply {
					text = ApplicationSettings.slicerSettings.subFolderName.value
					focusedProperty().addListener { _, _, newValue ->
						ApplicationSettings.slicerSettings.subFolderName.value = text
					}
					textFormatter = TextFormatter<String> { change ->
						if (change.controlNewText.isEmpty()) {
							null
						} else if (change.controlNewText.length > Globals.MAX_SUBFOLDER_NAME_LENGTH) {
							null
						} else if (!change.controlNewText.matches(Globals.FOLDER_REGEX)) {
							null
						} else {
							change
						}
					}
				}, 1, 0)
			}
		}
		archiveSetting.apply {
			quickSetting = Switch(ApplicationSettings.slicerSettings.saveInArchive.value).apply {
				trueLocalization = "settingsPage.slicer.archive.enabled"
				falseLocalization = "settingsPage.slicer.archive.disabled"
				stateProperty.addListener { _, _, newValue ->
					ApplicationSettings.slicerSettings.saveInArchive.value = newValue
				}
			}
			expandableContent = VBox().apply {
				spacing = 8.0
				children.addAll(
					SingleSettingGrid().apply {
						add(LabelWithLocalization().apply {
							localizationKey = "settingsPage.slicer.archive.archiveName"
						}, 0, 0)
						add(TextField().apply {
							text = ApplicationSettings.slicerSettings.archiveName.value
							focusedProperty().addListener { _, _, newValue ->
								ApplicationSettings.slicerSettings.archiveName.value = text
							}
							textFormatter = TextFormatter<String> { change ->
								if (change.controlNewText.isEmpty()) {
									null
								} else if (change.controlNewText.length > Globals.MAX_SUBFOLDER_NAME_LENGTH) {
									null
								} else if (!change.controlNewText.matches(Globals.FOLDER_REGEX)) {
									null
								} else {
									change
								}
							}
						}, 1, 0)
					},
					Separator(),
					SingleSettingGrid().apply {
						add(LabelWithLocalization().apply {
							localizationKey = "settingsPage.slicer.archive.archiveFormat"
						}, 0, 0)
						add(ChoiceBox<String>().apply {
							onMouseClicked = EventHandler {
								hide()
								Platform.runLater {
									show()
								}
							}
							value = ApplicationSettings.slicerSettings.archiveFormat.value
							valueProperty().addListener { _, _, newValue ->
								ApplicationSettings.slicerSettings.archiveFormat.value = newValue
							}
							items.addAll(Globals.ARCHIVE_OUTPUT_FORMATS)
						}, 1, 0)
					}
				)
			}
		}
		parallelExecutionSetting.apply {
			quickSetting = Switch(ApplicationSettings.slicerSettings.parallelExecution.value).apply {
				trueLocalization = "settingsPage.slicer.parallelExecution.enabled"
				falseLocalization = "settingsPage.slicer.parallelExecution.disabled"
				stateProperty.addListener { _, _, newValue ->
					ApplicationSettings.slicerSettings.parallelExecution.value = newValue
				}
			}
		}
		outputFormatSetting.apply {
			quickSetting = ChoiceBox<String>().apply {
				onMouseClicked = EventHandler {
					hide()
					Platform.runLater {
						show()
					}
				}
				value = ApplicationSettings.slicerSettings.outputFormat.value
				valueProperty().addListener { _, _, newValue ->
					ApplicationSettings.slicerSettings.outputFormat.value = newValue
				}
				items.addAll(Globals.IMAGE_OUTPUT_FORMATS)
			}
		}
		cutToleranceSetting.apply {
			quickSetting = NumberTextField().apply {
				prefWidth = 100.0
				minWidth = 100.0
				maxWidth = 100.0
				minimum = 0.0
				defaultValue = 20.0
				maximum = 255.0
				text = ApplicationSettings.slicerSettings.cutTolerance.value.toString()
				focusedProperty().addListener { _, _, newValue ->
					if (!newValue) {
						ApplicationSettings.slicerSettings.cutTolerance.value = value.toInt()
					}
				}
			}
		}
	}
}

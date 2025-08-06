package org.wip.womtoolkit.view.pages.settings

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.wip.womtoolkit.model.services.localization.Lsp
import org.wip.womtoolkit.model.services.modulesManagment.ModuleManagementService
import org.wip.womtoolkit.model.services.modulesManagment.moduleDTO.ModuleInfo
import org.wip.womtoolkit.view.components.SettingElement
import org.wip.womtoolkit.view.components.VersionDisplay

class ModuleSettings : VBox() {
	val scope = MainScope()

	init {
		FXMLLoader(javaClass.getResource("/view/pages/settings/moduleSettings.fxml")).apply {
			setRoot(this@ModuleSettings)
			setController(this@ModuleSettings)
			load()
		}
	}

	@FXML fun initialize() {
		scope.launch {
			for ((name, module) in ModuleManagementService.modules) {
				val settingElement = prepModuleCommands(module)
				with (Dispatchers.JavaFx) { children.add(settingElement) }

				children.filter { node -> (node is SettingElement && !node.getTitleLocalization().contains(name)) }.forEach { node ->
					node as SettingElement

				}
			}
		}
	}


	private fun prepModuleCommands(module: ModuleInfo): SettingElement {
		return SettingElement().apply {
			setTitleLocalization("utility.localizedNothing", SimpleStringProperty(module.name.value))
			setDescriptionLocalization("utility.localizedNothing", SimpleStringProperty(module.description.value))
			setContent(module.icon.value)

			fun updateQuickSetting() {
				(quickSetting as VersionDisplay).apply {
					label.text = module.version.value
					icon.content = when {
						!module.compatibleWithPlatform || !module.compatibleWithToolkitVersion -> VersionDisplay.INCOMPATIBLE
						module.isUpdateAvailable.value -> VersionDisplay.UPDATE
						module.isInstalled.value -> VersionDisplay.INSTALLED
						else -> VersionDisplay.INSTALL
					}
				}
			}
			fun updateExpandableContent() {
				val installOrUpdate = lookup("#installOrUpdate") as Button

				if (module.isInstalled.value && module.isUpdateAvailable.value) {
					installOrUpdate.textProperty().unbind()
					installOrUpdate.textProperty().bind(Lsp.lsb("modulesPage.common.button.update"))
				} else {
					installOrUpdate.textProperty().unbind()
					installOrUpdate.textProperty().bind(Lsp.lsb("modulesPage.common.button.install"))
					installOrUpdate.isDisable = module.isInstalled.value
				}
			}

			quickSetting = VersionDisplay().apply {
				label.text = module.version.value
				icon.content = when {
					!module.compatibleWithPlatform || !module.compatibleWithToolkitVersion -> VersionDisplay.INCOMPATIBLE
					module.isUpdateAvailable.value -> VersionDisplay.UPDATE
					module.isInstalled.value -> VersionDisplay.INSTALLED
					else -> VersionDisplay.INSTALL
				} }

			scope.launch {
				module.version.collect {
					updateQuickSetting()
					updateExpandableContent()
				}
			}
			scope.launch {
				module.isInstalled.collect {
					updateQuickSetting()
					updateExpandableContent()
				}
			}
			scope.launch {
				module.isUpdateAvailable.collect {
					updateQuickSetting()
					updateExpandableContent()
				}
			}
			scope.launch { module.isValidated.collect { updateQuickSetting() } }

			expandableContent = FXMLLoader(javaClass.getResource("/view/pages/settings/modulesExpandablePanes/module.fxml")).load()
			expandableContent.apply {
				val installOrUpdate = lookup("#installOrUpdate") as Button
				val revalidate = lookup("#revalidate") as Button
				val uninstall = lookup("#uninstall") as Button


				revalidate.textProperty().bind(Lsp.lsb("modulesPage.common.button.revalidate"))
				uninstall.textProperty().bind(Lsp.lsb("modulesPage.common.button.uninstall"))

				installOrUpdate.setOnAction {
					if (module.isInstalled.value && module.isUpdateAvailable.value) {
						installOrUpdate.textProperty().unbind()
						installOrUpdate.textProperty().bind(Lsp.lsb("modulesPage.common.button.update"))
						module.update()
					} else {
						installOrUpdate.textProperty().unbind()
						installOrUpdate.textProperty().bind(Lsp.lsb("modulesPage.common.button.install"))
						module.install()
					}
				}

				revalidate.setOnAction {
					module.validate()
				}

				uninstall.setOnAction {
					module.uninstall()
				}
			}
		}
	}
}
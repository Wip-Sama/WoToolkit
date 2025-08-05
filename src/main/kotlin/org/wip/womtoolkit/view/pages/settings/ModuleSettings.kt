package org.wip.womtoolkit.view.pages.settings

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
import org.wip.womtoolkit.view.components.SettingElement
import org.wip.womtoolkit.view.components.VersionDisplay

class ModuleSettings : VBox() {

	@FXML lateinit var slicerSetting: SettingElement

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
				with (Dispatchers.JavaFx) {
					children.add(SettingElement().apply {
						setTitleLocalization("utility.localizedNothing")
						setDescriptionLocalization("utility.localizedNothing")
						setContent(module.icon.value)
						quickSetting = VersionDisplay().apply {
							label.text = module.version.value
							icon.content = if (!module.compatibleWithPlatform || !module.compatibleWithToolkitVersion) {
								VersionDisplay.INCOMPATIBLE
							} else if (module.isUpdateAvailable) {
								VersionDisplay.UPDATE
							} else if (module.isInstalled) {
								VersionDisplay.INSTALLED
							} else {
								VersionDisplay.INSTALL
							}
						}
						expandableContent = FXMLLoader(javaClass.getResource("/view/pages/settings/modulesExpandablePanes/module.fxml")).load()
						expandableContent.apply {
							val installOrUpdate = lookup("#installOrUpdate") as Button
							val revalidate = lookup("#revalidate") as Button
							val uninstall = lookup("#uninstall") as Button

							if (module.isInstalled && module.isUpdateAvailable) {
								installOrUpdate.textProperty().bind(Lsp.lsb("modulesPage.common.button.update"))
							} else {
								installOrUpdate.textProperty().bind(Lsp.lsb("modulesPage.common.button.install"))
							}

							revalidate.textProperty().bind(Lsp.lsb("modulesPage.common.button.revalidate"))
							uninstall.textProperty().bind(Lsp.lsb("modulesPage.common.button.uninstall"))

							installOrUpdate.setOnAction {
								if (module.isInstalled && module.isUpdateAvailable) {
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
					})

				}

				children.filter { node -> (node is SettingElement && !node.getTitleLocalization().contains(name)) }.forEach { node ->
					node as SettingElement

				}
			}
		}
		slicerSetting.apply {
			quickSetting = VersionDisplay()
		}
	}
}
package org.wip.womtoolkit.view.pages

import javafx.scene.paint.Color
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.LocalizationService
import org.wip.womtoolkit.utils.cssReader

abstract class MainWindow {

	private fun updateStyles() {
	}

	private fun updateLocale() {
		LocalizationService.currentLocale = ApplicationSettings.userSettings.localization.value
	}
}
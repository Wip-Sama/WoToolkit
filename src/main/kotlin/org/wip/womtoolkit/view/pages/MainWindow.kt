package org.wip.womtoolkit.view.pages

import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.services.localization.LocalizationService

abstract class MainWindow {

	private fun updateStyles() {
	}

	private fun updateLocale() {
		LocalizationService.currentLocale = ApplicationSettings.userSettings.localization.value
	}
}
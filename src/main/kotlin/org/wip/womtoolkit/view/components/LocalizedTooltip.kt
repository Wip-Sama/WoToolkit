package org.wip.womtoolkit.view.components

import javafx.scene.control.Tooltip
import org.wip.womtoolkit.model.services.localization.Lsp

class LocalizedTooltip: Tooltip() {
	private var _localizationKey: String? = null
	var localizationKey: String?
		get() = _localizationKey
		set(value) {
			_localizationKey = value
			textProperty().bind(Lsp.lsb(value ?: ""))
		}
}
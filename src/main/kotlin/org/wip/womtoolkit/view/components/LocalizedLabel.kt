package org.wip.womtoolkit.view.components

import javafx.scene.control.Label
import org.wip.womtoolkit.model.Lsp

class LocalizedLabel: Label() {
	private var _localizationKey: String? = null
	var localizationKey: String?
		get() = _localizationKey
		set(value) {
			_localizationKey = value
			textProperty().bind(Lsp.lsb(value ?: ""))
		}
}
package org.wip.womtoolkit.components

import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane

/**
 * @author Wip
 * Exposes: image|svg / name/ description/ switch|choice|custom? / expandable
 */
class SettingElement : BorderPane() {
    init {
        try {
            FXMLLoader(javaClass.getResource("/components/settingElement.fxml")).apply {
                setRoot(this@SettingElement)
                setController(this@SettingElement)
                load()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
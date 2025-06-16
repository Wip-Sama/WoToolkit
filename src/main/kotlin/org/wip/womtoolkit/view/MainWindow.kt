package org.wip.womtoolkit.view

import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import org.wip.womtoolkit.utils.cssReader
import xss.it.nfx.NfxWindow
import java.net.URL
import java.util.ResourceBundle

open class MainWindow : NfxWindow(), Initializable {


	fun initialize() {}

	init {
	}

	fun start() {
		val fxmlLoader = FXMLLoader(javaClass.getResource("/pages/main.fxml"))
		scene = Scene(fxmlLoader.load<AnchorPane>())
		title = "WomToolkit"

		icons.add(Image(javaClass.getResource("/icons/icon.png")?.toExternalForm()))
		scene.stylesheets.add(
			javaClass.getResource("/styles/dark.css")!!.toExternalForm()
		)
		captionColor = Color.valueOf(
			cssReader.getValueFromCssFile(
				"/styles/dark.css",
				"womt-text-color-1"
			) ?: "#000000"
		)
		titleBarColor = Color.valueOf(
			cssReader.getValueFromCssFile(
				"/styles/dark.css",
				"womt-background-1"
			) ?: "#000000"
		)

		isResizable =
			true
		show()
	}

	/**
	 * Initializes the controller.
	 *
	 * @param url            The location used to resolve relative paths for the root object, or null if the location is not known.
	 * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
	 */
	override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
		TODO("Not yet implemented")
	}
}
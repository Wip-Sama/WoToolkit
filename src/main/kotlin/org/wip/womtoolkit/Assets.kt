package org.wip.womtoolkit

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import java.io.IOException
import java.io.InputStream
import java.net.URL

/**
 * @author XDSSWAR
 * Created on 04/17/2024
 */
object Assets {
	/**
	 * This method loads a URL for a given location.
	 *
	 * @param location The location of the resource to load.
	 * @return A URL object representing the resource's location.
	 */
	fun load(location: String): URL? {
		return Assets::class.java.getResource(location)
	}

	/**
	 * Retrieves an InputStream for a given resource location using the class loader.
	 *
	 * @param location The resource location.
	 * @return An InputStream for the specified resource.
	 */
	fun stream(location: String): InputStream? {
		return Assets::class.java.getResourceAsStream(location)
	}


	/**
	 * Loads an FXML file from the specified location and sets the controller.
	 *
	 * @param location   The location of the FXML file to load.
	 * @param controller The controller object to be set for the loaded FXML file.
	 * @return           The root node of the loaded FXML file as a Parent object.
	 * @throws java.io.IOException If an I/O error occurs during loading.
	 */
	@Throws(IOException::class)
	fun load(location: String, controller: Any?): Parent? {
		val loader = FXMLLoader(javaClass.getResource("/pages/main.fxml"))
		loader.setController(controller)
		return loader.load<Parent?>()
	}
}
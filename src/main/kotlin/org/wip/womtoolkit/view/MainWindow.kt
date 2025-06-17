package org.wip.womtoolkit.view

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.SVGPath
import javafx.stage.StageStyle
import org.wip.womtoolkit.components.collapsablesidebarmenu.CollapsableSidebarMenu
import org.wip.womtoolkit.model.LocalizationService
import org.wip.womtoolkit.utils.cssReader
import org.wip.womtoolkit.view.MainWindow.Constants.page_indicator
import xss.it.nfx.AbstractNfxUndecoratedWindow
import xss.it.nfx.HitSpot
import xss.it.nfx.WindowState
import java.net.URL
import java.util.*

open class MainWindow : AbstractNfxUndecoratedWindow(), Initializable {
	@FXML lateinit var closeButton: Button
	@FXML lateinit var maximizeButton: Button
	@FXML lateinit var minimizeButton: Button
	@FXML lateinit var sidebarMenu: CollapsableSidebarMenu
	@FXML lateinit var info: Label
	@FXML lateinit var contentPane: BorderPane
	@FXML lateinit var buttonContainer: HBox
	@FXML lateinit var mainPane: AnchorPane

	object Constants {
		val MINIMIZE: String = // chrome minimize (hardest)
			"M0.554306 1.1109C0.554306 0.932457 0.698965 0.787798 0.877407 0.787798H5.40082C5.57926 0.787798 5.72392 0.932457 5.72392 1.1109C5.72392 1.28934 5.57926 1.434 5.40082 1.434H0.877407C0.698965 1.434 0.554306 1.28934 0.554306 1.1109Z"
		val MAXIMIZE: String = // maximize
			"M5.75 3H18.25C19.7688 3 21 4.23122 21 5.75V18.25C21 19.7688 19.7688 21 18.25 21H5.75C4.23122 21 3 19.7688 3 18.25V5.75C3 4.23122 4.23122 3 5.75 3ZM5.75 4.5C5.05964 4.5 4.5 5.05964 4.5 5.75V18.25C4.5 18.9404 5.05964 19.5 5.75 19.5H18.25C18.9404 19.5 19.5 18.9404 19.5 18.25V5.75C19.5 5.05964 18.9404 4.5 18.25 4.5H5.75Z"
		val REDUCE: String = // square multiplez
			"M7.51758 5H6.00932C6.13697 3.32189 7.53905 2 9.24988 2H17.25C19.8733 2 22 4.12665 22 6.75V14.75C22 16.4608 20.6781 17.8629 19 17.9905V16.4823C19.8481 16.361 20.5 15.6316 20.5 14.75V6.75C20.5 4.95507 19.0449 3.5 17.25 3.5H9.24988C8.36825 3.5 7.63889 4.15193 7.51758 5ZM5.25003 6C3.45509 6 2 7.45507 2 9.25V18.75C2 20.5449 3.45509 22 5.25003 22H14.7501C16.5451 22 18.0002 20.5449 18.0002 18.75V9.25C18.0002 7.45507 16.5451 6 14.7501 6H5.25003ZM3.50001 9.25C3.50001 8.2835 4.28352 7.5 5.25003 7.5H14.7501C15.7166 7.5 16.5001 8.2835 16.5001 9.25V18.75C16.5001 19.7165 15.7166 20.5 14.7501 20.5H5.25003C4.28352 20.5 3.50001 19.7165 3.50001 18.75V9.25Z"
		val CLOSE: String = // dismiss
			"M4.39705 4.55379L4.46967 4.46967C4.73594 4.2034 5.1526 4.1792 5.44621 4.39705L5.53033 4.46967L12 10.939L18.4697 4.46967C18.7626 4.17678 19.2374 4.17678 19.5303 4.46967C19.8232 4.76256 19.8232 5.23744 19.5303 5.53033L13.061 12L19.5303 18.4697C19.7966 18.7359 19.8208 19.1526 19.6029 19.4462L19.5303 19.5303C19.2641 19.7966 18.8474 19.8208 18.5538 19.6029L18.4697 19.5303L12 13.061L5.53033 19.5303C5.23744 19.8232 4.76256 19.8232 4.46967 19.5303C4.17678 19.2374 4.17678 18.7626 4.46967 18.4697L10.939 12L4.46967 5.53033C4.2034 5.26406 4.1792 4.8474 4.39705 4.55379L4.46967 4.46967L4.39705 4.55379Z"
		val page_indicator: StringProperty = SimpleStringProperty()
	}

	init {
		val fxmlLoader = FXMLLoader(javaClass.getResource("/pages/main.fxml"))
		fxmlLoader.setController(this)
		scene = Scene(fxmlLoader.load<AnchorPane>())
		minWidth = 620.0
		minHeight = 420.0
		width = 620.0
		height = 420.0

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

		isResizable = true
		initStyle(StageStyle.UNIFIED)
		show()
	}

	/**
	 * Initializes the controller.
	 *
	 * @param url            The location used to resolve relative paths for the root object, or null if the location is not known.
	 * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
	 */
	override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
		handelState(windowState)

//		windowStateProperty().addListener(ChangeListener { obs: ObservableValue<out WindowState?>?, o: WindowState?, state: WindowState? -> }
		windowStateProperty().addListener(ChangeListener { _, _, state: WindowState? ->
			handelState(state)
		})

		sidebarMenu.selectedButtonProperty.addListener(ChangeListener { _, _, new ->
			if (new != null) {
				page_indicator.unbind()
				page_indicator.bind(LocalizationService.lsb("menu.${new.id}"))

				contentPane.center = when (new.id) {
					"slicer" -> Slicer().root
					"converter" -> Converter().root
					"settings" -> Settings().root
					else -> null
				}
			}
		})



		info.textProperty().bind(page_indicator)
	}

	private fun handelState(state: WindowState?) {
		if (maximizeButton.graphic is SVGPath) {
			val path = maximizeButton.graphic as SVGPath
			if (state == WindowState.MAXIMIZED) {
				path.content = Constants.REDUCE
			} else if (state == WindowState.NORMAL) {
				path.content = Constants.MAXIMIZE
			}
		}
	}

	override fun getHitSpots(): MutableList<HitSpot?> {
		val minimizeHitSpot = HitSpot.builder()
			.window(this)
			.control(minimizeButton)
			.minimize(true)
			.build()

		minimizeHitSpot.hoveredProperty()
			.addListener(ChangeListener { obs: ObservableValue<out Boolean?>?, o: Boolean?, hovered: Boolean? ->
				if (hovered == true) {
					minimizeHitSpot.control.styleClass.add("hit-btn-hover")
				} else {
					minimizeHitSpot.control.styleClass.remove("hit-btn-hover")
				}
			})

		val maximizeHitSpot = HitSpot.builder()
			.window(this)
			.control(maximizeButton)
			.maximize(true)
			.build()

		maximizeHitSpot.hoveredProperty()
			.addListener(ChangeListener { obs: ObservableValue<out Boolean?>?, o: Boolean?, hovered: Boolean? ->
				if (hovered == true) {
					maximizeHitSpot.control.styleClass.add("hit-btn-hover")
				} else {
					maximizeHitSpot.control.styleClass.remove("hit-btn-hover")
				}
			})

		val closeHitSpot = HitSpot.builder()
			.window(this)
			.control(closeButton)
			.close(true)
			.build()

		closeHitSpot.hoveredProperty()
			.addListener(ChangeListener { obs: ObservableValue<out Boolean?>?, o: Boolean?, hovered: Boolean? ->
				if (hovered == true) {
					closeHitSpot.control.styleClass.add("hit-close-hover")
				} else {
					closeHitSpot.control.styleClass.remove("hit-close-hover")
				}
			})

		return listOf<HitSpot?>(minimizeHitSpot, maximizeHitSpot, closeHitSpot).toMutableList()
	}

	override fun getTitleBarHeight(): Double {
		return 32.0
	}

	@FXML
	fun onCloseAction(event: ActionEvent) {
		close()
	}

	@FXML
	fun onMaximizeAction(event: ActionEvent) {
		windowState = if (windowState == WindowState.MAXIMIZED) {
			WindowState.NORMAL
		} else {
			WindowState.MAXIMIZED
		}
	}

	@FXML
	fun onMinimizeAction(event: ActionEvent) {
		windowState = WindowState.MINIMIZED
	}


}
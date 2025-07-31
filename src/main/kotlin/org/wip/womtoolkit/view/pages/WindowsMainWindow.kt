package org.wip.womtoolkit.view.pages

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import org.wip.womtoolkit.view.components.collapsablesidebarmenu.CollapsableSidebarMenu
import org.wip.womtoolkit.model.services.localization.LocalizationService
import org.wip.womtoolkit.utils.cssReader
import xss.it.nfx.AbstractNfxUndecoratedWindow
import xss.it.nfx.HitSpot
import xss.it.nfx.WindowState
import java.net.URL
import java.util.*
import kotlinx.coroutines.MainScope
import org.wip.womtoolkit.model.ApplicationData
import org.wip.womtoolkit.view.components.CustomTitleBar

open class WindowsMainWindow : AbstractNfxUndecoratedWindow(), Initializable, MainWindowInterface {
	@FXML lateinit var sidebarMenu: CollapsableSidebarMenu
	@FXML lateinit var basePane: BorderPane
	@FXML lateinit var contentPane: BorderPane
	@FXML lateinit var buttonContainer: HBox

	private val scope = MainScope()
	private val customTitleBar = CustomTitleBar()

	init {
		/* Stage configuration */
		MainWindow.setStage(this)
		initStyle(StageStyle.UNIFIED)
		scene = Scene(FXMLLoader(javaClass.getResource("/view/pages/main.fxml")).apply {
			setController(this@WindowsMainWindow)
		}.load())
		MainWindow.loadSettings(scene, this)
		updateLocale()
		updateStyles()
		basePane.top = customTitleBar
		show()
	}

	override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
		MainWindow.initialize(sidebarMenu, contentPane, customTitleBar)

		sidebarMenu.selectedItemProperty.addListener(ChangeListener { _, _, new ->
			if (new != null) {
				new as Pane
				customTitleBar.page_indicator.unbind()
				customTitleBar.page_indicator.bind(LocalizationService.lsb(new.localizationKey))
			}
		})

		windowStateProperty().addListener(ChangeListener { _, _, state: WindowState? -> customTitleBar.handelState(state) })

		customTitleBar.closeButton.onAction = EventHandler { onCloseAction(it) }
		customTitleBar.maximizeButton.onAction = EventHandler { onMaximizeAction(it) }
		customTitleBar.minimizeButton.onAction = EventHandler { onMinimizeAction(it) }

		customTitleBar.handelState(windowState)
	}

	override fun updateStyles() {
		MainWindow._updateStyles(scene)

		captionColor = Color.valueOf(
			cssReader.getHexFromCssFile("/view/styles/${ApplicationData.userSettings.theme.value}.css", "womt-text") ?: "#000000"
		)
		titleBarColor = Color.valueOf(
			cssReader.getHexFromCssFile("/view/styles/${ApplicationData.userSettings.theme.value}.css", "womt-background-dark") ?: "#000000"
		)
	}

	private fun updateLocale() {
		MainWindow._updateLocale()
	}

	/* Specific of this class */
	@FXML fun onCloseAction(event: ActionEvent) {
		close()
	}

	@FXML fun onMaximizeAction(event: ActionEvent) {
		windowState = if (windowState == WindowState.MAXIMIZED) {
			WindowState.NORMAL
		} else {
			WindowState.MAXIMIZED
		}
	}

	@FXML fun onMinimizeAction(event: ActionEvent) {
		windowState = WindowState.MINIMIZED
	}

	override fun getHitSpots(): MutableList<HitSpot?> {
		val minimizeHitSpot = HitSpot.builder()
			.window(this)
			.control(customTitleBar.minimizeButton)
			.minimize(true)
			.build().apply {
				hoveredProperty().addListener(ChangeListener { obs: ObservableValue<out Boolean?>?, o: Boolean?, hovered: Boolean? ->
					if (hovered == true) {
						control.styleClass.add("hit-btn-hover")
					} else {
						control.styleClass.remove("hit-btn-hover")
					}
				})
			}


		val maximizeHitSpot = HitSpot.builder()
			.window(this)
			.control(customTitleBar.maximizeButton)
			.maximize(true)
			.build().apply {
				hoveredProperty().addListener(ChangeListener { obs: ObservableValue<out Boolean?>?, o: Boolean?, hovered: Boolean? ->
					if (hovered == true) {
						control.styleClass.add("hit-btn-hover")
					} else {
						control.styleClass.remove("hit-btn-hover")
					}
				})
			}

		val closeHitSpot = HitSpot.builder()
			.window(this)
			.control(customTitleBar.closeButton)
			.close(true)
			.build().apply {
				hoveredProperty().addListener(ChangeListener { obs: ObservableValue<out Boolean?>?, o: Boolean?, hovered: Boolean? ->
					if (hovered == true) {
						control.styleClass.add("hit-close-hover")
					} else {
						control.styleClass.remove("hit-close-hover")
					}
				})
			}

		return listOf<HitSpot?>(minimizeHitSpot, maximizeHitSpot, closeHitSpot).toMutableList()
	}

	override fun getTitleBarHeight(): Double {
		return 32.0
	}
}
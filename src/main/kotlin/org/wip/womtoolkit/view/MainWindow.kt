package org.wip.womtoolkit.view

import javafx.application.Platform
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
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.SVGPath
import javafx.stage.StageStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.wip.womtoolkit.Globals
import org.wip.womtoolkit.components.PageIndicator
import org.wip.womtoolkit.components.collapsablesidebarmenu.CollapsableComponent
import org.wip.womtoolkit.components.collapsablesidebarmenu.CollapsableSidebarMenu
import org.wip.womtoolkit.model.LocalizationService
import org.wip.womtoolkit.utils.cssReader
import org.wip.womtoolkit.view.MainWindow.Constants.page_indicator
import xss.it.nfx.AbstractNfxUndecoratedWindow
import xss.it.nfx.HitSpot
import xss.it.nfx.WindowState
import java.net.URL
import java.util.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class MainWindow : AbstractNfxUndecoratedWindow(), Initializable {
	@FXML lateinit var closeButton: Button
	@FXML lateinit var maximizeButton: Button
	@FXML lateinit var minimizeButton: Button
	@FXML lateinit var sidebarMenu: CollapsableSidebarMenu
	@FXML lateinit var info: Label
	@FXML lateinit var pageIndicator: PageIndicator
	@FXML lateinit var contentPane: BorderPane
	@FXML lateinit var buttonContainer: HBox
	@FXML lateinit var mainPane: AnchorPane

	object Constants {
		const val MINIMIZE: String = // chrome minimize (hardest)
			"M0.554306 1.1109C0.554306 0.932457 0.698965 0.787798 0.877407 0.787798H5.40082C5.57926 0.787798 5.72392 0.932457 5.72392 1.1109C5.72392 1.28934 5.57926 1.434 5.40082 1.434H0.877407C0.698965 1.434 0.554306 1.28934 0.554306 1.1109Z"
		const val MAXIMIZE: String = // maximize
			"M5.75 3H18.25C19.7688 3 21 4.23122 21 5.75V18.25C21 19.7688 19.7688 21 18.25 21H5.75C4.23122 21 3 19.7688 3 18.25V5.75C3 4.23122 4.23122 3 5.75 3ZM5.75 4.5C5.05964 4.5 4.5 5.05964 4.5 5.75V18.25C4.5 18.9404 5.05964 19.5 5.75 19.5H18.25C18.9404 19.5 19.5 18.9404 19.5 18.25V5.75C19.5 5.05964 18.9404 4.5 18.25 4.5H5.75Z"
		const val REDUCE: String = // square multiplez
			"M7.51758 5H6.00932C6.13697 3.32189 7.53905 2 9.24988 2H17.25C19.8733 2 22 4.12665 22 6.75V14.75C22 16.4608 20.6781 17.8629 19 17.9905V16.4823C19.8481 16.361 20.5 15.6316 20.5 14.75V6.75C20.5 4.95507 19.0449 3.5 17.25 3.5H9.24988C8.36825 3.5 7.63889 4.15193 7.51758 5ZM5.25003 6C3.45509 6 2 7.45507 2 9.25V18.75C2 20.5449 3.45509 22 5.25003 22H14.7501C16.5451 22 18.0002 20.5449 18.0002 18.75V9.25C18.0002 7.45507 16.5451 6 14.7501 6H5.25003ZM3.50001 9.25C3.50001 8.2835 4.28352 7.5 5.25003 7.5H14.7501C15.7166 7.5 16.5001 8.2835 16.5001 9.25V18.75C16.5001 19.7165 15.7166 20.5 14.7501 20.5H5.25003C4.28352 20.5 3.50001 19.7165 3.50001 18.75V9.25Z"
		const val CLOSE: String = // dismiss
			"M4.39705 4.55379L4.46967 4.46967C4.73594 4.2034 5.1526 4.1792 5.44621 4.39705L5.53033 4.46967L12 10.939L18.4697 4.46967C18.7626 4.17678 19.2374 4.17678 19.5303 4.46967C19.8232 4.76256 19.8232 5.23744 19.5303 5.53033L13.061 12L19.5303 18.4697C19.7966 18.7359 19.8208 19.1526 19.6029 19.4462L19.5303 19.5303C19.2641 19.7966 18.8474 19.8208 18.5538 19.6029L18.4697 19.5303L12 13.061L5.53033 19.5303C5.23744 19.8232 4.76256 19.8232 4.46967 19.5303C4.17678 19.2374 4.17678 18.7626 4.46967 18.4697L10.939 12L4.46967 5.53033C4.2034 5.26406 4.1792 4.8474 4.39705 4.55379L4.46967 4.46967L4.39705 4.55379Z"

		const val SLICER: String =
			"M7.8294 2.43943C7.52083 1.98139 6.89937 1.86022 6.44132 2.16879C5.98328 2.47736 5.86211 3.09882 6.17068 3.55686L10.9634 10.6711L8.63676 14.3487C8.13715 14.1244 7.58315 13.9996 7 13.9996C4.79086 13.9996 3 15.7905 3 17.9996C3 20.2087 4.79086 21.9996 7 21.9996C9.20914 21.9996 11 20.2087 11 17.9996C11 17.1029 10.705 16.2752 10.2067 15.6081L12.1839 12.4828L14.0676 15.2791C13.4051 15.9929 13 16.9489 13 17.9996C13 20.2087 14.7909 21.9996 17 21.9996C19.2091 21.9996 21 20.2087 21 17.9996C21 15.7905 19.2091 13.9996 17 13.9996C16.5639 13.9996 16.1441 14.0694 15.7511 14.1984L12.1319 8.82411L12.1313 8.8251L7.8294 2.43943ZM5 17.9996C5 16.895 5.89543 15.9996 7 15.9996C8.10457 15.9996 9 16.895 9 17.9996C9 19.1042 8.10457 19.9996 7 19.9996C5.89543 19.9996 5 19.1042 5 17.9996ZM15 17.9996C15 16.895 15.8954 15.9996 17 15.9996C18.1046 15.9996 19 16.895 19 17.9996C19 19.1042 18.1046 19.9996 17 19.9996C15.8954 19.9996 15 19.1042 15 17.9996ZM14.5202 8.78988L17.8452 3.53421C18.1404 3.06749 18.0014 2.44977 17.5347 2.1545C17.068 1.85922 16.4503 1.99821 16.155 2.46494L13.2998 6.97801L14.5202 8.78988Z"
		const val CONVERTER: String =
			"M14.7123 2.28878L14.6251 2.21113C14.2326 1.90052 13.6607 1.92641 13.2981 2.28878L13.2204 2.37594C12.9096 2.76818 12.9355 3.33963 13.2981 3.702L14.597 4.99899L8.99921 4.99919L8.75859 5.00325C5.00445 5.12998 2 8.21112 2 11.9935C2 13.4382 2.43833 14.7806 3.18863 15.8918C3.37024 16.1432 3.666 16.3068 4 16.3068C4.55228 16.3068 5 15.8594 5 15.3075C5 15.0914 4.93132 14.8912 4.81525 14.7288L4.68008 14.5107C4.24775 13.7716 4 12.9114 4 11.9935C4 9.23444 6.23822 6.99779 8.99921 6.99779L14.595 6.99758L13.2981 8.29497L13.2204 8.38213C12.9096 8.77438 12.9355 9.34582 13.2981 9.7082C13.6886 10.0984 14.3218 10.0984 14.7123 9.7082L17.7175 6.7051L17.7952 6.61794C18.106 6.2257 18.0801 5.65425 17.7175 5.29188L14.7123 2.28878ZM20.7865 8.06013C20.6034 7.82751 20.3191 7.67811 20 7.67811C19.4477 7.67811 19 8.12551 19 8.67741C19 8.88559 19.0637 9.0789 19.1717 9.23841C19.6952 10.0282 20 10.9753 20 11.9935C20 14.7525 17.7618 16.9892 15.0008 16.9892L9.415 16.9886L10.7087 15.6972L10.7923 15.6025C11.0733 15.2408 11.0713 14.7307 10.7864 14.3712L10.7087 14.284L10.6139 14.2004C10.252 13.9196 9.7415 13.9216 9.38169 14.2063L9.29447 14.284L6.28926 17.2871L6.20562 17.3818C5.92465 17.7435 5.92663 18.2536 6.21156 18.6132L6.28926 18.7003L9.29447 21.7034L9.38867 21.7865C9.78097 22.0913 10.3482 22.0636 10.7087 21.7034C11.0713 21.341 11.0972 20.7696 10.7864 20.3773L10.7087 20.2902L9.405 18.9872L15.0008 18.9878L15.2414 18.9837C18.9956 18.857 22 15.7759 22 11.9935C22 10.5336 21.5524 9.17809 20.7868 8.05666L20.7865 8.06013Z"
		const val SETTINGS: String =
			"M12.0122 2.25C12.7462 2.25846 13.4773 2.34326 14.1937 2.50304C14.5064 2.57279 14.7403 2.83351 14.7758 3.15196L14.946 4.67881C15.0231 5.37986 15.615 5.91084 16.3206 5.91158C16.5103 5.91188 16.6979 5.87238 16.8732 5.79483L18.2738 5.17956C18.5651 5.05159 18.9055 5.12136 19.1229 5.35362C20.1351 6.43464 20.8889 7.73115 21.3277 9.14558C21.4223 9.45058 21.3134 9.78203 21.0564 9.9715L19.8149 10.8866C19.4607 11.1468 19.2516 11.56 19.2516 11.9995C19.2516 12.4389 19.4607 12.8521 19.8157 13.1129L21.0582 14.0283C21.3153 14.2177 21.4243 14.5492 21.3297 14.8543C20.8911 16.2685 20.1377 17.5649 19.1261 18.6461C18.9089 18.8783 18.5688 18.9483 18.2775 18.8206L16.8712 18.2045C16.4688 18.0284 16.0068 18.0542 15.6265 18.274C15.2463 18.4937 14.9933 18.8812 14.945 19.3177L14.7759 20.8444C14.741 21.1592 14.5122 21.4182 14.204 21.4915C12.7556 21.8361 11.2465 21.8361 9.79803 21.4915C9.48991 21.4182 9.26105 21.1592 9.22618 20.8444L9.05736 19.32C9.00777 18.8843 8.75434 18.498 8.37442 18.279C7.99451 18.06 7.5332 18.0343 7.1322 18.2094L5.72557 18.8256C5.43422 18.9533 5.09403 18.8833 4.87678 18.6509C3.86462 17.5685 3.11119 16.2705 2.6732 14.8548C2.57886 14.5499 2.68786 14.2186 2.94485 14.0293L4.18818 13.1133C4.54232 12.8531 4.75147 12.4399 4.75147 12.0005C4.75147 11.561 4.54232 11.1478 4.18771 10.8873L2.94516 9.97285C2.6878 9.78345 2.5787 9.45178 2.67337 9.14658C3.11212 7.73215 3.86594 6.43564 4.87813 5.35462C5.09559 5.12236 5.43594 5.05259 5.72724 5.18056L7.12762 5.79572C7.53056 5.97256 7.9938 5.94585 8.37577 5.72269C8.75609 5.50209 9.00929 5.11422 9.05817 4.67764L9.22824 3.15196C9.26376 2.83335 9.49786 2.57254 9.8108 2.50294C10.5281 2.34342 11.26 2.25865 12.0122 2.25ZM12.0124 3.7499C11.5583 3.75524 11.1056 3.79443 10.6578 3.86702L10.5489 4.84418C10.4471 5.75368 9.92003 6.56102 9.13042 7.01903C8.33597 7.48317 7.36736 7.53903 6.52458 7.16917L5.62629 6.77456C5.05436 7.46873 4.59914 8.25135 4.27852 9.09168L5.07632 9.67879C5.81513 10.2216 6.25147 11.0837 6.25147 12.0005C6.25147 12.9172 5.81513 13.7793 5.0771 14.3215L4.27805 14.9102C4.59839 15.752 5.05368 16.5361 5.626 17.2316L6.53113 16.8351C7.36923 16.4692 8.33124 16.5227 9.12353 16.9794C9.91581 17.4361 10.4443 18.2417 10.548 19.1526L10.657 20.1365C11.5466 20.2878 12.4555 20.2878 13.3451 20.1365L13.4541 19.1527C13.5549 18.2421 14.0828 17.4337 14.876 16.9753C15.6692 16.5168 16.6332 16.463 17.4728 16.8305L18.3772 17.2267C18.949 16.5323 19.4041 15.7495 19.7247 14.909L18.9267 14.3211C18.1879 13.7783 17.7516 12.9162 17.7516 11.9995C17.7516 11.0827 18.1879 10.2206 18.9258 9.67847L19.7227 9.09109C19.4021 8.25061 18.9468 7.46784 18.3748 6.77356L17.4783 7.16737C17.113 7.32901 16.7178 7.4122 16.3187 7.41158C14.849 7.41004 13.6155 6.30355 13.4551 4.84383L13.3462 3.8667C12.9007 3.7942 12.4526 3.75512 12.0124 3.7499ZM11.9997 8.24995C14.0708 8.24995 15.7497 9.92888 15.7497 12C15.7497 14.071 14.0708 15.75 11.9997 15.75C9.92863 15.75 8.2497 14.071 8.2497 12C8.2497 9.92888 9.92863 8.24995 11.9997 8.24995ZM11.9997 9.74995C10.7571 9.74995 9.7497 10.7573 9.7497 12C9.7497 13.2426 10.7571 14.25 11.9997 14.25C13.2423 14.25 14.2497 13.2426 14.2497 12C14.2497 10.7573 13.2423 9.74995 11.9997 9.74995Z"

		val page_indicator: StringProperty = SimpleStringProperty()
	}

	private val scope = MainScope()

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

//		Platform.runLater {
//			scope.launch(Dispatchers.JavaFx) {
//				Globals.themeFlow.collectLatest { newTheme ->
//					updateStyles()
//				}
//			}
//		}

		scope.launch {
			Globals.themeFlow.collectLatest { newTheme ->
				withContext(Dispatchers.JavaFx) {
					updateStyles()
				}
			}
		}

		scope.launch {
			Globals.accentFlow.collectLatest { newAccent ->
				withContext(Dispatchers.JavaFx) {
					updateStyles()
				}
			}
		}

		updateStyles()

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

		sidebarMenu.selectedItemProperty.addListener(ChangeListener { _, _, new ->
			if (new != null) {
				val n: Pane = new as Pane
				page_indicator.unbind()
				page_indicator.bind(LocalizationService.lsb(new.localizationKey))

				contentPane.center = when (n.id) {
					"slicer" -> Slicer()
					"converter" -> Converter()
					"settings" -> Settings(pageIndicator)
					else -> null
				}
			}
		})

		info.textProperty().bind(page_indicator)

		sidebarMenu.apply {
			addComponent(CollapsableComponent().apply {
				id = "slicer"
				localizationKey = "menu.slicer"
				icon.content = Constants.SLICER
			}, CollapsableSidebarMenu.Positions.TOP)

			addComponent(CollapsableComponent().apply {
				id = "converter"
				localizationKey = "menu.converter"
				icon.content = Constants.CONVERTER
			}, CollapsableSidebarMenu.Positions.TOP)

			addComponent(CollapsableComponent().apply {
				id = "settings"
				localizationKey = "menu.settings"
				icon.content = Constants.SETTINGS
			}, CollapsableSidebarMenu.Positions.BOTTOM)
		}

		pageIndicator.addLabel(
			info
		)
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

	private fun updateStyles() {
		scene.stylesheets.clear()
		val cssUrl = javaClass.getResource("/styles/${Globals.theme}.css")
		if (cssUrl != null) {
			scene.stylesheets.add(cssUrl.toExternalForm())
		} else {
			println("File CSS non trovato: /styles/${Globals.theme}.css")
		}

		captionColor = Color.valueOf(
			cssReader.getValueFromCssFile("/styles/${Globals.theme}.css", "womt-text-color-1") ?: "#000000"
		)
		titleBarColor = Color.valueOf(
			cssReader.getValueFromCssFile("/styles/${Globals.theme}.css", "womt-background-1") ?: "#000000"
		)

		val accentColor = Globals.accent.toString().replace("0x", "#")
		scene.root.style = "-womt-accent: $accentColor;"
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
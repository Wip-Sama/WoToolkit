package org.wip.womtoolkit.view.settings

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ChoiceBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.shape.SVGPath
import org.wip.womtoolkit.components.SettingElement
import org.wip.womtoolkit.components.Switch
import org.wip.womtoolkit.model.LocalizationService
import org.wip.womtoolkit.model.Lsp
import kotlin.properties.Delegates

/**
 * @author Wip
 * This class represents the general settings view in the application.
 * It contains: Accent color, Theme color, Language, Starting page.
 * */
class GeneralSettings : VBox() {
	@FXML lateinit var accentSetting: SettingElement
	@FXML lateinit var themeSetting: SettingElement
	@FXML lateinit var localizationSetting: SettingElement
	@FXML lateinit var startingPageSetting: SettingElement

	object Constants {
		const val LOCALIZATION: String =
			"M9.56258 7.50543L9.61862 7.62215L14.9259 20.6267C15.1346 21.1381 14.8892 21.7217 14.3779 21.9304C13.903 22.1242 13.3658 21.9265 13.1244 21.4879L13.0741 21.3824L11.693 17.999H5.40701L3.91608 21.4056C3.71043 21.8754 3.18729 22.1078 2.70853 21.9612L2.599 21.9206C2.1292 21.715 1.89681 21.1918 2.04333 20.7131L2.08394 20.6036L7.77668 7.59899C8.11338 6.82981 9.1713 6.80444 9.56258 7.50543ZM19 2C19.5128 2 19.9355 2.38604 19.9933 2.88338L20 3L19.9998 7H21C21.5128 7 21.9355 7.38604 21.9933 7.88338L22 8C22 8.51284 21.614 8.93551 21.1166 8.99327L21 9H19.9998L20 16C20 16.5128 19.614 16.9355 19.1166 16.9933L19 17C18.4872 17 18.0645 16.614 18.0067 16.1166L18 16V3C18 2.44772 18.4477 2 19 2ZM8.66047 10.5674L6.28201 15.999H10.877L8.66047 10.5674ZM11 2H16C16.5128 2 16.9355 2.38604 16.9933 2.88338L17 3V5.97488C17 8.18401 15.2091 9.97488 13 9.97488C12.4477 9.97488 12 9.52716 12 8.97488C12 8.42259 12.4477 7.97488 13 7.97488C14.0544 7.97488 14.9182 7.159 14.9945 6.12414L15 5.97488V4H11C10.4477 4 10 3.55228 10 3C10 2.48716 10.386 2.06449 10.8834 2.00673L11 2H16H11Z"
		const val THEME: String =
			"M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22ZM12 20V4C16.4183 4 20 7.58172 20 12C20 16.4183 16.4183 20 12 20Z"
		const val ACCENT: String =
			"M3.83885 5.85764C6.77986 1.94203 12.8685 0.802644 17.2028 3.49752C21.4826 6.15853 23.0566 11.2746 21.3037 16.0749C19.6485 20.6075 15.2873 22.4033 12.144 20.1233C10.9666 19.2692 10.5101 18.1985 10.2895 16.4595L10.1841 15.4715L10.1387 15.0741C10.016 14.14 9.82762 13.7216 9.43435 13.5024C8.89876 13.2038 8.54213 13.1969 7.83887 13.4694L7.48775 13.615L7.30902 13.693C6.29524 14.1332 5.62085 14.2879 4.76786 14.1092L4.56761 14.062L4.40407 14.0154C1.61511 13.1512 1.20202 9.36827 3.83885 5.85764ZM16.7669 10.5797C16.9456 11.2465 17.631 11.6423 18.2978 11.4636C18.9646 11.2849 19.3604 10.5995 19.1817 9.93267C19.003 9.26583 18.3176 8.87011 17.6508 9.04878C16.9839 9.22746 16.5882 9.91288 16.7669 10.5797ZM17.2615 14.0684C17.4402 14.7352 18.1256 15.1309 18.7924 14.9523C19.4592 14.7736 19.855 14.0882 19.6763 13.4213C19.4976 12.7545 18.8122 12.3588 18.1454 12.5374C17.4785 12.7161 17.0828 13.4015 17.2615 14.0684ZM14.7884 7.57703C14.9671 8.24386 15.6525 8.63959 16.3193 8.46091C16.9861 8.28224 17.3819 7.59681 17.2032 6.92998C17.0245 6.26315 16.3391 5.86742 15.6723 6.0461C15.0054 6.22478 14.6097 6.9102 14.7884 7.57703ZM14.7599 16.5754C14.9386 17.2422 15.624 17.638 16.2908 17.4593C16.9577 17.2806 17.3534 16.5952 17.1747 15.9284C16.996 15.2615 16.3106 14.8658 15.6438 15.0445C14.9769 15.2232 14.5812 15.9086 14.7599 16.5754ZM11.263 6.60544C11.4416 7.27227 12.1271 7.668 12.7939 7.48932C13.4607 7.31064 13.8565 6.62522 13.6778 5.95839C13.4991 5.29156 12.8137 4.89583 12.1469 5.07451C11.48 5.25318 11.0843 5.9386 11.263 6.60544Z"
		const val STARTING_PAGE: String =
			"M13.4508 2.53318C12.6128 1.82618 11.3872 1.82618 10.5492 2.53318L3.79916 8.22772C3.29241 8.65523 3 9.28447 3 9.94747V19.2526C3 20.2191 3.7835 21.0026 4.75 21.0026H7.75C8.7165 21.0026 9.5 20.2191 9.5 19.2526V15.25C9.5 14.5707 10.0418 14.018 10.7169 14.0004H13.2831C13.9582 14.018 14.5 14.5707 14.5 15.25V19.2526C14.5 20.2191 15.2835 21.0026 16.25 21.0026H19.25C20.2165 21.0026 21 20.2191 21 19.2526V9.94747C21 9.28447 20.7076 8.65523 20.2008 8.22772L13.4508 2.53318Z"
	}

	init {
		FXMLLoader(javaClass.getResource("/pages/settings/generalSettings.fxml")).apply {
			setRoot(this@GeneralSettings)
			setController(this@GeneralSettings)
			load()
		}
	}

	@FXML
	fun initialize() {
		accentSetting.title.textProperty().bind(Lsp.lsb("settingsPage.general.accent.title"))
		accentSetting.description.textProperty().bind(Lsp.lsb("settingsPage.general.accent.description"))
		themeSetting.title.textProperty().bind(Lsp.lsb("settingsPage.general.theme.title"))
		themeSetting.description.textProperty().bind(Lsp.lsb("settingsPage.general.theme.description"))
		localizationSetting.title.textProperty().bind(Lsp.lsb("settingsPage.general.language.title"))
		localizationSetting.description.textProperty().bind(Lsp.lsb("settingsPage.general.language.description"))
		startingPageSetting.title.textProperty().bind(Lsp.lsb("settingsPage.general.startingPage.title"))
		startingPageSetting.description.textProperty().bind(Lsp.lsb("settingsPage.general.startingPage.description"))

		//Add buttons and expandable content to settings panels
		accentSetting.expandableContent = Pane().apply {
			styleClass.add("-fx-background-color: #ff0000;")
			prefHeight = 100.0
		}
		accentSetting.imageContainer.center = SVGPath().apply {
			content = Constants.ACCENT
		}
		themeSetting.quickSetting = Switch(true).apply {
			stateProperty.addListener { observable, oldValue, newValue ->
				textStateIndicator.textProperty().unbind()
				textStateIndicator.textProperty().bind(
					if (newValue) {
						Lsp.lsb("settingsPage.general.theme.dark")
					} else {
						Lsp.lsb("settingsPage.general.theme.light")
					}
				)
			}
			textStateIndicator.textProperty().unbind()
			textStateIndicator.textProperty().bind(
				if (state) {
					Lsp.lsb("settingsPage.general.theme.dark")
				} else {
					Lsp.lsb("settingsPage.general.theme.light")
				}
			)
		}
		themeSetting.imageContainer.center = SVGPath().apply {
			content = Constants.THEME
		}
		localizationSetting.quickSetting = ChoiceBox<String>().apply {
			items.addAll(LocalizationService.locales)
			value = LocalizationService.currentLocale
			onMouseClicked = EventHandler {
				hide()
				Platform.runLater {
					show()
				}
			}
			valueProperty().addListener { _, _, newValue ->
				LocalizationService.currentLocale = newValue ?: LocalizationService.currentLocale
			}
		}

		localizationSetting.imageContainer.center = SVGPath().apply {
			content = Constants.LOCALIZATION
		}
		startingPageSetting.quickSetting = ChoiceBox<String>().apply {
			items.addAll("None", "Slicer", "Converter")
			value = "None"
			onMouseClicked = EventHandler {
				hide()
				Platform.runLater {
					show()
				}
			}
			valueProperty().addListener { _, _, newValue ->
				println("Starting page changed to: $newValue")
			}
		}
		startingPageSetting.imageContainer.center = SVGPath().apply {
			content = Constants.STARTING_PAGE
		}
	}
}
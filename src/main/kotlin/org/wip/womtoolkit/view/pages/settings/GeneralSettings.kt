package org.wip.womtoolkit.view.pages.settings

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.shape.SVGPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.wip.womtoolkit.view.components.SettingElement
import org.wip.womtoolkit.view.components.Switch
import org.wip.womtoolkit.view.components.colorpicker.ColorPickerButton
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.LocalizationService
import org.wip.womtoolkit.model.Lsp

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
		const val COLOR_PICKER_MODE: String =
			"M21.0303 2.96997C19.6329 1.57251 17.3671 1.57251 15.9697 2.96997L14 4.93966L13.9874 4.9271C13.304 4.24368 12.196 4.24368 11.5125 4.92709L10.9268 5.51288C10.2433 6.1963 10.2433 7.30434 10.9268 7.98776L10.9394 8.00039L4.15914 14.7806C3.73719 15.2026 3.50013 15.7749 3.50013 16.3716V17.0591L2.21981 19.4063C1.38379 20.939 3.06141 22.6167 4.59412 21.7806L6.94138 20.5003H7.62881C8.22555 20.5003 8.79785 20.2633 9.2198 19.8413L16.0001 13.0611L16.0125 13.0735C16.696 13.757 17.804 13.757 18.4874 13.0735L19.0732 12.4878C19.7566 11.8043 19.7566 10.6963 19.0732 10.0129L19.0606 10.0003L21.0303 8.03063C22.4278 6.63317 22.4278 4.36744 21.0303 2.96997ZM12.0001 9.06105L14.9394 12.0004L8.15914 18.7806C8.01849 18.9213 7.82773 19.0003 7.62881 19.0003H6.75013C6.62465 19.0003 6.50116 19.0318 6.391 19.0919L3.87584 20.4638C3.80671 20.5015 3.75536 20.5032 3.717 20.4964C3.67227 20.4885 3.62232 20.4641 3.57935 20.4211C3.53638 20.3781 3.51196 20.3282 3.50405 20.2834C3.49726 20.2451 3.49895 20.1937 3.53665 20.1246L4.90856 17.6095C4.96865 17.4993 5.00014 17.3758 5.00014 17.2503V16.3716C5.00014 16.1727 5.07915 15.982 5.21981 15.8413L12.0001 9.06105Z"
	}

	private val scope = MainScope()

	init {
		FXMLLoader(javaClass.getResource("/view/pages/settings/generalSettings.fxml")).apply {
			setRoot(this@GeneralSettings)
			setController(this@GeneralSettings)
			load()
		}
	}

	@FXML
	fun initialize() {
		themeSetting.title.textProperty().bind(Lsp.lsb("settingsPage.general.theme.title"))
		themeSetting.description.textProperty().bind(Lsp.lsb("settingsPage.general.theme.description"))
		localizationSetting.title.textProperty().bind(Lsp.lsb("settingsPage.general.language.title"))
		localizationSetting.description.textProperty().bind(Lsp.lsb("settingsPage.general.language.description"))
		startingPageSetting.title.textProperty().bind(Lsp.lsb("settingsPage.general.startingPage.title"))
		startingPageSetting.description.textProperty().bind(Lsp.lsb("settingsPage.general.startingPage.description"))

		accentSetting.apply {
			title.textProperty().bind(Lsp.lsb("settingsPage.general.accent.title"))
			description.textProperty().bind(Lsp.lsb("settingsPage.general.accent.description"))
			quickSetting = ColorPickerButton(true).apply {
				colorProperty.value = ApplicationSettings.userSettings.accent
				colorProperty.addListener { _, _, color ->
					ApplicationSettings.userSettings.accent = color
				}
			}
			expandableContent = VBox().apply {
				spacing = 8.0
				children.addAll(
					Label().apply {
						textProperty().bind(themeSetting.title.textProperty())
					},
					HBox().apply {
						children.addAll(
							ColorPickerButton(),
							ColorPickerButton(),
							ColorPickerButton(),
							ColorPickerButton(),
							ColorPickerButton(),
						)
					},
					Separator().apply {
						styleClass.add("my-separator")
					},
					Label().apply {
						textProperty().bind(themeSetting.title.textProperty())
					},
					GridPane().apply {
						for (x in 0..4) {
							for (y in 0..3) {
//								val color = ApplicationSettings.userSettings.accentPalette[x][y]
								add(ColorPickerButton(false).apply {
//									colorProperty.value = color
//									setOnAction {
//										ApplicationSettings.userSettings.accentPalette[x][y] = colorProperty.value
//									}
								}, x, y)
							}
						}
					}
				)
			}
		}

		accentSetting.imageContainer.center = SVGPath().apply {
			content = Constants.ACCENT
		}

		themeSetting.quickSetting = Switch(ApplicationSettings.userSettings.theme == "dark").apply {
			trueLocalization = "settingsPage.general.theme.dark"
			falseLocalization = "settingsPage.general.theme.light"

			fun updateTheme() {
				ApplicationSettings.userSettings.theme = if (state) "dark" else "light"
			}
			stateProperty.addListener { observable, oldValue, newValue ->
				if (newValue != oldValue) {
					updateTheme()
				}
			}

			scope.launch {
				ApplicationSettings.userSettings.themeFlow.collectLatest { newTheme ->
					withContext(Dispatchers.JavaFx) {
						state = newTheme == "dark"
					}
				}
			}
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
				ApplicationSettings.userSettings.localization = newValue
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

		//isColorPickerHueSelector
		children.add(SettingElement().apply {
			quickSetting = Switch(false).apply {
				trueLocalization = "settingsPage.general.isColorPickerHueSelector.hueSelector"
				falseLocalization = "settingsPage.general.isColorPickerHueSelector.imageSelector"
			}
			imageContainer.center = SVGPath().apply {
				content = Constants.COLOR_PICKER_MODE
			}
			title.textProperty().bind(Lsp.lsb("settingsPage.general.isColorPickerHueSelector.title"))
			description.textProperty().bind(Lsp.lsb("settingsPage.general.isColorPickerHueSelector.description"))
		})
	}
}
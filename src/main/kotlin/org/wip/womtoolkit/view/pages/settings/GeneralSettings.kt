package org.wip.womtoolkit.view.pages.settings

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
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
	companion object {
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
		const val ANIMATIONS: String =
			"M16.0883 6.41228C16.016 6.31886 15.9377 6.2298 15.8539 6.14569C15.5417 5.83255 15.1606 5.59666 14.741 5.45683L13.3632 5.00939C13.257 4.97196 13.165 4.90253 13.1 4.81068C13.0349 4.71883 13 4.60908 13 4.49656C13 4.38404 13.0349 4.27429 13.1 4.18244C13.165 4.09058 13.257 4.02116 13.3632 3.98372L14.741 3.53628C15.1547 3.39352 15.5299 3.15705 15.837 2.84537C16.1357 2.54224 16.3623 2.17595 16.5 1.77372L16.5114 1.73963L16.9592 0.362894C16.9967 0.256782 17.0662 0.164895 17.1581 0.0998993C17.25 0.0349035 17.3598 0 17.4724 0C17.5851 0 17.6949 0.0349035 17.7868 0.0998993C17.8787 0.164895 17.9482 0.256782 17.9857 0.362894L18.4335 1.73963C18.5727 2.15819 18.8077 2.53853 19.1198 2.85041C19.432 3.1623 19.8126 3.39715 20.2315 3.53628L21.6093 3.98372L21.6368 3.99061C21.743 4.02804 21.835 4.09747 21.9 4.18932C21.9651 4.28117 22 4.39092 22 4.50344C22 4.61596 21.9651 4.72571 21.9 4.81756C21.835 4.90942 21.743 4.97884 21.6368 5.01628L20.259 5.46372C19.8402 5.60285 19.4595 5.8377 19.1474 6.14959C18.8353 6.46147 18.6003 6.84181 18.461 7.26037L18.0132 8.63711C18.0092 8.64855 18.0048 8.65983 18 8.67093C17.9605 8.76273 17.8964 8.84212 17.8144 8.9001C17.7224 8.9651 17.6126 9 17.5 9C17.3874 9 17.2776 8.9651 17.1856 8.9001C17.0937 8.8351 17.0242 8.74322 16.9868 8.63711L16.539 7.26037C16.4378 6.95331 16.2851 6.66664 16.0883 6.41228ZM23.7829 10.2132L23.0175 9.9646C22.7848 9.8873 22.5733 9.75683 22.3999 9.58356C22.2265 9.41029 22.0959 9.199 22.0186 8.96646L21.7698 8.20161C21.749 8.14266 21.7104 8.09161 21.6593 8.0555C21.6083 8.01939 21.5473 8 21.4847 8C21.4221 8 21.3611 8.01939 21.31 8.0555C21.259 8.09161 21.2204 8.14266 21.1996 8.20161L20.9508 8.96646C20.875 9.19736 20.7467 9.40761 20.5761 9.58076C20.4055 9.75392 20.1971 9.88529 19.9672 9.9646L19.2018 10.2132C19.1428 10.234 19.0917 10.2725 19.0555 10.3236C19.0194 10.3746 19 10.4356 19 10.4981C19 10.5606 19.0194 10.6216 19.0555 10.6726C19.0917 10.7236 19.1428 10.7622 19.2018 10.783L19.9672 11.0316C20.2003 11.1093 20.412 11.2403 20.5855 11.4143C20.7589 11.5882 20.8893 11.8003 20.9661 12.0335L21.2149 12.7984C21.2357 12.8573 21.2743 12.9084 21.3254 12.9445C21.3764 12.9806 21.4374 13 21.5 13C21.5626 13 21.6236 12.9806 21.6746 12.9445C21.7257 12.9084 21.7643 12.8573 21.7851 12.7984L22.0339 12.0335C22.1113 11.801 22.2418 11.5897 22.4152 11.4164C22.5886 11.2432 22.8001 11.1127 23.0328 11.0354L23.7982 10.7868C23.8572 10.766 23.9083 10.7275 23.9445 10.6764C23.9806 10.6254 24 10.5644 24 10.5019C24 10.4394 23.9806 10.3784 23.9445 10.3274C23.9083 10.2764 23.8572 10.2378 23.7982 10.217L23.7829 10.2132ZM10.251 3.00275C11.2175 3.00275 12.001 3.78625 12.001 4.75275V9.25275C12.001 10.2192 11.2175 11.0027 10.251 11.0027H4.75098C3.78448 11.0027 3.00098 10.2192 3.00098 9.25275V4.75275C3.00098 3.78625 3.78448 3.00275 4.75098 3.00275H10.251ZM7.23856 12.9845C8.20506 12.9845 8.98856 13.7681 8.98856 14.7345V19.249C8.98856 20.2155 8.20506 20.999 7.23856 20.999H4.75098C3.78448 20.999 3.00098 20.2155 3.00098 19.249V14.7345C3.00098 13.7681 3.78448 12.9845 4.75098 12.9845H7.23856ZM19.2527 12.999C20.2192 12.999 21.0027 13.7825 21.0027 14.749V19.249C21.0027 20.2155 20.2192 20.999 19.2527 20.999H12.7527C11.7862 20.999 11.0027 20.2155 11.0027 19.249V14.749L11.0085 14.6054C11.0815 13.7061 11.8345 12.999 12.7527 12.999H19.2527Z"


		var colorPresets = mutableListOf(
			"#c01135ff",  // Rosso Cremisi - meno vivace
			"#e60000ff",  // Rosso Brillante - meno vivace
			"#e63e00ff",  // Rosso-Arancio - meno vivace
			"#e67248ff",  // Corallo - meno vivace
			"#e69100ff",  // Arancio Brillante - meno vivace
			"#e6bf00ff",  // Oro Brillante - meno vivace
			"#e6e600ff",  // Giallo Puro - meno vivace
			"#9acd28ff",  // Verde Giallo Elettrico - meno vivace
			"#71e600ff",  // Verde Chartreuse - meno vivace
			"#00e600ff",  // Verde Brillante - meno vivace
			"#00de88ff",  // Verde Acqua Medio - meno vivace
			"#00e6e6ff",  // Ciano Brillante - meno vivace
			"#00a8e6ff",  // Azzurro Cielo Profondo - meno vivace
			"#197eceff",  // Blu Dodger - meno vivace
			"#0000e6ff",  // Blu Puro - meno vivace
			"#7b25c9ff",  // Blu Violetto - meno vivace
			"#882ba8ff",  // Viola Scuro - meno vivace
			"#e600e6ff",  // Magenta Brillante - meno vivace
			"#e61182ff",  // Rosa Intenso - meno vivace
			"#e65c9fff",  // Rosa Caldo - meno vivace
		)
	}

	@FXML lateinit var accentSetting: SettingElement
	@FXML lateinit var themeSetting: SettingElement
	@FXML lateinit var localizationSetting: SettingElement
	@FXML lateinit var startingPageSetting: SettingElement

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
		accentSetting.apply {
			title.textProperty().bind(Lsp.lsb("settingsPage.general.accent.title"))
			description.textProperty().bind(Lsp.lsb("settingsPage.general.accent.description"))
			imageContainer.center = SVGPath().apply {
				content = ACCENT
			}
			quickSetting = ColorPickerButton().apply {
				isColorPickerAvailable = true
				colorProperty.value = ApplicationSettings.userSettings.accent.value

				colorProperty.addListener { _, _, newColor ->
					if (newColor != ApplicationSettings.userSettings.accent)
						ApplicationSettings.userSettings.accent.value = newColor
				}

				scope.launch(Dispatchers.JavaFx) {
					ApplicationSettings.userSettings.accent.collectLatest { newColor ->
						withContext(Dispatchers.JavaFx) {
							if (newColor != colorProperty.value)
								colorProperty.value = newColor
						}
					}
				}
			}
			expandableContent = VBox().apply {
				spacing = 8.0

				fun getSelectableColorPicker(): ColorPickerButton {
					return ColorPickerButton().apply {
						scope.launch(Dispatchers.IO) {
							ApplicationSettings.userSettings.accent.collectLatest { newColor ->
								withContext(Dispatchers.JavaFx) {
									isSelectedProperty.value = newColor == colorProperty.value
								}
							}
						}
						onAction = EventHandler {
							ApplicationSettings.userSettings.accent.value = colorProperty.value
						}
						colorProperty.addListener { _, _, newColor ->
							isSelectedProperty.value = colorProperty.value == ApplicationSettings.userSettings.accent.value
						}
						Platform.runLater {
							isSelectedProperty.value = colorProperty.value == ApplicationSettings.userSettings.accent.value
						}
					}
				}

				children.addAll(
					VBox().apply {
						setMargin(this, Insets(0.0, 0.0, 0.0, 55.0))
						spacing = 8.0
						children.addAll(
							Label().apply {
								textProperty().bind(Lsp.lsb("settingsPage.general.accent.history"))
							},
							HBox().apply {
								for (x in 0..4) {
									children.add(getSelectableColorPicker())
								}

								fun updateColorPickerButtons() {
									children.forEachIndexed { index, node ->
										if (node is ColorPickerButton) {
											if (index >= ApplicationSettings.userSettings.accentHistory.value.size) {
												node.visibleProperty().set(false)
											} else {
												// Controllo aggiuntivo per evitare IndexOutOfBounds
												val historyIndex = ApplicationSettings.userSettings.accentHistory.value.size - 1 - index
												if (historyIndex in ApplicationSettings.userSettings.accentHistory.value.indices) {
													node.visibleProperty().set(true)
													node.colorProperty.value = ApplicationSettings.userSettings.accentHistory.value[historyIndex]
												} else {
													node.visibleProperty().set(false)
												}
											}
										}
									}
								}

								scope.launch(Dispatchers.IO) {
									ApplicationSettings.userSettings.accentHistory.collectLatest { newColors ->
										withContext(Dispatchers.JavaFx) {
											updateColorPickerButtons()
										}
									}
								}
							},
						)
					},
					Separator().apply {
						styleClass.add("my-separator")
					},
					VBox().apply {
						setMargin(this, Insets(0.0, 0.0, 0.0, 55.0))
						spacing = 8.0
						children.addAll(
							Label().apply {
								textProperty().bind(Lsp.lsb("settingsPage.general.accent.presets"))
							},
							GridPane().apply {
								for (x in 0..4) {
									for (y in 0..3) {
										val color = getSelectableColorPicker()
										color.colorProperty.value = Color.web(colorPresets[x * 4 + y])
										add(color, x, y)
									}
								}
							}
						)
					}
				)
			}
		}

		themeSetting.apply {
			title.textProperty().bind(Lsp.lsb("settingsPage.general.theme.title"))
			description.textProperty().bind(Lsp.lsb("settingsPage.general.theme.description"))
			imageContainer.center = SVGPath().apply {
				content = THEME
			}
			quickSetting = Switch(ApplicationSettings.userSettings.theme.value == "dark").apply {
				trueLocalization = "settingsPage.general.theme.dark"
				falseLocalization = "settingsPage.general.theme.light"

				fun updateTheme() {
					ApplicationSettings.userSettings.theme.value = if (state) "dark" else "light"
				}

				stateProperty.addListener { observable, oldValue, newValue ->
					if (newValue != oldValue) {
						updateTheme()
					}
				}

				scope.launch {
					ApplicationSettings.userSettings.theme.collectLatest { newTheme ->
						withContext(Dispatchers.JavaFx) {
							state = newTheme == "dark"
						}
					}
				}
			}
		}

		localizationSetting.apply {
			title.textProperty().bind(Lsp.lsb("settingsPage.general.language.title"))
			description.textProperty().bind(Lsp.lsb("settingsPage.general.language.description"))
			imageContainer.center = SVGPath().apply {
				content = LOCALIZATION
			}
			quickSetting = ChoiceBox<String>().apply {
				items.addAll(LocalizationService.locales)
				value = LocalizationService.currentLocale
				onMouseClicked = EventHandler {
					hide()
					Platform.runLater {
						show()
					}
				}
				valueProperty().addListener { _, _, newValue ->
					ApplicationSettings.userSettings.localization.value = newValue
				}
			}
		}

		//TODO: complete
		startingPageSetting.apply {
			title.textProperty().bind(Lsp.lsb("settingsPage.general.startingPage.title"))
			description.textProperty().bind(Lsp.lsb("settingsPage.general.startingPage.description"))
			imageContainer.center = SVGPath().apply {
				content = STARTING_PAGE
			}
			quickSetting = ChoiceBox<String>().apply {
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
		}

		//isColorPickerHueSelector
		children.add(SettingElement().apply {
			title.textProperty().bind(Lsp.lsb("settingsPage.general.colorPickerMode.title"))
			description.textProperty().bind(Lsp.lsb("settingsPage.general.colorPickerMode.description"))
			imageContainer.center = SVGPath().apply {
				content = COLOR_PICKER_MODE
			}
		})

		children.add(SettingElement().apply {
			title.textProperty().bind(Lsp.lsb("settingsPage.general.disableAnimations.title"))
			description.textProperty().bind(Lsp.lsb("settingsPage.general.disableAnimations.description"))
			imageContainer.center = SVGPath().apply {
				content = ANIMATIONS
			}
			quickSetting = Switch(ApplicationSettings.userSettings.disableAnimations.value).apply {
				trueLocalization = "settingsPage.general.disableAnimations.enabled"
				falseLocalization = "settingsPage.general.disableAnimations.disabled"

				stateProperty.addListener { observable, oldValue, newValue ->
					if (newValue != oldValue) {
						ApplicationSettings.userSettings.disableAnimations.value = newValue
					}
				}

				scope.launch(Dispatchers.JavaFx) {
					ApplicationSettings.userSettings.disableAnimations.collectLatest { newValue ->
						withContext(Dispatchers.JavaFx) {
							state = newValue
						}
					}
				}
			}

		})
	}
}
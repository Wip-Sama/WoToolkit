package org.wip.womtoolkit.view.pages

import com.pty4j.PtyProcessBuilder
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class Slicer : BorderPane() {
    @FXML lateinit var cliInput: TextField
    @FXML lateinit var cliOutput: Label
    @FXML lateinit var testUpdate: ProgressBar

    val scope =MainScope()

    init {
        FXMLLoader(javaClass.getResource("/view/pages/slicer.fxml")).apply {
            setRoot(this@Slicer)
            setController(this@Slicer)
            load()
        }
    }

    @FXML
    fun initialize() {
    }

    @FXML fun testCli() {
        val command = cliInput.text
        if (command.isBlank()) {
            cliOutput.text = "Please enter a command."
            return
        }

        val userCommand = cliInput.text
        val commandList = userCommand.split(" ")
        scope.launch(Dispatchers.IO) {
            val process = PtyProcessBuilder()
                .setCommand(commandList.toTypedArray())
                .setDirectory(System.getProperty("user.dir"))
                .start()

            val reader = process.inputStream.bufferedReader()
            var line: String?
            do {
                line = reader.readLine()
                if (line != null) {
                    // Remove ANSI escape codes and trim the line
                    val ansiRegex = Regex("\\u001B\\[[0-9;?]*[A-Za-z]")
                    val cleanedLine = ansiRegex.replace(line, "").replace("\r", "").replace("\n", "").trim()

                    val num = cleanedLine.toIntOrNull()
                    Platform.runLater {
                        testUpdate.progress = num?.div(10.0)!!
                        cliOutput.text = cleanedLine
                    }

                } else {
                    delay(100)
                }
            } while (process.isAlive || line != null)
        }
    }
}
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
import java.io.File

class Slicer : BorderPane() {
    @FXML lateinit var cliInput: TextField
    @FXML lateinit var cliOutput: Label
    @FXML lateinit var testUpdate: ProgressBar

    val scope = MainScope()

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

        scope.launch(Dispatchers.IO) {

            val commandList = listOf("powershell.exe", "-Command", command)

            commandList.forEach { command -> println(command) }

//            val process = ProcessBuilder(commandList)
//                .directory(File(System.getProperty("user.dir")))
//                .start()


            val process = PtyProcessBuilder()
                .setCommand(commandList.toTypedArray())
                .setDirectory(System.getProperty("user.dir"))
                .start()

            val reader = process.inputStream.bufferedReader()
            var line: String?

            do {
                line = reader.readLine()
                if (line != null) {
                    val ansiRegex = Regex("\\u001B\\[[0-9;?]*[A-Za-z]")
                    val cleanedLine = ansiRegex.replace(line, "").replace("\r", "").replace("\n", "").trim()

                    Platform.runLater {
                        cliOutput.text = cleanedLine
                    }
                } else {
                    delay(500)
                }

            } while (process.isAlive || line != null)
        }
    }
}
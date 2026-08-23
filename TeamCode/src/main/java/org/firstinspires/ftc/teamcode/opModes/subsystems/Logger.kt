package org.firstinspires.ftc.teamcode.opModes.subsystems

import com.bylazar.configurables.annotations.Configurable
import dev.nextftc.core.subsystems.Subsystem
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter


@Configurable
object Logger : Subsystem {

    private var writer: BufferedWriter? = null
    private var activeHeaders: List<String> = emptyList()

    fun start(fileType: String, headers: List<String>) {
        val file = File("/sdcard/FIRST/$fileType.csv")
        activeHeaders = headers

        writer = BufferedWriter(FileWriter(file))
        writer?.apply {
            write(activeHeaders.joinToString(","))
            newLine()
        }
    }

    fun log(data: Map<String, Any>) {
        if (writer == null) return

        val rowValues = activeHeaders.map { header ->
            data[header] ?: ""
        }

        writer?.apply {
            write(rowValues.joinToString(","))
            newLine()
        }
    }

    fun close() {
        writer?.flush()
        writer?.close()
        writer = null
    }
}
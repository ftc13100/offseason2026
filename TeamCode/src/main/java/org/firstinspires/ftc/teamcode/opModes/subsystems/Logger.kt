package org.firstinspires.ftc.teamcode.opModes.subsystems

import dev.nextftc.core.subsystems.Subsystem
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

object Logger : Subsystem {

    private var writer: BufferedWriter? = null

    fun start(fileType: String) {
        val file = File("/sdcard/FIRST/$fileType.csv")

        writer = BufferedWriter(FileWriter(file))
        writer?.apply {
            write("Time,ShooterVelocity,PositionX,PositionY")
            newLine()
        }
    }

    fun log(
        blueAlliance: Boolean,
        redAlliance : Boolean,
        time: Long,
        positionX: Double,
        positionY: Double,
        heading : Double,
        shooterVelocity: Double,

        ) {
        writer?.apply {
            write("%.0f,%.0f,%.1f,%.1f".format(
                blueAlliance,
                redAlliance,
                time.toDouble(),
                positionX,
                positionY,
                heading,
                shooterVelocity
                ))
            newLine()
        }
    }

    fun close() {
        writer?.flush()
        writer?.close()
        writer = null
    }
}
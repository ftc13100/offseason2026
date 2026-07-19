package org.firstinspires.ftc.teamcode.opModes.teleOp

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class LogTest {

    private val writer: BufferedWriter

    init {
        val file = File("/sdcard/FIRST/loggingtest.csv")

        writer = BufferedWriter(FileWriter(file))

        //CSV header
        writer.write("Time, ShooterVelocity, Position X, Position Y,")
        writer.newLine()
    }

    fun log(time: Long, shooterVelocity: Double, positionX:Double, positionY: Double) {
        writer.write("$time,$shooterVelocity,%.1f,%.1f".format(positionX, positionY))
        writer.newLine()
    }

    fun close() {
        writer.flush()
        writer.close()
    }
}
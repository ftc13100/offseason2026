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
        writer.write("Time,X,Y,Heading,ProjectedX,ProjectedY,TurretTarget,TurretActual,ShooterTarget,ShooterActual,ShooterPower")
        writer.newLine()
    }

    fun log(time: Long, x: Double, y: Double, heading: Double, projectedX: Double, projectedY: Double, TurretTarget: Double, TurretActual: Double, ShooterTarget: Double, ShooterActual: Double, ShooterPower: Double) {
        writer.write("$time,$x,$y,$heading,$projectedX,$projectedY,$TurretTarget,$TurretActual,$ShooterTarget,$ShooterActual,$ShooterPower")
        writer.newLine()
    }

    fun close() {
        writer.flush()
        writer.close()
    }
}
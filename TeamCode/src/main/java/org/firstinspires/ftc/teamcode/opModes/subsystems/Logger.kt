package org.firstinspires.ftc.teamcode.opModes.subsystems

import com.qualcomm.robotcore.hardware.NormalizedColorSensor
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
            write("Blue Alliance, Red Alliance, Time,PositionX," +
                    "PositionY, Heading, AngularVelocity, taFieldRef, taStatic, taAV, taOffset, taRobotRef," +
                    " taServoPos, shooterTarget, shooterAdjust, " +
                    "shooterVelocity, shooterAngle, intakePower, intakeCurrent, spindexerPower," +
                    "spindexerPosition, spindexerVelocity, spindexerResult, spindexerTarget")
            newLine()
        }
    }

    fun log(
        time: Long,
        alliance : String,
        positionX: Double,
        positionY: Double,
        heading : Double,
        angularVelocity : Double,
        taFieldRef : Double,
        taStatic: Double,
        taAV: Double,
        taOffset: Double,
        taRobotRef: Double,
        taServoPos: Double,
        shooterTarget: Double,
        shooterAdjust: Double,
        shooterVelocity: Double,
        shooterAngle: Double,
        intakePower: Double,
        intakeCurrent: Double,
        spindexerPower: Double,
        spindexerPosition: Double,
        spindexerVelocity: Double,
        spindexerResult: Int,
        spindexerTarget: Double
        ) {
        writer?.apply {
            write("%b,%b,%.0f,%.1f,%.1f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f".format(
                time.toDouble(),
                alliance,
                positionX,
                positionY,
                heading,
                angularVelocity,
                taFieldRef,
                taStatic,
                taAV,
                taOffset,
                taRobotRef,
                taServoPos,
                shooterTarget,
                shooterAdjust,
                shooterVelocity,
                shooterAngle,
                intakePower,
                intakeCurrent,
                spindexerPower,
                spindexerPosition,
                spindexerVelocity,
                spindexerResult,
                spindexerTarget
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
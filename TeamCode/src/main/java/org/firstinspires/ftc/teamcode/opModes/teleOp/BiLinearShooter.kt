package org.firstinspires.ftc.teamcode.opModes.teleOp

import com.pedropathing.geometry.Pose
import dev.nextftc.core.commands.CommandManager
import org.firstinspires.ftc.teamcode.opModes.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.Shooter
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.ShooterAngle
import kotlin.math.pow
import kotlin.math.sqrt

object BiLinearShooter {
    val goalClose = Pose(4.0, 139.5)
    val goalFar = Pose(3.0, 139.0)


    data class ShotParameters(val velocity: Double, val angle: Double, val spinspeed: Double)

    private data class DataPoint(
        val x: Double,
        val y: Double,
        val velocity: Double,
        val angle: Double,
        val spinspeed: Double
    )

    private val shotData = listOf(
        DataPoint(24.0, 117.97, 1960.0, 0.800, 0.9),
        DataPoint(48.0, 117.97, 1780.0, 0.550, 0.9),
        DataPoint(96.0, 117.97, 1460.0, 0.300, 0.9),
        DataPoint(72.0, 117.97, 1640.0, 0.600, 0.9),
        DataPoint(72.0, 93.97, 1640.0, 0.600, 0.9),
        DataPoint(24.0, 93.97, 1960.0, 0.750, 0.9),
        DataPoint(96.0, 93.97, 1400.0, 0.350, 0.9),
        DataPoint(48.0, 93.97, 1840.0, 0.750, 0.9),
        DataPoint(120.0, 93.97, 1600.0, 0.700, 0.9),
        DataPoint(72.0, 69.97, 1720.0, 0.700, 0.9),
        DataPoint(96.0, 69.97, 1780.0, 0.700, 0.9),
        DataPoint(48.0, 69.97, 1820.0, 0.650, 0.9),
        DataPoint(72.0, 45.97, 1860.0, 0.700, 0.9),
        DataPoint(98.03, 24.00, 1880.0, 0.650, 0.9),
        DataPoint(72.0, 21.97, 2100.0, 0.800, 0.9),
        DataPoint(50.03, 21.97, 2060.0, 0.550, 0.9)
    )

    private const val IDW_POWER = 2.0  // Higher = more weight to closer points
    private const val EPSILON = 1e-6   // Threshold for exact match

    /**
     * Get shot parameters using Inverse Distance Weighting (IDW) interpolation.
     * Handles scattered data points without needing a complete grid.
     */
    fun getShot(x: Double, y: Double): ShotParameters {
        val distances = shotData.map { point ->
            val dx = x - point.x
            val dy = y - point.y
            sqrt(dx * dx + dy * dy)
        }

        // If we're exactly on a data point, return it directly
        val minDistance = distances.minOrNull() ?: 0.0
        if (minDistance < EPSILON) {
            val exactPoint = shotData[distances.indexOf(minDistance)]
            return ShotParameters(exactPoint.velocity, exactPoint.angle, exactPoint.spinspeed)
        }

        // IDW interpolation: weight = 1 / distance^power
        val weights = distances.map { 1.0 / it.pow(IDW_POWER) }
        val totalWeight = weights.sum()

        val velocity = shotData.zip(weights)
            .sumOf { (point, weight) -> point.velocity * weight } / totalWeight

        val angle = shotData.zip(weights)
            .sumOf { (point, weight) -> point.angle * weight } / totalWeight

        val spinspeed = shotData.zip(weights)
            .sumOf { (point, weight) -> point.spinspeed * weight } / totalWeight

        return ShotParameters(velocity, angle, spinspeed)
    }

    /**
     * Apply a shot by setting the hood angle and flywheel speed.
     */
    fun applyShot(params: ShotParameters) {
        ShooterAngle.targetPosition = params.angle + ShooterAngle.manualOffset
        CommandManager.scheduleCommand(ShooterAngle.update())
        CommandManager.scheduleCommand(Shooter.spinAtSpeed(params.velocity + Shooter.manualOffset))
        Spindexer.spinShotSpeed = params.spinspeed
    }
}
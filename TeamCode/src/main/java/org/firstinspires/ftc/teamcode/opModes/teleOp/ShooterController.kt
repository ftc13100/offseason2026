package org.firstinspires.ftc.teamcode.opModes.teleOp

import com.pedropathing.geometry.Pose
import dev.nextftc.core.commands.CommandManager
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.Shooter
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.ShooterAngle


object ShooterController {
    val goal = Pose(6.0, 138.0)
    const val SHOOTER_TO_GOAL_Z_SQRD = 1139.0 // (46.0 - 12.25).pow(2.0)

    data class ShotParameters(val distance: Double, val velocity: Double, val angle: Double)

    private val shooterLookupTable = mapOf(
   //Distance to ShotParameters(Dist,            Velocity,        Angle)            // Origin (X, Y)
        42.27  to ShotParameters(42.27,  985.0, 0.104),     // Pair(24, 120)
        48.61  to ShotParameters(48.61, 1020.0, 0.145),     // Pair(36, 120)
        56.81  to ShotParameters(56.81, 1000.0, 0.186),     // Pair(48, 120)
        61.67  to ShotParameters(61.67, 1020.0, 0.193),     // Pair(48, 108)
        68.32  to ShotParameters(68.32, 1065.0, 0.173),     // Pair(48, 96)
        76.29  to ShotParameters(76.29, 1080.0, 0.200),     // Pair(72, 120)
        79.98  to ShotParameters(79.98, 1150.0, 0.207),     // Pair(72, 108)
        85.20  to ShotParameters(85.20, 1180.0, 0.207),     // Pair(72, 96)
        97.80  to ShotParameters(97.80, 1200.0, 0.225),     // Pair(96, 120)
        99.25  to ShotParameters(99.25, 1200.0, 0.218),     // Pair(72, 72)
        104.90 to ShotParameters(104.90,1290.0, 0.225),     // Pair(96, 96)
        135.99 to ShotParameters(135.99,1460.0, 0.225),     // Pair(72, 24)
        137.03 to ShotParameters(137.03,1460.0, 0.225),     // Pair(48, 12)
        146.18 to ShotParameters(146.18,1510.0, 0.212),     // Pair(72, 12)
        158.48 to ShotParameters(158.48,1560.0, 0.212)      // Pair(96, 12)
    ).toSortedMap()

    private fun lerp(x: Double, x0: Double, x1: Double, y0: Double, y1: Double): Double {
        return y0 + (x - x0) * (y1 - y0) / (x1 - x0)
    }

    /**
     * Finds the shot parameters for the distance closest to the target.
     * If the distance is within a certain threshold it returns the entry.
     */

    fun getShot(distance: Double): ShotParameters? {
        val keys = shooterLookupTable.keys.sorted()

        // Clamp to bounds
        if (distance !in keys.first()..keys.last()) {
            return null
        }

        var low = 0
        var high = keys.size - 1

        while (low <= high) {
            val mid = (low + high) ushr 1
            val midValue = keys[mid]

            when {
                distance < midValue -> high = mid - 1
                distance > midValue -> low = mid + 1
                else -> return shooterLookupTable[midValue]!!
            }
        }

        // At this point:
        // high < low
        // high is index of lower bound
        // low is index of upper bound

        val lowerKey = keys[high]
        val upperKey = keys[low]

        val lower = shooterLookupTable[lowerKey]!!
        val upper = shooterLookupTable[upperKey]!!

        return ShotParameters(
            distance = distance,
            velocity = lerp(distance, lowerKey, upperKey, lower.velocity, upper.velocity),
            angle = lerp(distance, lowerKey, upperKey, lower.angle, upper.angle)
        )
    }

    fun applyShot(params: ShotParameters) {
        // Set the hood angle
        ShooterAngle.targetPosition = params.angle

        // Schedule the movement and the flywheel spin
        CommandManager.scheduleCommand(
            ShooterAngle.update()
        )

        CommandManager.scheduleCommand(
            Shooter.spinAtSpeed(params.velocity)
        )
    }
}

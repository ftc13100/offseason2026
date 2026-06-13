package org.firstinspires.ftc.teamcode.opModes.teleOp

import com.pedropathing.geometry.Pose
import dev.nextftc.control2.util.InterpolatingMap2D
import dev.nextftc.core.commands.CommandManager
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.Shooter
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.ShooterAngle

object Interpolation {

    data class ShotParameters(
        val velocity: Double,
        val angle: Double,
        val airTime: Double
    )

    private val flywheelVelocity = InterpolatingMap2D.bilinear()
    private val hood = InterpolatingMap2D.bilinear()
    private val airTime = InterpolatingMap2D.bilinear()

    private fun clamp(v: Double, min: Double, max: Double): Double {
        return maxOf(min, minOf(max, v))
    }

    init {
        // =========================
        // FLYWHEEL VELOCITY
        // =========================

        // y = 144
        flywheelVelocity[24.0, 144.0] = 1935.0
        flywheelVelocity[48.0, 144.0] = 1805.0
        flywheelVelocity[72.0, 144.0] = 1675.0
        flywheelVelocity[96.0, 144.0] = 1520.0
        flywheelVelocity[120.0, 144.0] = 1500.0

        // y = 117.97
        flywheelVelocity[0.0, 117.97] = 1995.0
        flywheelVelocity[24.0, 117.97] = 1960.0
        flywheelVelocity[48.0, 117.97] = 1780.0
        flywheelVelocity[72.0, 117.97] = 1640.0
        flywheelVelocity[96.0, 117.97] = 1460.0
        flywheelVelocity[120.0, 117.97] = 1508.0
        flywheelVelocity[144.0, 117.97] = 1470.0

        // y = 93.97
        flywheelVelocity[0.0, 93.97] = 1988.0
        flywheelVelocity[24.0, 93.97] = 1960.0
        flywheelVelocity[48.0, 93.97] = 1840.0
        flywheelVelocity[72.0, 93.97] = 1640.0
        flywheelVelocity[96.0, 93.97] = 1400.0
        flywheelVelocity[120.0, 93.97] = 1565.0
        flywheelVelocity[144.0, 93.97] = 1540.0

        // y = 69.97
        flywheelVelocity[0.0, 69.97] = 1872.0
        flywheelVelocity[24.0, 69.97] = 1854.0
        flywheelVelocity[48.0, 69.97] = 1820.0
        flywheelVelocity[72.0, 69.97] = 1720.0
        flywheelVelocity[96.0, 69.97] = 1780.0
        flywheelVelocity[120.0, 69.97] = 1762.0
        flywheelVelocity[144.0, 69.97] = 1750.0

        // y = 45.97
        flywheelVelocity[0.0, 45.97] = 1905.0
        flywheelVelocity[24.0, 45.97] = 1894.0
        flywheelVelocity[48.0, 45.97] = 1908.0
        flywheelVelocity[72.0, 45.97] = 1860.0
        flywheelVelocity[96.0, 45.97] = 1858.0
        flywheelVelocity[120.0, 45.97] = 1846.0
        flywheelVelocity[144.0, 45.97] = 1840.0

        // y = 21.97
        flywheelVelocity[0.0, 21.97] = 2025.0
        flywheelVelocity[24.0, 21.97] = 2002.0
        flywheelVelocity[48.0, 21.97] = 2060.0
        flywheelVelocity[72.0, 21.97] = 2100.0
        flywheelVelocity[96.0, 21.97] = 1883.0
        flywheelVelocity[120.0, 21.97] = 1902.0
        flywheelVelocity[144.0, 21.97] = 1895.0

        // y = 0
        flywheelVelocity[24.0, 0.0] = 2050.0
        flywheelVelocity[48.0, 0.0] = 2040.0
        flywheelVelocity[72.0, 0.0] = 2120.0
        flywheelVelocity[96.0, 0.0] = 1950.0
        flywheelVelocity[120.0, 0.0] = 1930.0


        // =========================
        // HOOD
        // =========================

        // y = 144
        hood[24.0, 144.0] = 0.810
        hood[48.0, 144.0] = 0.580
        hood[72.0, 144.0] = 0.620
        hood[96.0, 144.0] = 0.330
        hood[120.0, 144.0] = 0.370

        // y = 117.97
        hood[0.0, 117.97] = 0.820
        hood[24.0, 117.97] = 0.800
        hood[48.0, 117.97] = 0.550
        hood[72.0, 117.97] = 0.600
        hood[96.0, 117.97] = 0.300
        hood[120.0, 117.97] = 0.360
        hood[144.0, 117.97] = 0.340

        // y = 93.97
        hood[0.0, 93.97] = 0.770
        hood[24.0, 93.97] = 0.750
        hood[48.0, 93.97] = 0.750
        hood[72.0, 93.97] = 0.600
        hood[96.0, 93.97] = 0.350
        hood[120.0, 93.97] = 0.420
        hood[144.0, 93.97] = 0.410

        // y = 69.97
        hood[0.0, 69.97] = 0.710
        hood[24.0, 69.97] = 0.700
        hood[48.0, 69.97] = 0.650
        hood[72.0, 69.97] = 0.700
        hood[96.0, 69.97] = 0.700
        hood[120.0, 69.97] = 0.690
        hood[144.0, 69.97] = 0.690

        // y = 45.97
        hood[0.0, 45.97] = 0.670
        hood[24.0, 45.97] = 0.660
        hood[48.0, 45.97] = 0.667
        hood[72.0, 45.97] = 0.700
        hood[96.0, 45.97] = 0.697
        hood[120.0, 45.97] = 0.690
        hood[144.0, 45.97] = 0.688

        // y = 21.97
        hood[0.0, 21.97] = 0.640
        hood[24.0, 21.97] = 0.638
        hood[48.0, 21.97] = 0.551
        hood[72.0, 21.97] = 0.800
        hood[96.0, 21.97] = 0.652
        hood[120.0, 21.97] = 0.680
        hood[144.0, 21.97] = 0.670

        // y = 0
        hood[24.0, 0.0] = 0.780
        hood[48.0, 0.0] = 0.560
        hood[72.0, 0.0] = 0.820
        hood[96.0, 0.0] = 0.660
        hood[120.0, 0.0] = 0.650


        // =========================
        // AIR TIME
        // =========================

        airTime[24.0, 117.97] = 0.41
        airTime[60.0, 117.97] = 0.46
        airTime[96.0, 117.97] = 0.35

        airTime[24.0, 69.97] = 0.46
        airTime[60.0, 69.97] = 0.50
        airTime[96.0, 69.97] = 0.39

        airTime[24.0, 21.97] = 0.78
        airTime[60.0, 21.97] = 0.79
        airTime[96.0, 21.97] = 0.81
    }

    // =========================
    // GETTERS
    // =========================

    fun getShot(pose: Pose): ShotParameters {
        return getShot(pose.x, pose.y)
    }

    fun getShot(x: Double, y: Double): ShotParameters {
        val cx = clamp(x, 0.0, 144.0)
        val cy = clamp(y, 0.0, 144.0)

        return ShotParameters(
            velocity = flywheelVelocity[cx, cy],
            angle = hood[cx, cy],
            airTime = airTime[cx, cy]
        )
    }

    fun getFlywheelVelocity(x: Double, y: Double): Double {
        return flywheelVelocity[clamp(x, 0.0, 144.0), clamp(y, 0.0, 144.0)]
    }

    fun getHood(x: Double, y: Double): Double {
        return hood[clamp(x, 0.0, 144.0), clamp(y, 0.0, 144.0)]
    }

    fun getAirTime(x: Double, y: Double): Double {
        return airTime[clamp(x, 0.0, 144.0), clamp(y, 0.0, 144.0)]
    }

    // =========================
    // APPLY
    // =========================
    fun applyShot(params: ShotParameters) {
        val targetAngle = params.angle + ShooterAngle.manualOffset
        val targetVelocity = params.velocity + Shooter.manualOffset

        // Only update angle if it actually changes
        if (ShooterAngle.targetPosition != targetAngle) {
            ShooterAngle.targetPosition = targetAngle
            CommandManager.scheduleCommand(ShooterAngle.update())
        }

        // Always update shooter speed (can be removed if you want extra optimization)
        CommandManager.scheduleCommand(
            Shooter.spinAtSpeed(targetVelocity)
        )
    }
    fun applyShot(pose: Pose) {
        applyShot(pose.x, pose.y)
    }

    fun applyShot(x: Double, y: Double) {
        val shot = getShot(x, y)

        ShooterAngle.targetPosition = shot.angle + ShooterAngle.manualOffset
        CommandManager.scheduleCommand(ShooterAngle.update())
        CommandManager.scheduleCommand(
            Shooter.spinAtSpeed(shot.velocity + Shooter.manualOffset)
        )
    }
}
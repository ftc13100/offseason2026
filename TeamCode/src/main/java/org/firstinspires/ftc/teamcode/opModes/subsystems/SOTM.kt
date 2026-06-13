package org.firstinspires.ftc.teamcode.opModes.subsystems

import com.pedropathing.geometry.Pose
import com.pedropathing.math.Vector
import dev.nextftc.core.subsystems.Subsystem
import org.firstinspires.ftc.teamcode.opModes.teleOp.Interpolation

object SOTM : Subsystem {

    private const val ITERATIONS = 5

    /**
     * Calculate where to aim when shooting on the move.
     * Iteratively computes the virtual target position accounting for robot velocity.
     *
     * @param robotPose Current robot position
     * @param robotVelocity Current robot velocity (inches/second)
     * @return Virtual pose to aim at (where robot will be when shot arrives)
     */
    fun calculateVirtualTarget(robotPose: Pose, robotVelocity: Vector): Pose {
        var virtualPose = robotPose

        for (i in 0..<ITERATIONS) {
            // Get air time for current virtual position
            val airTime = Interpolation.getAirTime(virtualPose.x,virtualPose.y)

            // Calculate where robot will be after this air time
            val futureX = robotPose.x + robotVelocity.xComponent * airTime
            val futureY = robotPose.y + robotVelocity.yComponent * airTime
            virtualPose = Pose(futureX, futureY, robotPose.heading)
        }

        return virtualPose
    }
}
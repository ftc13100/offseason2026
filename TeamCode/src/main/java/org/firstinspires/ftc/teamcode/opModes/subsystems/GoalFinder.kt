//package org.firstinspires.ftc.teamcode.opModes.subsystems
//
//import com.pedropathing.geometry.Pose
//import com.qualcomm.hardware.limelightvision.LLResult
//import com.qualcomm.robotcore.util.ElapsedTime
//import dev.nextftc.core.subsystems.Subsystem
//import org.firstinspires.ftc.teamcode.opModes.teleOp.ShooterController.SHOOTER_TO_GOAL_Z_SQRD
//import org.firstinspires.ftc.teamcode.opModes.teleOp.ShooterController.goalClose
//import kotlin.math.abs
//import kotlin.math.acos
//import kotlin.math.atan2
//import kotlin.math.pow
//import kotlin.math.sqrt
//
//object GoalFinder : Subsystem {
//    var gfActive = false
//    var gfDone = false
//    var gfDoneMs = 0.0
//
//    var gfTargetAngle = 0.0
//    var gfHeadingError =
//        0.0 // Current angular error of Shooter to Goal with margin for shooter adjustment. This is used to rotate robot
//
//    val gfReady: Boolean
//        get() = if (Turret.goalTrackingActive) abs(Turret.turretErrorTicks) < 5.0 else true
//
//    var gfGoalAprilTagAdj = 0.0
//    var gfAnglesValid = false
//    var gfLLValid = false
//    var gfLLTx = 99.0
//    var gfLLTy = 99.0
//    var gfLLTa = 99.0
//    var gfGoalDistance = 1000.0
//    var gfBelowToleranceCount = 0
//    var gfTurretAdj = 0.0
//    var gfTurretAdjLL = 0.0
//    var gfTurretAdjGoalAprilTag = 0.0
//    private const val ALIGNMENT_POWER_COARSE = 0.6
//    private const val ALIGNMENT_POWER_FINE = 0.2
//    private val HEADING_TOLERANCE_COARSE = Math.toRadians(12.0)
//    private val HEADING_TOLERANCE_FINE = Math.toRadians(1.0)
//    private const val TURRET_LL_ADJ_FACTOR = 33
//    private const val HEADING_TOLERANCE_TARGET = 5
//    private val aprilTag = Pose(16.0, 132.0)
//
//    private val gfRuntime = ElapsedTime()
//
//    fun normalizeAngle(angle: Double): Double {
//        var a = angle
//        while (a > Math.PI) a -= 2 * Math.PI
//        while (a < -Math.PI) a += 2 * Math.PI
//        return a
//    }
//
//    fun findGoal() {
//        gfActive = true
//        gfBelowToleranceCount = 0
//        gfDone = false
//        gfDoneMs = 0.0
//        gfRuntime.reset()
//    }
//
//    fun goalFindSuccess() {
//        gfActive = false
//        gfDone = true
//        gfDoneMs = gfRuntime.milliseconds()
//    }
//
//    fun goalFindFailed() {
//        gfActive = false
//        gfDone = false
//    }
//
//    fun stop() {
//        gfActive = false
//        gfDone = false
//
//        val posAdj = Turret.turret.currentPosition - Turret.startPosition
//        Turret.turn(posAdj)
//    }
//
//    fun calculateAngles(pose: Pose, llResult: LLResult?, blueAlliance: Boolean) {
//        val adjX = if (blueAlliance) {
//            pose.x
//        } else {
//            144.0 - pose.x
//        }
//
//        gfGoalDistance =
//            sqrt(
//                (adjX - goalClose.x).pow(2.0) + (pose.y - goalClose.y).pow(2.0) +
//                        SHOOTER_TO_GOAL_Z_SQRD
//            )
//
//        gfTargetAngle = if (blueAlliance) {
//            Math.PI - atan2(abs(goalClose.y - pose.y), abs(goalClose.x - adjX))
//        } else {
//            atan2(abs(goalClose.y - pose.y), abs(goalClose.x - adjX))
//        }
//
//        gfHeadingError =
//            normalizeAngle(gfTargetAngle - Turret.turretHeadingWithMargin(pose.heading))
//        gfAnglesValid = true
//
//        if (llResult == null || !llResult.isValid) {
//            gfLLTx = 99.0
//            gfLLTy = 99.0
//            gfLLTa = 99.0
//            gfLLValid = false
//            return
//        }
//
//        gfLLTx = llResult.tx
//        gfLLTy = llResult.ty
//        gfLLTa = llResult.ta
//        gfLLValid = true
//
////        // Calculate adjustment angle to account for position difference of April tag and desired goalClose
////        val aprilTagVecorX = aprilTag.x - adjX
////        val aprilTagVecorY = aprilTag.y - pose.y
////        val goalVectorX = goalClose.x - adjX
////        val goalVectorY = goalClose.y - pose.y
////
////        gfGoalAprilTagAdj = acos(
////            (aprilTagVecorX * goalVectorX + aprilTagVecorY * goalVectorY) / (sqrt(aprilTagVecorX * aprilTagVecorX + aprilTagVecorY * aprilTagVecorY) * sqrt(
////                goalVectorX * goalVectorX + goalVectorY * goalVectorY
////            ))
////        )
//
//        if (blueAlliance) {
//            if (gfTargetAngle < Math.PI * 3.0 / 4.0) {
//                // Turret has to turn left for adjustment
//                gfGoalAprilTagAdj = -gfGoalAprilTagAdj
//            }
//        } else {
//            if (gfTargetAngle < Math.PI / 4.0) {
//                // Turret has to turn left for adjustment
//                gfGoalAprilTagAdj = -gfGoalAprilTagAdj
//            }
//        }
//
//        gfTurretAdjLL = gfLLTx * TURRET_LL_ADJ_FACTOR
//        gfTurretAdjGoalAprilTag = gfGoalAprilTagAdj / Turret.TURRET_TICKS_TO_RADS
//        gfTurretAdj = gfTurretAdjLL //+ gfTurretAdjGoalAprilTag
//    }
//
//    fun calculate(pose: Pose, llResult: LLResult?, blueAlliance: Boolean): Double {
//        if (pose.x < 0.0 || pose.x > 144.0 || pose.y < 0.0 || pose.y > 144.0) {
//            gfAnglesValid = false
//            gfLLValid = false
//            return 0.0
//        }
//
//        calculateAngles(pose, llResult, blueAlliance)
//
//        if (!gfActive)
//            return 0.0
//
//        if (gfBelowToleranceCount == 0 && abs(gfHeadingError) < HEADING_TOLERANCE_FINE) {
//            goalFindSuccess()
//            return 0.0
//        } else if (abs(gfHeadingError) > HEADING_TOLERANCE_COARSE) {
//            return if (gfHeadingError > 0) {
//                -ALIGNMENT_POWER_COARSE
//            } else {
//                ALIGNMENT_POWER_COARSE
//            }
//        } else if (abs(gfHeadingError) > HEADING_TOLERANCE_FINE) {
//            return if (gfHeadingError > 0) {
//                -ALIGNMENT_POWER_FINE
//            } else {
//                ALIGNMENT_POWER_FINE
//            }
//        } else if (++gfBelowToleranceCount < HEADING_TOLERANCE_TARGET) {
//            return if (gfHeadingError > 0) {
//                -ALIGNMENT_POWER_FINE
//            } else {
//                ALIGNMENT_POWER_FINE
//            }
//        }
//        return 0.0
//    }
//
//    fun adjustToLL() {
//        if (!gfAnglesValid || !gfLLValid) {
//            return
//        }
//
//        Turret.turn(gfTurretAdj)
//    }
//}

package org.firstinspires.ftc.teamcode.opModes.subsystems
//
//import com.bylazar.configurables.annotations.Configurable
//import dev.nextftc.core.subsystems.Subsystem
//import dev.nextftc.hardware.impl.MotorEx
//import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
//
//@Configurable
//object NewIntake : Subsystem {
//
//    val intake = MotorEx("intake").brakeMode().reversed()
//
//    @JvmField var CURRENT_THRESHOLD_FAST = 6500.0
//    @JvmField var CURRENT_THRESHOLD_SLOW = 4500.0
//
//    enum class State {
//        IDLE,
//        INTAKE_FAST,
//        INTAKE_SLOW,
//        JAM_CLEAR,
//        REVERSE
//    }
//
//    var state = State.IDLE
//
//    var intakeRunning = false
//    var intakeSpindexer = false
//
//    private var jamStartTime = 0.0
//
//    override fun periodic() {
//
//        val current = intake.motor.getCurrent(CurrentUnit.MILLIAMPS)
//        val now = System.currentTimeMillis() / 1000.0
//
//        val isFull = Spindexer.isFull
//
//        // Define when spindexer needs feeding
//        val spindexerActive = Spindexer.isBusy
//
//        when (state) {
//
//            State.IDLE -> {
//                intake.power = 0.0
//                intakeRunning = false
//                intakeSpindexer = false
//                when {
//                    spindexerActive -> state = State.INTAKE_SLOW
//                    !isFull -> state = State.INTAKE_FAST
//                }
//            }
//
//            State.INTAKE_FAST -> {
//
//                if (isFull) {
//                    state = State.IDLE
//                    return
//                }
//
//                if (spindexerActive) {
//                    state = State.INTAKE_SLOW
//                    return
//                }
//
//                intake.power = -1.0
//                intakeRunning = true
//                intakeSpindexer = false
//
//                if (current > CURRENT_THRESHOLD_FAST) {
//                    jamStartTime = now
//                    state = State.JAM_CLEAR
//                }
//            }
//
//            State.INTAKE_SLOW -> {
//
//                if (!spindexerActive) {
//                    state = State.IDLE
//                    return
//                }
//
//                intake.power = -0.5
//                intakeRunning = false
//                intakeSpindexer = true
//
//                if (current > CURRENT_THRESHOLD_SLOW) {
//                    jamStartTime = now
//                    state = State.JAM_CLEAR
//                }
//            }
//
//            State.JAM_CLEAR -> {
//
//                intakeRunning = false
//                intakeSpindexer = false
//
//                val elapsed = now - jamStartTime
//
//                when {
//                    elapsed < 0.3 -> intake.power = 0.4
//                    else -> {
//                        intake.power = 0.0
//
//                        state = when {
//                            isFull -> State.IDLE
//                            spindexerActive -> State.INTAKE_SLOW
//                            else -> State.INTAKE_FAST
//                        }
//                    }
//                }
//            }
//
//            State.REVERSE -> {
//                intake.power = 0.7
//                intakeRunning = true
//                intakeSpindexer = false
//
//            }
//        }
//    }
//
//    fun stop() {
//        state = State.IDLE
//    }
//
//    fun reverse() {
//        state = State.REVERSE
//    }
//}
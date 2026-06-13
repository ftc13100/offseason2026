package org.firstinspires.ftc.teamcode.opModes.subsystems.shooter

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.util.ElapsedTime
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.controllable.RunToVelocity
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.VoltageCompensatingMotor

@Configurable
object Shooter : Subsystem {
    @JvmField var target = 0.0
    @JvmField var velPIDCoefficients = PIDCoefficients(0.001, 0.0, 0.0)
    @JvmField var basicFFParameters = BasicFeedforwardParameters(0.000385, 0.0, 0.061)

    val shooter = VoltageCompensatingMotor(
        MotorEx("shooter").brakeMode().reversed(),
        voltageCacheTimeSeconds = 0.5,
        nominalVoltage = 12.5
    )

    val SHOOTER_MAX_SPEED = 2200.0
    var manualOffset: Double = 0.0

    var shooterActive = false
    var shooterReady = false
    var shooterReadyMs: Double = 0.00
    private val runtime = ElapsedTime()

    val controller = controlSystem {
        velPid(velPIDCoefficients)
        basicFF(basicFFParameters)
    }

    override fun periodic() {
        if (shooterActive) {

            val currentVel = shooter.velocity
            val error = target - currentVel

            val motorPower =  if (error > 100.0) {
                1.0
            } else if (error < -100.0 ) {
                0.0
            } else {
                controller.calculate(shooter.state)
            }

            shooter.power = motorPower

        } else {
            shooter.power = 0.0
        }
    }

    fun spinAtSpeed(speed: Double) =
        InstantCommand {
            target = speed.coerceIn(0.0, SHOOTER_MAX_SPEED)
            shooterActive = true
            shooterReady = false
            shooterReadyMs = 0.00
            runtime.reset()
        }.then(
            RunToVelocity(controller, speed, 21.0),
            InstantCommand {
                shooterReady = true
                shooterReadyMs = runtime.milliseconds()
            }
        ).setInterruptible(true).requires(this)

    fun adjustSpeed(adj : Double) {
        spinAtSpeed(target + adj)()
    }

    val stallShooter = spinAtSpeed(1600.0)

    val stopShooter =
        InstantCommand {
            shooterActive = false
            shooterReady = false
            target = 0.0
        }.requires(this)


    fun spinning() {
        shooterActive = true
        controller.goal = KineticState(velocity = target)
        val motorPower = controller.calculate(
            KineticState(velocity = shooter.velocity)
        )
        shooter.power = motorPower
    }
}
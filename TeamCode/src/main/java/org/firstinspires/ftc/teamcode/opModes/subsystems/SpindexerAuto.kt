import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.controllable.RunToPosition
import dev.nextftc.hardware.impl.MotorEx

object SpindexerAuto : Subsystem {
    val spindexer = MotorEx("spindexer")

    @JvmField var target = 0.0
    @JvmField var posPIDCoefficients = PIDCoefficients(-0.0014, 0.0, -0.000035)



    val controlSystem = controlSystem {
        posPid(posPIDCoefficients)
    }

    val toShoot = RunToPosition(controlSystem, -9600.0, 100.0).requires(this)
    val toIntake = RunToPosition(controlSystem, 0.0, 50.0).requires(this)

    override fun periodic() {
        spindexer.power = controlSystem.calculate(spindexer.state)
    }
}
import vision.gears.webglmath.*
import kotlin.math.exp
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.atan2
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CarObject (
  val chassisMesh: Mesh,
  val wheelMesh: Mesh
) : GameObject(chassisMesh) {

  val width = 14f
  val length = 24.8f
  val originZ = 11f // the z component of the origin relative to the back wheels

  override var move = object: Motion(this) {
    val car = gameObject as CarObject
    val speed = 0.4f
    var curve = 0.0f

    // Move the car forward/backward applying steering as needed
    // (wheels have a radius of 2.5)
    fun moveCar(
      amount : Float
    ) {
      val s = PI.toFloat()*5f*amount // curve distance that the car moves
      if (curve == 0.0f) {
        car.position += Vec3(0f, 0f, s)
      }
      else {
        val baseDistance = abs(1/curve) + width/length/2f
        val origin2Base = originZ / length

        val circleOrigin = car.position + Vec3(baseDistance, 0f, origin2Base)

        val originDistance = sqrt( baseDistance*baseDistance + origin2Base*origin2Base)
        val C = originDistance * PI.toFloat()*s
        val deltaAngle = 2f*PI.toFloat()*s / C
        car.position += Vec3(
          originDistance * cos(deltaAngle), 
          0f, 
          originDistance * sin(deltaAngle))

        car.wheels.forEach {
            it.pitch += amount * PI.toFloat()*2.0f
        }
      }
    }

    // Turn the wheels left/right
    // If the wheels are already at their maximum
    // angle, don't rotate them anymore
    fun steer( amount: Float ){
      curve += amount
      var thetaLeft = 0.0f
      var thetaRight = 0.0f

      if (curve < 0) {
        thetaLeft = atan2(1f/curve, 1f) + PI.toFloat()/2f
        thetaRight = atan2(1f/curve + width/length, 1f) + PI.toFloat()/2.0f
      } else {
        thetaRight = atan2(1f/curve, 1f) - PI.toFloat()/2f
        thetaLeft = atan2(1f/curve + width/length, 1f) - PI.toFloat()/2.0f 
      }

      frontRightWheel.yaw = thetaRight
      frontLeftWheel.yaw = thetaLeft
    }

    // Move the car when keys are pressed
    override operator fun invoke(
      dt : Float,
      t : Float,
      keysPressed : Set<String>,
      gameObjects : List<GameObject>
    ) : Boolean {
      if ("W" in keysPressed) {
        moveCar(dt * speed)
      }
      else if ("S" in keysPressed) {
        moveCar(-dt * speed)
      }

      if ("A" in keysPressed) {
        steer(-dt)
      }
      else if ("D" in keysPressed) {
        steer(dt)
      }
      
      return true
    }
  }

  val frontRightWheel = GameObject(wheelMesh).also {
    it.position.set(-7f,-3.5f,13.8f)
    it.parent = this
  }
  val frontLeftWheel = GameObject(wheelMesh).also {
    it.position.set(7f,-3.5f,13.8f)
    it.parent = this
  }
  val backRightWheel = GameObject(wheelMesh).also {
    it.position.set(-7f,-3.5f,-11f)
    it.parent = this
  }
  val backLeftWheel = GameObject(wheelMesh).also {
    it.position.set(7f,-3.5f,-11f)
    it.parent = this
  }
  val wheels = arrayOf(
    frontLeftWheel, frontRightWheel,
    backLeftWheel, backRightWheel
  )

}




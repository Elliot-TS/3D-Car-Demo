import vision.gears.webglmath.*
import kotlin.math.exp
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.atan2
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.pow

class CarObject (
  val chassisMesh: Mesh,
  val wheelMesh: Mesh
) : GameObject(chassisMesh) {

  val width = 14f
  val length = 24.8f
  val originZ = 11f // the z component of the origin relative to the back wheels

  override var move = object: Motion(this) {
    val car = gameObject as CarObject
    val speed = 1.3f
    val steerSpeed = 2f
    var curve = 0.0f

    // Move the car forward/backward applying steering as needed
    // (wheels have a radius of 2.5)
    fun moveCar(
      amount : Float
    ) {
      val s = PI.toFloat()*5f*amount // curve distance that the car moves
      if (curve == 0.0f) {
        car.position += Vec3(0f, 0f, s)
        wheels.forEach {
            it.pitch += s / 5f
        }
      }
      else {
        val baseDistance = abs(length/curve) + width/2f
        val origin2Base = originZ

        console.log(baseDistance)
        val sign = -curve / abs(curve)
        val circleOrigin = Vec4(sign*baseDistance, 0f, -origin2Base, 1f) * car.modelMatrix
        //val circleOriginLeft = Vec4(0f, 30f, 0f, 1f)

        //val originDistance = circleOrigin.length()
        val originDistance = sqrt(baseDistance*baseDistance + origin2Base*origin2Base)
        val C = originDistance * PI.toFloat()*2f
        val deltaAngle = sign*2f*PI.toFloat()*s / C

        val rotMatrix = Mat4()
          .translate(-circleOrigin.xyz)
          .rotate(deltaAngle, 0f, 1f, 0f)
          .translate(circleOrigin.xyz)

        car.position.set((car.position.xyz1 * rotMatrix).xyz)
        car.yaw += deltaAngle

        // Rotate Wheels
        val tld = sqrt(baseDistance.pow(2) + length.pow(2)) * sign
        val trd = sqrt((baseDistance + width).pow(2) + length.pow(2)) * sign
        val bld = baseDistance * sign
        val brd = (baseDistance + width) * sign
        
        if (sign > 0) {
          frontLeftWheel.pitch += tld * deltaAngle / 5f
          frontRightWheel.pitch += trd * deltaAngle / 5f
          backLeftWheel.pitch += bld * deltaAngle / 5f
          backRightWheel.pitch += brd * deltaAngle / 5f
        }
        else {
          frontRightWheel.pitch += tld * deltaAngle / 5f
          frontLeftWheel.pitch += trd * deltaAngle / 5f
          backRightWheel.pitch += bld * deltaAngle / 5f
          backLeftWheel.pitch += brd * deltaAngle / 5f
        }
        
      }

    }

    // Turn the wheels left/right
    // If the wheels are already at their maximum
    // angle, don't rotate them anymore
    fun steer( amount: Float ){
      curve += amount
      if (curve >= 2f) curve = 2f

      if (curve <= -2f) curve = -2f

      var thetaLeft = atan2(1f/abs(curve), 1f) - PI.toFloat()/2f
      var thetaRight = atan2(1f/abs(curve) + width/length, 1f) - PI.toFloat()/2.0f

      if (curve < 0f) {
        thetaRight *= -1
        thetaLeft *= -1
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
        steer(-steerSpeed * dt)
      }
      else if ("D" in keysPressed) {
        steer(steerSpeed * dt)
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




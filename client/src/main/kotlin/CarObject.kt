import vision.gears.webglmath.*
import kotlin.math.exp
import kotlin.math.PI
import kotlin.math.floor

class CarObject (
  val chassisMesh: Mesh,
  val wheelMesh: Mesh
) : GameObject(chassisMesh) {

  override var move = object: Motion(this) {
    val car = gameObject as CarObject
    val speed = 0.4f

    // Move the car forward/backward applying steering as needed
    // (wheels have a radius of 2.5)
    fun moveCar(
      amount : Float
    ) {
      car.position += Vec3(0f, 0f, 5f*3.14f*amount)
      car.wheels.forEach {
          it.pitch += amount * 6.28f
      }
    }

    // Turn the wheels left/right
    // If the wheels are already at their maximum
    // angle, don't rotate them anymore
    fun steer( amount: Float )
    { }

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




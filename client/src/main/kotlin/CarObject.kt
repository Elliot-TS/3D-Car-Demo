import vision.gears.webglmath.*
import kotlin.math.exp
import kotlin.math.PI
import kotlin.math.floor

class CarObject (
  val chassisMesh: Mesh,
  val wheelMesh: Mesh
) : GameObject(chassisMesh) {

  override var move = object: Motion(this) {
    override operator fun invoke(
      dt : Float,
      t : Float,
      keysPressed : Set<String>,
      gameObjects : List<GameObject>
    ) : Boolean {
      // gameObject.roll += dt 
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




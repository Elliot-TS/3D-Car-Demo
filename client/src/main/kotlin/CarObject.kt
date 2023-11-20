import vision.gears.webglmath.*
import kotlin.math.exp
import kotlin.math.PI
import kotlin.math.floor

class CarObject (
	vararg val meshes : Mesh
) : GameObject(*meshes) {

	override var move = object: Motion(this) {
    override operator fun invoke(
        dt : Float = 0.016666f,
        t : Float = 0.0f,
        keysPressed : Set<String> = emptySet<String>(),
        gameObjects : List<GameObject> = emptyList<GameObject>()
        ) : Boolean {
      // gameObject.roll += dt 
      return true
    }

  val wheels = MutableList<GameObject>()

  init {
	  val vsTextured = Shader(gl, GL.VERTEX_SHADER, "textured-vs.glsl")
	  val fsSolid = Shader(gl, GL.FRAGMENT_SHADER, "solid-fs.glsl")
	  val solidProgram = Program(gl, vsTextured, fsSolid)

	  val wheelMaterial = Material(solidProgram).apply {
	    this["color"]?.set(Vec3(1.0f, 0.0f, 0.7f))
  	}
  
  }




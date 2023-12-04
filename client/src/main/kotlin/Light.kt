import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4

class Light(id : Int) : UniformProvider("lights[$id]") {

  val position by Vec4(0.0f, 1.0f, 0.0f, 0.0f) 
  val powerDensity by Vec3(0.0f, 0.0f, 0.0f)
  val direction by Vec4(0f, -1f, 0f, 0f)
  val angle by Vec3(6.28f, 0f, 0f)

}

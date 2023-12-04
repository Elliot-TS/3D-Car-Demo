import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL //# GL# we need this for the constants declared ˙HUN˙ a constansok miatt kell
import kotlin.js.Date
import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec1
import vision.gears.webglmath.Vec2
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4
import vision.gears.webglmath.Mat4
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos

class Scene (
    val gl : WebGL2RenderingContext
)  : UniformProvider("scene") {


    // Vertex Shaders
    val vsTextured = Shader(gl, GL.VERTEX_SHADER, "textured-vs.glsl")
    val vsQuad = Shader(gl, GL.VERTEX_SHADER, "quad-vs.glsl")

    // Fragment Shaders
    val fsBackground = Shader(gl, GL.FRAGMENT_SHADER, "background-fs.glsl")
    val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")

    // Programs
    val texturedProgram = Program(gl, vsTextured, fsTextured)
    val backgroundProgram = Program(gl, vsQuad, fsBackground)

    // Textures
    val skyCubeTexture = TextureCube(
        gl,
        "media/posx512.jpg", "media/negx512.jpg",
        "media/posy512.jpg", "media/negy512.jpg",
        "media/posz512.jpg", "media/negz512.jpg"
    )  
    val chevyTexture = Texture2D(gl, "media/chevy/chevy.png")
    val roadTexture = Texture2D(gl, "media/road.jpg")

    // Materials
    val backgroundMaterial = Material(backgroundProgram).apply{
        this["envTexture"]?.set( skyCubeTexture )
    }
    val chevyMaterial = Material(texturedProgram).apply {
        this["colorTexture"]?.set(chevyTexture)
        this["envTexture"]?.set( skyCubeTexture )
    }
    val groundMaterial = Material(texturedProgram).apply {
        this["colorTexture"]?.set(roadTexture)
        this["envTexture"]?.set( skyCubeTexture )
    }

    // Geometry
    val jsonLoader = JsonLoader()
    val texturedQuadGeometry = TexturedQuadGeometry(gl)
    val groundGeometry = TexturedQuadGeometry(gl, 40f)
    val chevyChassisGeometries = jsonLoader.loadGeometries(gl, "media/chevy/chassis.json")
    val wheelGeometries = jsonLoader.loadGeometries( gl, "media/chevy/wheel.json")

    // Mesh
    val backgroundMesh = Mesh(backgroundMaterial, texturedQuadGeometry)
    val chevyChassisMesh = Mesh(chevyMaterial, chevyChassisGeometries[0])
    val wheelMesh = Mesh(chevyMaterial, wheelGeometries[0])
    val groundMesh = Mesh(groundMaterial, groundGeometry)

    // Lights
    val lights = Array<Light>(3) { Light(it) }
    init{
        // Environment Light
        lights[1].position.set(0f, 0f, 0f, 0f)
        lights[1].direction.set(0f, 0f, 0f, 0f)
        lights[1].powerDensity.set(0.4f, 0.35f, 0.3f)

        // Directional Light
        lights[0].position.set(1.0f, 1.0f, 1.0f, 0f).normalize()
        lights[0].direction.set(-1f, 1f, -1f, 1f).normalize()
        lights[0].powerDensity.set(10f, 1f, 0.95f)

        // Positional Light
        lights[2].position.set(0f, -5.6f, 20f, 1f)
        lights[2].powerDensity.set(100f, 100f, 1000f)
    }

    // Game Objects
    val gameObjects = ArrayList<GameObject>()

    val avatar = CarObject(chevyChassisMesh, wheelMesh).apply{
        position.set(40f, 0f, 40f)
        scale.set(1f, 1f, 1f)
    }
    init {
        // LABTODO: create and add game object using meshes loaded from JSON
        gameObjects += avatar
        gameObjects.addAll(avatar.wheels)
        gameObjects += GameObject(backgroundMesh)
        gameObjects += GameObject(groundMesh).apply {
            position.set(0f, -6f, 0f)
            scale.set(1000f, 1000f, 1f)
            pitch = 3.14f / 2f
        }
    }

    // LABTODO: replace with 3D camera
    val camera = PerspectiveCamera().apply{
        position.set(30f, 10f, 20f)
        pitch = -PI.toFloat() / 2f
        yaw = PI.toFloat()
        update()
    }

    fun resize(canvas : HTMLCanvasElement) {
        gl.viewport(0, 0, canvas.width, canvas.height)//#viewport# tell the rasterizer which part of the canvas to draw to ˙HUN˙ a raszterizáló ide rajzoljon
        camera.setAspectRatio(canvas.width.toFloat()/canvas.height)
    }

    val timeAtFirstFrame = Date().getTime()
    var timeAtLastFrame =  timeAtFirstFrame

    init{
        //LABTODO: enable depth test
        gl.enable(GL.DEPTH_TEST)
        addComponentsAndGatherUniforms()
    }

    @Suppress("UNUSED_PARAMETER")
    fun update(keysPressed : Set<String>) {
        val timeAtThisFrame = Date().getTime() 
        val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
        val t = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f
        timeAtLastFrame = timeAtThisFrame

        //LABTODO: move camera
        camera.move(dt, keysPressed)
        // lights[0].position.set(sin(t), cos(t), cos(2f*t), 0f).normalize()

        gl.clearColor(0.3f, 0.0f, 0.3f, 1.0f)//## red, green, blue, alpha in [0, 1]
        gl.clearDepth(1.0f)//## will be useful in 3D ˙HUN˙ 3D-ben lesz hasznos
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)//#or# bitwise OR of flags

        gl.enable(GL.BLEND)
        gl.blendFunc(
            GL.SRC_ALPHA,
            GL.ONE_MINUS_SRC_ALPHA)

            gameObjects.forEach{ it.move(dt, t, keysPressed, gameObjects) }

            gameObjects.forEach{ it.update() }
            gameObjects.forEach{ it.draw(this, camera, *lights) }
        }
    }

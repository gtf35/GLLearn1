package top.gtf35.gllearn1

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val glSurfaceView = glsv
        // 设置RGBA颜色缓冲、深度缓冲及stencil缓冲大小
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        // 设置GL版本，这里设置为2.0
        glSurfaceView.setEGLContextClientVersion(2)
        // 给GLSurfaceView设置Renderer，
        // 这个Renderer就是用于做渲染的，
        // 可以把GLSurfaceView理解成就是一块画板，
        // 具体怎么画，是在Renderer里做的
        glSurfaceView.setRenderer(SampleHelloWorld())
    }

    inner class SampleHelloWorld : GLSurfaceView.Renderer {
        private val vertexShaderCode =
                "precision mediump float;\n" +
                        "attribute vec4 a_Position;\n" +
                        "void main() {\n" +
                        "    gl_Position = a_Position;\n" +
                        "}"

        private val fragmentShaderCode =
                "precision mediump float;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);\n" +
                        "}"

        private var glSurfaceViewWidth: Int = 0
        private var glSurfaceViewHeight: Int = 0

        /**
         * 渲染时的回调，我们的渲染逻辑就是在这里面写
         */
        override fun onDrawFrame(gl: GL10?) {
            // 设置清屏颜色
            GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1f)

            // 清屏
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            // 设置视口，这里设置为整个GLSurfaceView区域
            GLES20.glViewport(0, 0, glSurfaceViewWidth, glSurfaceViewHeight)

            // 调用draw方法用TRIANGLES的方式执行渲染，顶点数量为3个
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        }

        /**
         * 在GLSurfaceView宽高改变时会回调，一般可以在这里记录最新的宽高
         */
        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            // 记录GLSurfaceView的宽高
            glSurfaceViewWidth = width
            glSurfaceViewHeight = height
        }

        /**
         * 在GLSurfaceView创建好时会回调，一般可以在里面写一些初始化逻辑
         */
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            // 创建GL程序
            val programId = GLES20.glCreateProgram()
            // 加载、编译vertex shader和fragment shader
            val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            val fragmentShader= GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            GLES20.glShaderSource(vertexShader, vertexShaderCode)
            GLES20.glShaderSource(fragmentShader, fragmentShaderCode)
            GLES20.glCompileShader(vertexShader)
            GLES20.glCompileShader(fragmentShader)
            // 将shader程序附着到GL程序上
            GLES20.glAttachShader(programId, vertexShader)
            GLES20.glAttachShader(programId, fragmentShader)
            // 链接GL程序
            GLES20.glLinkProgram(programId)
            // 应用GL程序
            GLES20.glUseProgram(programId)
            // 三角形顶点数据
            val vertexData = floatArrayOf(0f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f)
            // 将三角形顶点数据放入buffer中
            val buffer = ByteBuffer.allocateDirect(vertexData.size * java.lang.Float.SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
            buffer.put(vertexData)
            buffer.position(0)
            // 获取字段a_Position在shader中的位置
            val location = GLES20.glGetAttribLocation(programId, "a_Position")
            // 启动对应位置的参数
            GLES20.glEnableVertexAttribArray(location)
            // 指定a_Position所使用的顶点数据
            GLES20.glVertexAttribPointer(location, 2, GLES20.GL_FLOAT, false,0, buffer)

        }

    }

}
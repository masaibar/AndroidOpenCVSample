package com.masaibar.androidopencvsample

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val FILE_NAME = "haarcascade_frontalface_alt.xml"
    }

    private val onLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            super.onManagerConnected(status)

            val file = File(filesDir.path + File.separator + FILE_NAME)
            if (!file.exists()) {
                try {
                    val inputStream = assets.open(FILE_NAME)
                    val fileOutputStream = FileOutputStream(file, false)
                    val buffer = ByteArray(1024)
                    var read: Int
                    do {
                        read = inputStream.read(buffer)
                        if (read == -1) {
                            break
                        }
                        fileOutputStream.write(buffer, 0, read)
                    } while (true)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val cascadeClassifier = CascadeClassifier(file.absolutePath)

            try {
                val inputStream = assets.open("okawa_reiwa.jpg")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val mat = Mat()
                Utils.bitmapToMat(bitmap, mat)

                val matOfRect = MatOfRect()
                cascadeClassifier.detectMultiScale(mat, matOfRect)

                matOfRect.toArray().forEach { rect ->
                    Imgproc.rectangle(
                        mat,
                        Point(rect.x.toDouble(), rect.y.toDouble()),
                        Point(rect.x + rect.width.toDouble(), rect.y + rect.height.toDouble()),
                        Scalar(0.0, 255.0, 0.0),
                        5
                    )
                }
                Utils.matToBitmap(mat, bitmap)

                image.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(OpenCVLoader.initDebug()) {
            System.loadLibrary("opencv_java4")
            onLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }
}

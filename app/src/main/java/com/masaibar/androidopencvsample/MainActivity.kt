package com.masaibar.androidopencvsample

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val onLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            super.onManagerConnected(status)

            try {
                val inputStream = assets.open("okawa_reiwa.jpg")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val mat = Mat()
                Utils.bitmapToMat(bitmap, mat)

                Imgproc.resize(
                    mat,
                    mat,
                    Size(
                        0.0,
                        0.0
                    ),
                    0.05,
                    0.05,
                    Imgproc.INTER_AREA
                )
                Imgproc.resize(
                    mat,
                    mat,
                    Size(
                        0.0,
                        0.0
                    ),
                    20.0,
                    20.0,
                    Imgproc.INTER_AREA
                )

                val dstBitmap = Bitmap.createBitmap(
                    mat.size().width.toInt(),
                    mat.size().height.toInt(),
                    Bitmap.Config.ARGB_8888
                )
                Utils.matToBitmap(mat, dstBitmap)

                image.setImageBitmap(dstBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (OpenCVLoader.initDebug()) {
            System.loadLibrary("opencv_java4")
            onLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }
}

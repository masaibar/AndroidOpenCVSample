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
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.IOException

class MainActivity : AppCompatActivity() {
    companion object {
        private const val MOSAIC_LEVEL = 10.0
        private const val K = 64
    }

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
                    1.0 / MOSAIC_LEVEL,
                    1.0 / MOSAIC_LEVEL,
                    Imgproc.INTER_AREA
                )
                Imgproc.resize(
                    mat,
                    mat,
                    Size(
                        0.0,
                        0.0
                    ),
                    MOSAIC_LEVEL,
                    MOSAIC_LEVEL,
                    Imgproc.INTER_AREA
                )

                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGRA2BGR)

                val samples = mat.reshape(1, mat.cols() * mat.rows())
                val samples32f = Mat()
                samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0)

                val labels = Mat()
                val criteria = TermCriteria(TermCriteria.COUNT, 100, 1.0)
                val centers = Mat()
                Core.kmeans(samples32f, K, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers)

                centers.convertTo(centers, CvType.CV_8UC1, 255.0)
                centers.reshape(3)

                var dst = mat.clone()
                var rows = 0
                var y = 0
                while (y < mat.rows()) {
                    var x = 0
                    while (x < mat.cols()) {
                        val label = labels.get(rows, 0)[0].toInt()
                        val r = centers.get(label, 2)[0]
                        val g = centers.get(label, 1)[0]
                        val b = centers.get(label, 0)[0]
                        dst.put(y, x, b, g, r)
                        rows += 1
                        x += 1
                    }
                    y += 1
                }
                val dstBitmap = Bitmap.createBitmap(
                    dst.size().width.toInt(),
                    dst.size().height.toInt(),
                    Bitmap.Config.ARGB_8888
                )
                Utils.matToBitmap(dst, dstBitmap)
                image.setImageBitmap(dstBitmap)

//                Imgproc.resize(
//                    mat,
//                    mat,
//                    Size(
//                        0.0,
//                        0.0
//                    ),
//                    0.05,
//                    0.05,
//                    Imgproc.INTER_AREA
//                )
//                Imgproc.resize(
//                    mat,
//                    mat,
//                    Size(
//                        0.0,
//                        0.0
//                    ),
//                    20.0,
//                    20.0,
//                    Imgproc.INTER_AREA
//                )
//
//                val dstBitmap = Bitmap.createBitmap(
//                    mat.size().width.toInt(),
//                    mat.size().height.toInt(),
//                    Bitmap.Config.ARGB_8888
//                )
//                Utils.matToBitmap(mat, dstBitmap)
//
//                image.setImageBitmap(dstBitmap)
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

package com.example.tensorflowcnn.tflite

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.tensorflowcnn.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

object ImageClassifier {
    const val imageSize = 32
    private const val CONFIDENCE_THRESHOLD = 0f // random confidence threshold for now
    private val classes = arrayOf("Pineapple", "Apple", "Banana", "Orange")

    @Composable
    fun Classify(image: Bitmap, callback: (@Composable (String) -> Unit)) {
        val context = LocalContext.current
        val model = Model.newInstance(context)
        val byteBuffer = prepareImage(image)
        val confidences = runInference(model, byteBuffer)
        val classification = getClassification(confidences)

        callback.invoke(classification)

        model.close() // Ensure to release resources
    }

    private fun prepareImage(image: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3) // Float size * image size
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(imageSize * imageSize)
        image.getPixels(pixels, 0, image.width, 0, 0, image.width, image.height)
        pixels.forEach { pixel ->
            val r = (pixel shr 16 and 0xFF) * (1f / 1)
            val g = (pixel shr 8 and 0xFF) * (1f / 1)
            val b = (pixel and 0xFF) * (1f / 1)
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }
        return byteBuffer
    }

    private fun runInference(model: Model, byteBuffer: ByteBuffer): FloatArray {
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        return outputs.outputFeature0AsTensorBuffer.floatArray
    }

    private fun getClassification(confidences: FloatArray): String {
        var maxPos = 0
        var maxConfidence = 0f
        confidences.forEachIndexed { index, confidence ->
            if (confidence > maxConfidence) {
                maxConfidence = confidence
                maxPos = index
            }
        }

        Log.d("ImageClassifier", "Confidence: $maxConfidence")

        return if (maxConfidence >= CONFIDENCE_THRESHOLD) {
            classes[maxPos]
        } else {
            "Unclassified"
        }
    }
}


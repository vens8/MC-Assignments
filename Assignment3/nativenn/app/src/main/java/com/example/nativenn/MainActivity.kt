package com.example.nativenn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.TextView
import com.example.nativenn.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var resultTextView: TextView

    companion object {
        lateinit var instance: MainActivity
            private set
        init {
            System.loadLibrary("nativenn")
        }

        private const val REQUEST_CODE_GALLERY = 123
        private const val TAG = "NativeNNActivity"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultTextView = findViewById(R.id.resultTextView)
        binding.buttonChooseImages.setOnClickListener { openGallery() }
    }

    fun displayClassificationResult(predictedClass: Int) {
        Log.d(TAG, "Predicted class: $predictedClass")
        // Display the classification result
        resultTextView.text = "Predicted class: $predictedClass"
    }
    private fun openGallery() {
        Log.d(TAG, "Opening gallery")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            Log.d(TAG, "Image selected from gallery")
            Log.d(TAG, "data: $data")
            Log.d(TAG, "data?.clipData: ${data?.clipData}")
            // Check if there are multiple images selected
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    classifyImages(imageUri)
                    Log.d(TAG, "local classify images called")
                }
            } ?: run {
                // Single image selected
                data?.data?.let { imageUri ->
                    classifyImages(imageUri)
                    Log.d(TAG, "local classify images called")
                }
            }
        }
    }

    private fun classifyImages(imageUri: Uri?) {
        Log.d(TAG, "local classifyImages called")
        imageUri?.let { uri ->
            // Load the image from the URI
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Preprocess the image
                val inputTensor = preprocessImage(bitmap)

                // Call the native code to classify the image
                classifyImages(inputTensor)
                Log.d(TAG, "native classifyImages called")
            }
        }
    }
    private fun preprocessImage(bitmap: Bitmap): IntArray {
        Log.d(TAG, "preprocessImage called")
        val IMAGE_SIZE = 28
        val NUM_CHANNELS = 1
        // Resize the image to the expected input size (28x28 in this case)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)

        // Convert the bitmap to a float array
        val inputTensor = IntArray(IMAGE_SIZE * IMAGE_SIZE * NUM_CHANNELS)
        resizedBitmap.getPixels(inputTensor, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE)

        // Normalize the input tensor (optional, depending on your model requirements)
        inputTensor.forEachIndexed { index, pixel ->
            inputTensor[index] = ((pixel.toFloat() - 127.5f) / 127.5f).toInt()
        }

        return inputTensor
    }
    private external fun classifyImages(inputTensor: IntArray)
}
package com.aadityaverma.deepfakedetection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.documentfile.provider.DocumentFile
import com.aadityaverma.deepfakedetection.data.api.ApiService
import com.aadityaverma.deepfakedetection.data.api.DetectionResponse
import com.aadityaverma.deepfakedetection.di.AppModule
import com.aadityaverma.deepfakedetection.presentation.navgraph.Navigation
import com.aadityaverma.deepfakedetection.ui.theme.DeepFakeDetectionTheme
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    @Inject
//    lateinit var apiService: ApiService

//    private lateinit var pickVideoLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

//        pickVideoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                result.data?.data?.let { uri ->
//                    Log.d("MainActivity", "Selected video URI: $uri")
//                    uploadVideo(uri)
//                }
//            }
//        }

        setContent {
            DeepFakeDetectionTheme {
                Navigation()
            }
        }

//        pickVideoFromGallery()
    }

//    private fun pickVideoFromGallery() {
//        val intent = Intent(Intent.ACTION_PICK).apply {
//            type = "video/*"
//        }
//        pickVideoLauncher.launch(intent)
//    }
//
//    private fun uploadVideo(fileUri: Uri) {
//        val file = getFileFromUri(fileUri)
//        val requestFile = RequestBody.create("video/mp4".toMediaTypeOrNull(), file)
//        val body = MultipartBody.Part.createFormData("video", file.name, requestFile)
//
//        apiService.uploadVideo(body).enqueue(object : Callback<DetectionResponse> {
//            override fun onResponse(call: Call<DetectionResponse>, response: Response<DetectionResponse>) {
//                if (response.isSuccessful) {
//                    val detectionResult = response.body()
//                    if (detectionResult != null) {
//                        val output = detectionResult.output
//                        val confidence = detectionResult.confidence
//                        Log.d("MainActivity", "Detection result: $output, Confidence: $confidence")
//                    } else {
//                        Log.e("MainActivity", "Detection result is null")
//                    }
//                } else {
//                    Log.e("MainActivity", "Upload failed with response code: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<DetectionResponse>, t: Throwable) {
//                Log.e("MainActivity", "Upload failed", t)
//            }
//        })
//    }
//
//    private fun getFileFromUri(uri: Uri): File {
//        val inputStream: InputStream? = contentResolver.openInputStream(uri)
//        val file = File(cacheDir, DocumentFile.fromSingleUri(this, uri)?.name ?: "tempFile.mp4")
//        inputStream?.use { input ->
//            file.outputStream().use { output ->
//                input.copyTo(output)
//            }
//        }
//        return file
//    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeepFakeDetectionTheme {
        Greeting("Android")
    }
}
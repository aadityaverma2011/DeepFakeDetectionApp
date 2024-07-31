@file:OptIn(ExperimentalFoundationApi::class)

package com.aadityaverma.deepfakedetection.presentation.components

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width


import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.aadityaverma.deepfakedetection.R
import com.aadityaverma.deepfakedetection.data.api.ApiService
import com.aadityaverma.deepfakedetection.data.api.DetectionResponse
import com.robertlevonyan.compose.picker.ItemModel
import com.robertlevonyan.compose.picker.ItemType
import com.robertlevonyan.compose.picker.PickerDialog
import com.robertlevonyan.compose.picker.ShapeType
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream


@androidx.annotation.OptIn(UnstableApi::class)


@Composable
fun DetectScreen(navController: NavController, apiService: ApiService) {
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    var playerView by remember { mutableStateOf<PlayerView?>(null) }
    var detectionResult by remember { mutableStateOf<String?>(null) }
    var confidence by remember { mutableStateOf<Float?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedUri = it
        }
    }

    // Effect to handle player re-initialization when selectedUri changes
    LaunchedEffect(selectedUri) {
        selectedUri?.let { uri ->
            player?.release() // Release previous player instance
            player = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(uri))
                prepare()
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("DetectScreen", "Player error: ${error.message}")
                    }

                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_READY -> Log.d("DetectScreen", "Player is ready")
                            Player.STATE_BUFFERING -> Log.d("DetectScreen", "Player is buffering")
                            Player.STATE_ENDED -> Log.d("DetectScreen", "Playback ended")
                            Player.STATE_IDLE -> Log.d("DetectScreen", "Player is idle")
                        }
                    }
                })
            }
            playerView?.player = player
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        // Video Preview
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            factory = { context ->
                PlayerView(context).apply {
                    playerView = this
                    this.player = player
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                launcher.launch("video/*") // Use media type "video/*" to filter video files
            }) {
                Text(text = "Browse")
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(onClick = {
                selectedUri?.let { uri ->
                    uploadVideo(
                        context = context,
                        fileUri = uri,
                        apiService = apiService
                    ) { result, confidenceValue ->
                        detectionResult = result
                        confidence = confidenceValue
                    }
                }
            }) {
                Text(text = "Predict")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = detectionResult ?: "No result yet",
            fontSize = 18.sp,
            color = if (detectionResult == "REAL") Color.Green else Color.Red,
            modifier = Modifier.align(CenterHorizontally)
        )
        // Detection Result Box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            color = Color.Gray.copy(alpha = 0.2f),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(
                    progress = confidence?.div(100f) ?: 0f,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    color = if (confidence ?: 0f > 0.5f) Color.Green else Color.Red
                )

            }
        }
    }
}

private fun uploadVideo(context: Context, fileUri: Uri, apiService: ApiService, onResult: (String, Float) -> Unit) {
    val file = getFileFromUri(context, fileUri)
    val requestFile = RequestBody.create("video/mp4".toMediaTypeOrNull(), file)
    val body = MultipartBody.Part.createFormData("video", file.name, requestFile)

    apiService.uploadVideo(body).enqueue(object : Callback<DetectionResponse> {
        override fun onResponse(call: Call<DetectionResponse>, response: Response<DetectionResponse>) {
            if (response.isSuccessful) {
                val detectionResult = response.body()
                if (detectionResult != null) {
                    val output = detectionResult.output
                    val confidence = detectionResult.confidence
                    onResult(output, confidence.toFloat())
                    Log.d("DetectScreen", "Detection result: $output, Confidence: $confidence")
                } else {
                    onResult("No result", 0f)
                    Log.e("DetectScreen", "Detection result is null")
                }
            } else {
                onResult("Upload failed", 0f)
                Log.e("DetectScreen", "Upload failed with response code: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<DetectionResponse>, t: Throwable) {
            onResult("Upload failed", 0f)
            Log.e("DetectScreen", "Upload failed", t)
        }
    })
}

private fun getFileFromUri(context: Context, uri: Uri): File {
    val contentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "tempFile.mp4")
    inputStream?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return file
}
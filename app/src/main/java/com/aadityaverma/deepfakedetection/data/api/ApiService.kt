package com.aadityaverma.deepfakedetection.data.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/Detect")
    fun uploadVideo(
        @Part video: MultipartBody.Part
    ): Call<DetectionResponse>
}

data class DetectionResponse(
    val output: String,
    val confidence: Double
)
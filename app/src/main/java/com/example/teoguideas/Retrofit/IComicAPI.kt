package com.example.teoguideas.Retrofit

import com.example.teoguideas.Model.CentroHistorico
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.Part


interface IComicAPI {
    @get:GET("centro")
    val CHistoList:Observable<List<CentroHistorico>>

    @get:GET("buscarFoto")
    val PerfilList:Observable<List<CentroHistorico>>

    @get:POST("upload")
    val SubirImagen:Observable<List<CentroHistorico>>

    @Multipart
    @POST("/upload")
    fun postImage(@Part image: MultipartBody.Part, @Part("upload") name: RequestBody): Call<ResponseBody>
}
package com.example.minlynative;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitInterface {
    @Multipart
    @POST("/api/upload")
    Call<Response> uploadImage(@Part MultipartBody.Part image);

    @GET("/")
    Call<List<String>> getImages();


}
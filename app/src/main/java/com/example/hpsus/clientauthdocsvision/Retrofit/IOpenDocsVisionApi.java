package com.example.hpsus.clientauthdocsvision.Retrofit;

import com.example.hpsus.clientauthdocsvision.Model.SyncMessageAuthResult;
import com.example.hpsus.clientauthdocsvision.Model.SyncMessageResult;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IOpenDocsVisionApi {

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("GetMessages")
    Observable<List<SyncMessageResult>> getSyncMessage(@Body RequestBody message);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("GetAuthMessages")
    Observable<List<SyncMessageAuthResult>> getSyncMessageAuth(@Body RequestBody message);
}

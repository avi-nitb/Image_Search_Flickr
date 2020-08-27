package com.imagesearch.networks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface FlickerSearchApi {

    @GET("rest/")
    @Headers("Content-Type: application/json")
    Call<JsonElement>getSearchResult(@Query("method") String method, @Query("api_key") String apiKey, @Query("tags") String searchTag,
                                     @Query("format") String format, @Query("per_page") int pageSize, @Query("page") int page);
}

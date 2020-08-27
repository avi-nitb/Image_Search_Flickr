package com.imagesearch.networks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imagesearch.converter.GsonPConverterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClientInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://www.flickr.com/services/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(new GsonPConverterFactory(gson))
                    .build();
        }
        return retrofit;
    }
}

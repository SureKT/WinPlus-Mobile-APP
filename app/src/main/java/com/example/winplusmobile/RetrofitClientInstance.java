package com.example.winplusmobile;

import com.example.winplusmobile.Entities.Resp;
import com.example.winplusmobile.Entities.TUsuario;
import com.example.winplusmobile.Interfaces.WPMobile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClientInstance {
    private static String API_BASE_URL = "http://192.168.0.7:5000";
//    private static String API_BASE_URL = "http://192.168.43.210:5000";
    private static Retrofit retrofit;
    private static Gson gson;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}

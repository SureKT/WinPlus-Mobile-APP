package com.example.winplusmobile.Interfaces;

import com.example.winplusmobile.Entities.Causas;
import com.example.winplusmobile.Entities.Config;
import com.example.winplusmobile.Entities.Fichaje;
import com.example.winplusmobile.Entities.Personal;
import com.example.winplusmobile.Entities.Resp;
import com.example.winplusmobile.Entities.VContadores;
import com.example.winplusmobile.Entities.VFichajes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WPMobile {
    @FormUrlEncoded
    @POST("api/TUsuarios/validar")
    public Call<Resp> validaUsuario(@Field("token") String token);

    @FormUrlEncoded
    @POST("api/TUsuarios")
    public Call<Resp> loginUsuario(@Field("login") String login, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/Personal")
    public Call<Personal> getPersonal(@Field("token") String token);

    @FormUrlEncoded
    @POST("/api/Personal/foto")
    public Call<String> getFoto(@Field("token") String token);

    @GET("/api/Causas")
    public Call<List<Causas>> getCausas();

    @POST("/api/Fichajes/{token}")
    public Call<Resp> postFichaje(@Path("token") String token, @Body Fichaje fichaje);

    @FormUrlEncoded
    @POST("/api/VFichajes")
    public Call<List<VFichajes>> getVFichajes(@Field("token") String token, @Field("inicio") String inicio, @Field("fin") String fin);

    @FormUrlEncoded
    @POST("/api/VContadores")
    public Call<List<VContadores>> getContadores(@Field("token") String token, @Field("inicio") String inicio, @Field("fin") String fin);

    @FormUrlEncoded
    @POST("/api/Config")
    public Call<Config> getConfig(@Field("clave") String clave);
}

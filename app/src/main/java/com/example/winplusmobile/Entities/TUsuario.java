package com.example.winplusmobile.Entities;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.winplusmobile.Interfaces.WPMobile;
import com.example.winplusmobile.RetrofitClientInstance;

import java.time.LocalDate;
import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TUsuario extends Application {

    private String Nombre;
    private String Login;
    private String Password;
    private byte Nivel;
    private String Permisos;
    private String Email;
    private byte Bloqueado;
    private String Dominio;
    private String Personal;
    private byte Responsable;
    private String Avatar;
    private byte Estado;
    private String Cdesbloqueo;
    private LocalDate Fdesbloqueo;
    private short Nreintentos;
    private byte Visitas;
    private static String Token;

    public static boolean tokenValido;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void validarToken() {
        Retrofit retrofit = new RetrofitClientInstance().getRetrofitInstance();

        WPMobile service = retrofit.create(WPMobile.class);
        Call<Resp> call = service.validaUsuario(getToken());
        call.enqueue(new Callback<Resp>() {
            @Override
            public void onResponse(Call<Resp> call, Response<Resp> response) {
                Resp resp = response.body();

                if (resp.codigo == 0) {
                    tokenValido = true;
                } else {
                    tokenValido = false;
                }
            }

            @Override
            public void onFailure(Call<Resp> call, Throwable t) {
                tokenValido = false;
            }
        });
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public byte getNivel() {
        return Nivel;
    }

    public void setNivel(byte nivel) {
        Nivel = nivel;
    }

    public String getPermisos() {
        return Permisos;
    }

    public void setPermisos(String permisos) {
        Permisos = permisos;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public byte getBloqueado() {
        return Bloqueado;
    }

    public void setBloqueado(byte bloqueado) {
        Bloqueado = bloqueado;
    }

    public String getDominio() {
        return Dominio;
    }

    public void setDominio(String dominio) {
        Dominio = dominio;
    }

    public String getPersonal() {
        return Personal;
    }

    public void setPersonal(String personal) {
        Personal = personal;
    }

    public byte getResponsable() {
        return Responsable;
    }

    public void setResponsable(byte responsable) {
        Responsable = responsable;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public byte getEstado() {
        return Estado;
    }

    public void setEstado(byte estado) {
        Estado = estado;
    }

    public String getCdesbloqueo() {
        return Cdesbloqueo;
    }

    public void setCdesbloqueo(String cdesbloqueo) {
        Cdesbloqueo = cdesbloqueo;
    }

    public LocalDate getFdesbloqueo() {
        return Fdesbloqueo;
    }

    public void setFdesbloqueo(LocalDate fdesbloqueo) {
        Fdesbloqueo = fdesbloqueo;
    }

    public short getNreintentos() {
        return Nreintentos;
    }

    public void setNreintentos(short nreintentos) {
        Nreintentos = nreintentos;
    }

    public byte getVisitas() {
        return Visitas;
    }

    public void setVisitas(byte visitas) {
        Visitas = visitas;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public boolean tokenValido() {
        return tokenValido;
    }

    public static void setTokenValido(boolean tokenValido) {
        TUsuario.tokenValido = tokenValido;
    }
}

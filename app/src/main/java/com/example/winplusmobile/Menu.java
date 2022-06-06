package com.example.winplusmobile;

import static com.example.winplusmobile.R.drawable.borde_foto;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.winplusmobile.Entities.Personal;
import com.example.winplusmobile.Entities.TUsuario;
import com.example.winplusmobile.Interfaces.WPMobile;

import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Menu extends AppCompatActivity {

    TUsuario tUsuario;
    Personal personal;
    TextView codUsuario, nombre, apellidos;
    ImageView fotoUsuario;

    MediaPlayer error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        codUsuario = findViewById(R.id.codUsuario);
        nombre = findViewById(R.id.nombre);
        apellidos = findViewById(R.id.apellidos);

        fotoUsuario = findViewById(R.id.fotoUsuario);

        error = MediaPlayer.create(this, R.raw.efecto_error);

        tUsuario = (TUsuario) getApplicationContext();

        getPersonal();
        getFoto();
    }

    private void getPersonal() {
        Retrofit retrofit = new RetrofitClientInstance().getRetrofitInstance();

        WPMobile service = retrofit.create(WPMobile.class);
        Call<Personal> call = service.getPersonal(tUsuario.getToken());
        call.enqueue(new Callback<Personal>() {
            @Override
            public void onResponse(Call<Personal> call, Response<Personal> response) {
                if (response.isSuccessful()) {
                    personal = response.body();
                    codUsuario.setText(personal.codigo);
                    nombre.setText(personal.nombre);
                    apellidos.setText(personal.apellido1 + " " + personal.apellido2);
                }
            }

            @Override
            public void onFailure(Call<Personal> call, Throwable t) {
                Log.e("TAG", t.toString());
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "No es posible conectarse al servedor, prueba más tarde", Toast.LENGTH_LONG).show();
                error.start();
            }
        });
    }

    public void getFoto() {
        Retrofit retrofit = new RetrofitClientInstance().getRetrofitInstance();

        WPMobile service = retrofit.create(WPMobile.class);
        Call<String> call = service.getFoto(tUsuario.getToken());
        call.enqueue(new Callback<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String fotoString = response.body();
                    if (fotoString.equalsIgnoreCase("error")) {
                        return;
                    }
                    byte[] foto = Base64.getDecoder().decode(fotoString.getBytes());
                    fotoUsuario.setImageBitmap(BitmapFactory.decodeByteArray(foto, 0, foto.length));
                    //TODO borde de la imagen
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("TAG", t.toString());
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "No es posible recuperar la foto de usuario", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void volverMenu(View view) {
//        validarToken();
        Intent login = new Intent(Menu.this, Login.class);
        startActivity(login);
    }

    public void openFichar(View view) {
//        validarToken();
        Intent fichar = new Intent(Menu.this, Fichar.class);
        startActivity(fichar);
    }

    public void openFichajes(View view) {
//        validarToken();
        Intent fichajes = new Intent(Menu.this, Fichajes.class);
        startActivity(fichajes);
    }

    public void openAcumulados(View view) {
//        validarToken();
        Intent acumulados = new Intent(Menu.this, Acumulados.class);
        startActivity(acumulados);
    }

    public void openOpciones(View view) {
//        validarToken();
//        Intent opciones = new Intent(Menu.this, Opciones.class);
//        startActivity(opciones);
    }

    public void salir(View view) {
        finishAffinity();
    }

    private void validarToken() {
        tUsuario.validarToken();
        if (!tUsuario.tokenValido()) {
            Intent logOut = new Intent(Menu.this, Login.class);
            startActivity(logOut);
        }
    }
}
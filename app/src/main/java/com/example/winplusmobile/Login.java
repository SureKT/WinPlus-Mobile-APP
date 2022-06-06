package com.example.winplusmobile;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.winplusmobile.Entities.Resp;
import com.example.winplusmobile.Entities.TUsuario;
import com.example.winplusmobile.Interfaces.WPMobile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Login extends AppCompatActivity {

    Button entrar;
    TextView usuario, contraseña;
    String user, password;
    TUsuario tUsuario;
    MediaPlayer error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usuario = findViewById(R.id.usuario);
        contraseña = findViewById(R.id.contraseña);
        tUsuario = (TUsuario) getApplicationContext();

        error = MediaPlayer.create(this, R.raw.efecto_error);

        usuario.setText("test");
        contraseña.setText("Pbjjajlp5h4m1.");
    }

    public void login(View view) {
        user = usuario.getText().toString();
        password = contraseña.getText().toString();
        entrar(user, password);
    }

    private void entrar(String user, String password) {
        Retrofit retrofit = new RetrofitClientInstance().getRetrofitInstance();
        WPMobile service = retrofit.create(WPMobile.class);
        Call<Resp> call = service.loginUsuario(user, password);

        call.enqueue(new Callback<Resp>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            // Conexion establecida
            public void onResponse(Call<Resp> call, Response<Resp> response) {
                // Respuesta erronea
                if (!response.isSuccessful()) {
                    usuario.setText("Codigo de error: " + response.code());
                    return;
                }

                // Usuario / Contraseña incorrectos
                Resp resp = response.body();
                if (resp.codigo == 1 || resp.codigo == 2) {
                    Toast.makeText(getApplicationContext(), resp.descripcion, Toast.LENGTH_SHORT).show();
                    error.start();
                    return;
                }

                // Datos correctos
                tUsuario.setToken(resp.datos);
                //tUsuario.setTokenValido(trues);

                Intent menu = new Intent(Login.this, Menu.class);
                startActivity(menu);
            }

            // Conexion fallida
            @Override
            public void onFailure(Call<Resp> call, Throwable t) {
                Log.e("TAG", t.toString());
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "No es posible conectarse al servidor", Toast.LENGTH_LONG).show();
                error.start();
            }
        });
    }
}

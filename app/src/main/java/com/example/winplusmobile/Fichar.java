package com.example.winplusmobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.winplusmobile.Entities.Causas;
import com.example.winplusmobile.Entities.Fichaje;
import com.example.winplusmobile.Entities.Resp;
import com.example.winplusmobile.Entities.TUsuario;
import com.example.winplusmobile.Interfaces.WPMobile;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Fichar extends AppCompatActivity {

    TUsuario tUsuario;
    Spinner causas;
    List<Causas> causasList;
    ArrayList<String> causasDropdown = new ArrayList<String>();
    Fichaje fichaje = new Fichaje();
    Causas causaElegida;

    private FusedLocationProviderClient fusedLocationClient;
    GoogleMap mapa;
    ImageView gpsOff;

    MediaPlayer acierto, error;

    double latitud;
    double longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fichar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        gpsOff = findViewById(R.id.gps_off);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        tUsuario = (TUsuario) getApplicationContext();

        acierto = MediaPlayer.create(this, R.raw.efecto_acierto);
        error = MediaPlayer.create(this, R.raw.efecto_error);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);

        // CAUSAS DROPDOWN
        causas = (Spinner) findViewById(R.id.causas);

        listadoCausas();

        causas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                causaElegida = null;
                for (Causas causa : causasList) {
                    if (parent.getItemAtPosition(position).toString() == causa.descripcion) {
                        causaElegida = causa;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void volverFichar(View view) {
//        validarToken();
        Intent menu = new Intent(Fichar.this, Menu.class);
        startActivity(menu);
    }

    public void entrada(View view) throws Throwable {
//        validarToken();

        fichaje.setCausa((short) 0);
        fichaje.setFuncion((byte) 1);
        fichar(view);
    }

    public void salida(View view) throws Throwable {
//        validarToken();

        fichaje.setCausa((short) 0);
        fichaje.setFuncion((byte) 2);
        fichar(view);
    }

    public void salidaInc(View view) throws Throwable {
//        validarToken();

        if (causaElegida == null) {
            error.start();
            Toast.makeText(getApplicationContext(), "Debes seleccionar una causa", Toast.LENGTH_SHORT).show();
            return;
        }
        fichaje.setCausa(causaElegida.codigo);
        fichaje.setFuncion((byte) 4);
        fichar(view);
    }

    private void fichar(View view) {
        ubicacionActual();
        fichaje.setLatitud(latitud);
        fichaje.setLongitud(longitud);

        Retrofit retrofit = new RetrofitClientInstance().getRetrofitInstance();

        WPMobile service = retrofit.create(WPMobile.class);
        Call<Resp> call = service.postFichaje(tUsuario.getToken(), fichaje);
        call.enqueue(new Callback<Resp>() {
            @Override
            public void onResponse(Call<Resp> call, Response<Resp> response) {
                // Respuesta erronea
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "El fichaje ha fallado, inténtalo más tarde", Toast.LENGTH_LONG).show();
                    return;
                }

                Resp resp = response.body();
                if (resp.codigo == 0) {
                    acierto.start();
                } else {
                    error.start();
                }

                Snackbar.make(view, resp.descripcion, Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(getResources().getColor(R.color.blue))
                        .setAction("Cerrar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        }).show();
            }

            @Override
            public void onFailure(Call<Resp> call, Throwable t) {
                Log.e("TAG", t.toString());
                t.printStackTrace();
            }
        });
    }

    public void listadoCausas() {
        Retrofit retrofit = new RetrofitClientInstance().getRetrofitInstance();

        WPMobile service = retrofit.create(WPMobile.class);
        Call<List<Causas>> call = service.getCausas();
        call.enqueue(new Callback<List<Causas>>() {
            @Override
            public void onResponse(Call<List<Causas>> call, Response<List<Causas>> response) {
                if (response.body() != null) {
                    causasList = response.body();
                    for (Causas causa : causasList) {
                        causasDropdown.add(causa.descripcion);
                    }
                    causasDropdown.set(0, "Seleccionar");
                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, causasDropdown);
                    causas.setAdapter(adapter);
                } else {
                    Toast.makeText(getApplicationContext(), "No hay causas de incidendia", Toast.LENGTH_LONG).show();
                    error.start();
                }
            }

            @Override
            public void onFailure(Call<List<Causas>> call, Throwable t) {
                Log.e("TAG", t.toString());
                t.printStackTrace();
                error.start();
                Toast.makeText(getApplicationContext(), "No es posible conectarse al servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            ubicacionActual();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permisions, @NonNull int[] grantresults) {
        super.onRequestPermissionsResult(requestCode, permisions, grantresults);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkIfLocationOpened()) {
            ubicacionActual();
        } else {
            Toast.makeText(getApplicationContext(), "Sin permiso a su ubicacion, no se registrará su ubicación en el fichaje", Toast.LENGTH_LONG).show();
            gpsOff.setVisibility(gpsOff.VISIBLE);
            error.start();
        }
    }

    @SuppressLint("MissingPermission")
    public void ubicacionActual() {
        try {
//            if (checkIfLocationOpened()) {
            mapa.setMyLocationEnabled(true);
            getLastLocationNewMethod();
//            } else {
//                Toast.makeText(getApplicationContext(), "La ubicación no esta activada, no se registrara su ubicación en el fichaje", Toast.LENGTH_LONG).show();
//                gpsOff.setVisibility(gpsOff.VISIBLE);
//                error.start();
//            }
        } catch (Exception e) {
            error.start();
            Toast.makeText(getApplicationContext(), "Ha ocurrido un erro con la ubicacion, no se añadira la ubicacion en los fichajes 1", Toast.LENGTH_LONG).show();
            gpsOff.setVisibility(gpsOff.VISIBLE);
        }
    }

    private boolean checkIfLocationOpened() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (provider.contains("gps") || provider.contains("network")) {
            return true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocationNewMethod() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitud = location.getLatitude();
                            longitud = location.getLongitude();

                            LatLng fichaje = new LatLng(latitud, longitud);
                            mapa.getUiSettings().setScrollGesturesEnabled(false);
                            mapa.getUiSettings().setZoomGesturesEnabled(false);
                            mapa.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
                            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(fichaje, 16);
                            mapa.animateCamera(miUbicacion);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Winplus", "Error intentado obtener la ubicacion");
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un erro con la ubicacion, no se añadira la ubicacion en los fichajes 2", Toast.LENGTH_LONG).show();
                        gpsOff.setVisibility(gpsOff.VISIBLE);
                        error.start();
                    }
                });
    }

    private void validarToken() {
        tUsuario.validarToken();
        if (!tUsuario.tokenValido()) {
            Intent logOut = new Intent(this, Login.class);
            startActivity(logOut);
        }
    }
}

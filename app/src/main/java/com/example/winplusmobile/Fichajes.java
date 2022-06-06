package com.example.winplusmobile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.winplusmobile.Entities.Config;
import com.example.winplusmobile.Entities.TUsuario;
import com.example.winplusmobile.Entities.VFichajes;
import com.example.winplusmobile.Interfaces.WPMobile;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Fichajes extends AppCompatActivity {

    TUsuario tUsuario;
    EditText desdeFecha, hastaFecha;

    ImageView btDesde, btHasta;
    private int dia, mes, año;

    TableRow cabecera;
    TableLayout tablaPrincipal;
    List<VFichajes> vFichajes;

    MediaPlayer error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fichajes);

        tUsuario = (TUsuario) getApplicationContext();

        desdeFecha = findViewById(R.id.desde);
        hastaFecha = findViewById(R.id.hasta);

        btDesde = findViewById(R.id.btDesde);
        btHasta = findViewById(R.id.btHasta);

        tablaPrincipal = findViewById(R.id.tablaPrincipal);
        cabecera = findViewById(R.id.cabecera);
        cabecera.setVisibility(View.GONE);

        error = MediaPlayer.create(this, R.raw.efecto_error);

        fechasIniciales();
    }

    public void volverFichajes(View view) {
//        validarToken();
        Intent menu = new Intent(Fichajes.this, Menu.class);
        startActivity(menu);
    }

    private void validarToken() {
        tUsuario.validarToken();
        if (!tUsuario.tokenValido()) {
            Intent logOut = new Intent(Fichajes.this, Login.class);
            startActivity(logOut);
        }
    }

    public void crearTabla(View view) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaInicial;
        Date fechaFinal;

        try {
            fechaInicial = formato.parse(desdeFecha.getText().toString());
            fechaFinal = formato.parse(hastaFecha.getText().toString());
        } catch (ParseException ex) {
            Toast.makeText(getApplicationContext(), "Debe rellenar las fechas correctamente", Toast.LENGTH_SHORT).show();
            error.start();
            return;
        }

        if (fechaFinal.before(fechaInicial)){
            Toast.makeText(getApplicationContext(), "La fecha inicial debe ser anterior a la final", Toast.LENGTH_SHORT).show();
            error.start();
            return;
        }

        Retrofit retrofit = new RetrofitClientInstance().getRetrofitInstance();

        WPMobile service = retrofit.create(WPMobile.class);
        Call<List<VFichajes>> call = service.getVFichajes(tUsuario.getToken(), desdeFecha.getText().toString(), hastaFecha.getText().toString());
        call.enqueue(new Callback<List<VFichajes>>() {
            @Override
            public void onResponse(Call<List<VFichajes>> call, Response<List<VFichajes>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() != 0) {
                        tablaPrincipal.setBackground(getResources().getDrawable(R.drawable.none));

                        vFichajes = response.body();
                        try {
                            rellenarTabla();
                        } catch (ParseException e) {
                            Toast.makeText(getApplicationContext(), "Debes rellenar las fechas correctamente", Toast.LENGTH_SHORT).show();
                            error.start();
                        }
                    } else {
                        tablaPrincipal.removeAllViews();
                        cabecera.setVisibility(View.GONE);
                        tablaPrincipal.setBackground(getResources().getDrawable(R.drawable.notfound));
//                        tablaPrincipal.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<VFichajes>> call, Throwable t) {
                tablaPrincipal.removeAllViews();
                Log.e("TAG", t.toString());
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void rellenarTabla() throws ParseException {
        tablaPrincipal.removeAllViews();
        cabecera.setVisibility(View.VISIBLE);

        // TAMAÑOS
        int letra = dp(5);
        int padding = dp(5);
        int paddingExtra = dp(12);
        int dp170 = dp(170);

        // FECHAS
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatoFinal = new SimpleDateFormat("yyyy-MM-dd '-' HH:mm");

        for (int i = 0; i < vFichajes.size(); i++) {
            // FILA
            TableRow fila = new TableRow(this);
            if (i % 2 == 0) {
                if (i == 0) {
                    fila.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line_top_bottom));
                } else {
                    fila.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line_bottom));
                }
            } else {
                fila.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line_bottom_odd));
            }

            // FECHAS
            Date date = formatoFecha.parse(vFichajes.get(i).hora);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
//            2022-05-05T00:00:00

            // FECHA
            TextView fecha = new TextView(this);

            fecha.setWidth(dp170);
            fecha.setText(formatoFinal.format(date.getTime()));
            fecha.setPadding(paddingExtra, padding, padding, padding);
            fecha.setTextSize(letra);

            fila.addView(fecha);

            // FUNCION
            TextView funcion = new TextView(this);

            // FUNCION - DESCRIPCION
            switch (vFichajes.get(i).funcion) {
                case 1:
                    funcion.setText("Entrada");
                    funcion.setTextColor(0xFF00B954);
                    break;
                case 2:
                    funcion.setText("Salida");
                    funcion.setTextColor(0xFFDC5037);
                    break;
                case 4:
                    funcion.setText("Salida Inc");
                    funcion.setTextColor(0xFFFF890B);
                    break;
            }

            funcion.setPadding(paddingExtra, padding, padding, padding);
            funcion.setTextSize(letra);

            fila.addView(funcion);

            tablaPrincipal.addView(fila);

            // TODO: añadir un espacio extra cada cambio de fecha
//            if (i < vFichajes.size() - 1 && vFichajes.get(i).fecha.toString() != vFichajes.get(i + 1).fecha.toString()) {
//                TableRow filaEspacio = new TableRow(this);
//
//                filaEspacio.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line_bottom));
//
//                TextView espacio = new TextView(this);
//                filaEspacio.addView(espacio);
//
//                tablaPrincipal.addView(filaEspacio);
//            }
        }
    }

    private int dp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getApplicationContext().getResources().getDisplayMetrics());
    }

    public void fechasIniciales() {
        Retrofit retrofit = new RetrofitClientInstance().getRetrofitInstance();

        WPMobile service = retrofit.create(WPMobile.class);
        Call<Config> call = service.getConfig("Personal.UltFichaje");
        call.enqueue(new Callback<Config>() {
            @Override
            public void onResponse(Call<Config> call, Response<Config> response) {
                if (response.isSuccessful() && response != null) {
                    Config config = response.body();

                    Calendar calendar = Calendar.getInstance();
                    Date hoy = calendar.getTime();

                    calendar.add(Calendar.DATE, -1 * Integer.parseInt(config.valor));
                    Date inicio = calendar.getTime();

                    desdeFecha.setText(DateFormat.format("dd/MM/yyyy", inicio));
                    hastaFecha.setText(DateFormat.format("dd/MM/yyyy", hoy));
                } else {
                    Date hoy = new Date();

                    CharSequence desde = DateFormat.format("/MM/yyyy", hoy.getTime());
                    CharSequence hasta = DateFormat.format("dd/MM/yyyy", hoy.getTime());

                    desdeFecha.setText("01" + desde);
                    hastaFecha.setText(hasta);
                }
            }

            @Override
            public void onFailure(Call<Config> call, Throwable t) {
                Log.e("TAG", t.toString());
                t.printStackTrace();
            }
        });
    }

    public void fechasCalendario(View view) throws ParseException {
        Calendar calendar = null;
        calendar = Calendar.getInstance();
        if (view == btDesde && !desdeFecha.getText().toString().trim().isEmpty()) {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(desdeFecha.getText().toString().trim());
            calendar.setTime(date);
        } else if (view == btHasta && !hastaFecha.getText().toString().trim().isEmpty()) {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(hastaFecha.getText().toString().trim());
            calendar.setTime(date);
        }

        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        año = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                if (view == btDesde) {
                    desdeFecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                } else {
                    hastaFecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }
        }, año, mes, dia);

        datePickerDialog.show();
    }
}
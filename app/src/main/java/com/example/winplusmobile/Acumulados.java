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
import com.example.winplusmobile.Entities.VContadores;
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

public class Acumulados extends AppCompatActivity {

    TUsuario tUsuario;
    EditText desdeFecha, hastaFecha;

    ImageView btDesde, btHasta;
    private int dia, mes, año;

    TableRow cabecera;
    TableLayout tablaPrincipal;
    List<VContadores> vContadores;

    MediaPlayer error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acumulados);

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

    public void volverAcumulados(View view) {
//        validarToken();
        Intent menu = new Intent(Acumulados.this, Menu.class);
        startActivity(menu);
    }

    private void validarToken() {
        tUsuario.validarToken();
        if (!tUsuario.tokenValido()) {
            Intent logOut = new Intent(Acumulados.this, Login.class);
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

        if (fechaFinal.before(fechaInicial)) {
            Toast.makeText(getApplicationContext(), "La fecha inicial debe ser anterior a la final", Toast.LENGTH_SHORT).show();
            error.start();
            return;
        }

        Retrofit retrofit = new RetrofitClientInstance().getRetrofitInstance();

        WPMobile service = retrofit.create(WPMobile.class);
        Call<List<VContadores>> call = service.getContadores(tUsuario.getToken(), desdeFecha.getText().toString(), hastaFecha.getText().toString());
        call.enqueue(new Callback<List<VContadores>>() {
            @Override
            public void onResponse(Call<List<VContadores>> call, Response<List<VContadores>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() != 0) {
                        tablaPrincipal.setBackground(getResources().getDrawable(R.drawable.none));

                        vContadores = response.body();
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
            public void onFailure(Call<List<VContadores>> call, Throwable t) {
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
        int paddingExtra = dp(10);
        int dp170 = dp(280);

        // FECHAS
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatoFinal = new SimpleDateFormat("yyyy-MM-dd '-' HH:mm");

        for (int i = 0; i < vContadores.size(); i++) {
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

            // CONTADOR
            TextView contador = new TextView(this);

            contador.setWidth(dp170);
            contador.setText(vContadores.get(i).codigo + " - " + vContadores.get(i).descripcion);

            contador.setPadding(paddingExtra, padding, padding, padding);
            contador.setTextSize(letra);

            fila.addView(contador);

            // TOTAL
            TextView total = new TextView(this);

            if (vContadores.get(i).tipo == 1) {
                total.setText(String.valueOf(vContadores.get(i).valor));
            } else {
                total.setText(vContadores.get(i).valor / 60 + ":" + String.format("%02d", Math.abs(vContadores.get(i).getValor() % 60)));
            }

            total.setPadding(paddingExtra, padding, padding, padding);
            total.setTextSize(letra);

            fila.addView(total);

            tablaPrincipal.addView(fila);

            // TODO: añadir un espacio extra cada cambio de fecha
//            if (i < fichajes.size() - 1 && fichajes.get(i).fecha.toString() != fichajes.get(i + 1).fecha.toString()) {
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
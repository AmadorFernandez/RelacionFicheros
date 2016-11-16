package com.amador.relacionficheros;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Locale;

public class EjSix extends AppCompatActivity {

    private EditText edtDolares, edtEuros;
    private RadioButton rbDolares, rbEuros;
    private RadioGroup rg;
    private Button btnConvertir;
    private TextView txvResultado;
    private static final String URL = "http://api.fixer.io/latest"; //Url a la API
    private double ratio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ej_six);
        init();
    }

    private void init() {

        edtEuros = (EditText)findViewById(R.id.edEuros);
        edtDolares = (EditText)findViewById(R.id.edDolares);
        rg = (RadioGroup)findViewById(R.id.rg);
        btnConvertir = (Button)findViewById(R.id.btnConvertir);
        txvResultado = (TextView)findViewById(R.id.txvResultado);
        rbDolares = (RadioButton)findViewById(R.id.rbDolarEuro);
        rbEuros = (RadioButton)findViewById(R.id.rbEuroDolar);
        rbDolares.setChecked(true);
        edtDolares.setEnabled(true);
        edtDolares.requestFocus();
        edtEuros.setEnabled(false);
        ratio = 0;

        //Si no hay conexión
        if(!isNetworkAvailable()){

            Toast.makeText(EjSix.this, "No hay una conexion a internet para comprobar los ratios", Toast.LENGTH_LONG).show();

        }else {

            //Si hay conexión descargamos los ratios
            conexion();
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.rbDolarEuro){

                    edtDolares.setEnabled(true);
                    edtDolares.requestFocus();
                    edtEuros.setEnabled(false);

                }else {

                    edtEuros.setEnabled(true);
                    edtEuros.requestFocus();
                    edtDolares.setEnabled(false);

                }
            }
        });

        btnConvertir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double valor = 0;

                try {

                    //Si el ratio menor de cero es que no se pudo descargar
                    if(ratio > 0) {

                        //De dolares a euros
                        if (rbDolares.isChecked()) {

                                valor = Double.parseDouble(edtDolares.getText().toString());
                                double resultado = valor / ratio;
                                edtEuros.setText(String.format("%.2f", resultado));



                            //De euro a dolar
                        } else {


                                valor = Double.parseDouble(edtEuros.getText().toString());
                                double resultado = valor * ratio;
                                edtDolares.setText(String.format("%.2f", resultado));

                        }
                    }else {

                        //Aviso de que no habia datos para la conversion
                        Toast.makeText(EjSix.this, "No se tiene un valor para el ratio, conecte su movil a internet y reinicie la aplicación"
                                , Toast.LENGTH_LONG).show();
                    }
                } catch (NumberFormatException e) {

                    //Si alguno de los datos estaba mal
                    Toast.makeText(EjSix.this, "Hay un error en los datos introducidos REVISALOS!!", Toast.LENGTH_LONG).show();

                }

            }
        });

    }



    //Lo tipico para saber si hay conexion
    private boolean isNetworkAvailable(){
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            result = true;

        return result;

    }

    //Tarea asincrona para extraer el ratio
    private void conexion(){


        RequestQueue queue = Volley.newRequestQueue(EjSix.this);
        final ProgressDialog progress = new ProgressDialog(EjSix.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject ratios = response.getJSONObject("rates"); //Extrae el objeto JSON con los ratios
                    String fecha = response.getString("date"); //Extraigo la fecha
                    String valor = ratios.getString("USD"); //Es te es el ratio que buscamos
                    txvResultado.setText("Fecha: "+fecha+" 1 Euro equivale a "+valor+" Dolares"); //Informo
                    ratio = Double.parseDouble(valor); //Convierto


                } catch (JSONException e) {

                    //Si se produjo un error en la lectura
                    Toast.makeText(EjSix.this, "Se produjo un error en la lectura del archivo", Toast.LENGTH_LONG).show();

                } catch (NumberFormatException e){

                    //Por si hay un error en la conversion que nunca se sabe
                    Toast.makeText(EjSix.this, "Los datos del archivo provocaron un error de conversión", Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Si hay un error
                Toast.makeText(EjSix.this, "Se produjo el siguiente error "+error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        queue.add(jsonObjectRequest); //A la cola

    }
}

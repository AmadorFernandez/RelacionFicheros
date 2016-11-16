package com.amador.relacionficheros;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;
import cz.msebera.android.httpclient.Header;


public class EjFive extends AppCompatActivity {

    private Button btnAtras, btnAdelante, btnDowload;
    private ImageView imvPhoto;
    private EditText edtPath;
    private TextView txvTotal, txvNumber;
    private String[] imagePath;
    private int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ej_five);
        init();
    }

    private void init() {

        btnAtras = (Button)findViewById(R.id.btnAtras);
        btnAdelante = (Button)findViewById(R.id.btnAdelante);
        btnDowload = (Button)findViewById(R.id.btnDowload);
        imvPhoto = (ImageView)findViewById(R.id.imvEjFive);
        txvTotal = (TextView)findViewById(R.id.txvTotalPhoto);
        txvNumber = (TextView)findViewById(R.id.txvPos);
        edtPath = (EditText)findViewById(R.id.edtUrlEjFive);



        btnDowload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se verifica la conexión
                if(isNetworkAvailable()) {

                    if(!edtPath.getText().toString().isEmpty()){

                        pos = 0;

                        try {
                            //Se le pasa la ruta al metodo para la conexion asincrona
                            conectAsincronus(edtPath.getText().toString());
                        } catch (Exception e) {
                        }

                    }else {

                        //Informa de campo vacio
                        Toast.makeText(EjFive.this, "El campo no puede ser vacio", Toast.LENGTH_LONG).show();
                    }

                }else {

                    //Informa que no hay conexion
                    Toast.makeText(EjFive.this, "No hay conexion a internet", Toast.LENGTH_LONG).show();
                }


            }
        });

        btnAdelante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Verifica que el array con las rutas no este a null
                if(imagePath != null){

                    //Verfica que halla almenos un dato
                 if(imagePath.length > 0){

                     pos++;

                     try {
                         loadImagePos();
                     } catch (Exception e) {

                     }
                 }
                }
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Verifica que el array con las rutas no este a null
                if(imagePath != null){

                    //Verfica que halla almenos un dato
                    if(imagePath.length > 0) {
                        pos--;

                        try {
                            loadImageAnt();
                        } catch (Exception e) {

                        }
                    }
                }
            }
        });

    }

    private void loadImageAnt() {

        //Verifica que no se indique un indice que no existe
        if(pos < 0 && imagePath.length > 0){

            pos = imagePath.length - 1;
        }
        txvNumber.setText(String.valueOf(pos+1));

        try {
            //Le toca currar a picasso
            Picasso.with(EjFive.this).load(imagePath[pos]).error(R.drawable.error).placeholder(R.drawable.descargando).into(imvPhoto);
        } catch (Exception e) {

        }

    }

    private void loadImagePos() {

        //Comprobamos que no nos salimos del indice
        if(pos == imagePath.length){

            pos = 0;
        }
        txvNumber.setText(String.valueOf(pos+1));

        try {
            //A currar
            Picasso.with(EjFive.this).load(imagePath[pos]).error(R.drawable.error).placeholder(R.drawable.descargando).into(imvPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean isNetworkAvailable(){
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            result = true;

        return result;

    }

    private void conectAsincronus(String url){

        AsyncHttpClient client = new AsyncHttpClient();
        final ProgressDialog progress = new ProgressDialog(EjFive.this);
        client.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                //En case de error se detiene la barra de progreso y se informa al usuario
                progress.dismiss();
                Toast.makeText(EjFive.this, "Se produjo el siguiente error "+throwable.getMessage(), Toast.LENGTH_LONG).show();


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                progress.dismiss(); //Detiene la barra
                imagePath = responseString.split("\n"); //Extraemos las lineas del archivo
                //Comprobamos que tenemos almenos un elemento
                if(imagePath.length > 0){

                    txvTotal.setText("Se han descargado "+imagePath.length+" rutas");
                }
                loadImagePos();
            }

            @Override
            public void onStart() {
                // called before request is started
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage("Conectando . . .");
                progress.setCancelable(false);
                progress.show();
            }
        });


    }
}

package com.amador.relacionficheros;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class EjSeven extends AppCompatActivity {

    private Button btnOpenExplorer, btnUploadFile;
    private TextView txvFileName;
    private static final String WEB = "https://itishereapp.000webhostapp.com/upload.php";
    private static final String CODE  = "AFG";
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ej_seven);
        init();
    }

    private void init() {

        btnOpenExplorer = (Button)findViewById(R.id.btnOpenExplorer);
        btnUploadFile = (Button)findViewById(R.id.btnUpload);
        txvFileName = (TextView)findViewById(R.id.txvFileName);


        btnOpenExplorer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Lanzamos el explorador comprobando que existe alguno
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("file/*");
                if(i.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(i,1);

                }else{

                    Toast.makeText(EjSeven.this, "No hay un explorador de archivos instalado", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Comprobamos que tenemos conexion
                if(isNetworkAvailable()){

                    //Comprobamos que el fichero esta preparado para la subida
                    if(file != null){

                        subirFichero();

                    }else{

                        Toast.makeText(EjSeven.this, "No hay un archivo seleccionado", Toast.LENGTH_LONG).show();
                    }

                }else {

                    Toast.makeText(EjSeven.this, "No hay una conexion a la red", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            //Preparamos el fichero y sacamos el nombre a la pantalla
            file = new File(data.getData().getPath());
            txvFileName.setText(file.getName());
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

    private void subirFichero() {

        RequestParams params = new RequestParams();
        final ProgressDialog progreso = new ProgressDialog(EjSeven.this);

        try {
            params.put("fileToUpload", file);
            params.put("code", CODE);
            RestClient.post(WEB, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {

                    progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progreso.setMessage("Conectando . . .");
                    progreso.setOnCancelListener(new DialogInterface.OnCancelListener(){
                        public void onCancel(DialogInterface dialog){
                            RestClient.cancelRequests(getApplicationContext(), true);
                        }
                    });
                    progreso.show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    //Informamos del error
                    progreso.dismiss();
                    Toast.makeText(EjSeven.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //El servidor informara de lo sucedido
                    progreso.dismiss();
                    Toast.makeText(EjSeven.this, responseString, Toast.LENGTH_LONG).show();

                }
            });
        } catch (FileNotFoundException e) {

            Toast.makeText(EjSeven.this, "El archivo no pudo ser encontrado", Toast.LENGTH_LONG).show();
        }



    }
}

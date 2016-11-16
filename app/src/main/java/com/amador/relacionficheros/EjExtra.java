package com.amador.relacionficheros;

import android.app.ProgressDialog;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class EjExtra extends AppCompatActivity{

    private static final String FILE_NAME  = "prueba.odt";
    private TextView txvResultado;
    private ProgressDialog progreso;
    private CopyThread.EventListener listener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ej_extra);
        init();

    }

    private void init() {

        txvResultado = (TextView)findViewById(R.id.txvResultado);
        progreso = new ProgressDialog(EjExtra.this);
        listener = new CopyThread.EventListener() {
            @Override
            public void endCopy(final Result result) {

                //Esto es para que me deje acceder a los objetos del hilo principal
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {

                       progreso.dismiss();
                       txvResultado.setText(result.getMenssage());
                   }
               });
            }};

        /*Lo primero que hago es copiar el archivo prueba.odt de raw a la targeta SD usando el metodo con buffer.
        Despues, el archivo copiado es vuelto a copiar a uno nuevo pero sin buffer.
         Los archivos se llaman:
        copiaConBuffer.odt y copiaSinBuffer.odt
        *todo esto se hace en la clase CopyThread y los metodos de copia los tienes en la clase StreamIO*/

        if(!valideExternalSD()){

            Toast.makeText(EjExtra.this, "No hay SD compra una y luego me buscas", Toast.LENGTH_LONG).show();

        }else{

            try {

                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Copiando . . .");
                progreso.setCancelable(false);
                progreso.show();
                Result result = new Result();
                final CopyThread hilo = new CopyThread(result, EjExtra.this);
                hilo.setEndListener(listener); //Asigno el listener para el evento de fin de copias
                hilo.start();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private boolean valideExternalSD(){

        //Verifica si tenemos SD y si se puede escribir en ella
        String result = Environment.getExternalStorageState();
        return result.equals(Environment.MEDIA_MOUNTED);


    }
}

package com.amador.relacionficheros;


import android.content.Context;
import android.os.Environment;
import android.widget.EditText;

import java.io.File;

/**
 * Created by amador on 14/11/16.
 */

public class CopyThread extends Thread  {

    private StreamIO streamIO;
    private Result result;
    private static String FILE_NAME_BUFFER = "copiaConBuffer.odt";
    private Context context;
    private long inicioSinBuffer;
    private long finSinBuffer;
    private long inicioConBuffer;
    private long finConBuffer;
    private EventListener listener;

    public void setEndListener(EventListener listener){

        this.listener = listener;
    }

    public interface EventListener{

        void endCopy(Result result);
    }


    public CopyThread(Result result, Context context){

        this.result = result;
        this.context = context;
    }

    @Override
    public void run() {


        streamIO = new StreamIO(Environment.getExternalStorageDirectory().getAbsolutePath());

        //Inicio la copia con buffer
        inicioConBuffer = System.currentTimeMillis();
        streamIO.copyFile(context.getResources().openRawResource(R.raw.prueba), FILE_NAME_BUFFER);
        finConBuffer = System.currentTimeMillis();

        //Inicio la copia sin Buffer
        inicioSinBuffer = System.currentTimeMillis();
        streamIO.copyFileNoBuffer(FILE_NAME_BUFFER, "copiaSinBuffer.odt");
        finSinBuffer = System.currentTimeMillis();

        File file = new File(streamIO.getPath(), FILE_NAME_BUFFER);
        this.result.setMenssage("El archivo original esta en la raw con el nombre de prueba.odt\n" +
                "Ruta al archivo copiado con buffer: "+streamIO.getPath()+"\n"+
                "Nombre del archivo: "+FILE_NAME_BUFFER+"\n"+
                "Ruta al archivo copidado sin buffer: "+streamIO.getPath()+"\n"+
                "Nombre del archivo: copiaSinBuffer\n"+
                "Tamaño del archivo original: "+file.length()/1000000+"MB\n"+
                "Tiempo de copia sin buffer: "+String.valueOf(finSinBuffer-inicioSinBuffer)+" milisegundos\n"+
                "Tiempo de copia con buffer: "+String.valueOf(finConBuffer-inicioConBuffer)+" milisegundos");

        //Lanzo el evento al terminar
        if(listener != null) {

            listener.endCopy(result);
        }

    }
}

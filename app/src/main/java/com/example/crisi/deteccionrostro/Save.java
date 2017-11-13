package com.example.crisi.deteccionrostro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by crisi on 19/10/2017.
 */

public class Save {

        private Context TheThis;
        private String NameOfFolder = "/caras";
        private String NameOfFile = "rostro";

        public String[] SaveImage(Context context, Bitmap ImageToSave) {
            String[] msg = new String[2];
            TheThis = context;
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + NameOfFolder;
            String CurrentDateAndTime = getCurrentDateAndTime();
            File dir = new File(file_path);

            if (!dir.exists()) {
                dir.mkdirs();
            }
            String archivo = NameOfFile + CurrentDateAndTime + ".jpg";
            File file = new File(dir, archivo);

            try {
                FileOutputStream fOut = new FileOutputStream(file);

                ImageToSave.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
                MakeSureFileWasCreatedThenMakeAvabile(file);
                msg[0]="Rostro guardado";
                msg[1]=file_path+"/"+archivo;
            }

            catch (IOException e) {
                msg[0]="No se ha podido guardar el rostro: "+ e.getMessage();
            }

            return msg;
        }

        private void MakeSureFileWasCreatedThenMakeAvabile(File file){
            MediaScannerConnection.scanFile(TheThis, new String[] { file.toString() } , null, new MediaScannerConnection.OnScanCompletedListener() {

                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
        }

        private String getCurrentDateAndTime() {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String formattedDate = df.format(c.getTime());
            return formattedDate;
        }


}

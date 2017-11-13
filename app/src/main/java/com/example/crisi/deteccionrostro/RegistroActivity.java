package com.example.crisi.deteccionrostro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegistroActivity extends AppCompatActivity {

    EditText nombres, apellidos, email, telefono, documento, direccion, clave, clave1;
    TextView aviso;
    ImageButton rostro;
    Spinner tipo;
    String[] valores;
    String ruta;
    Bitmap image;
    private final static String[] usuarios = { "Cliente", "Domiciliario" };
    private final static int activitycara=0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.okregistro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.registromenu) {
            String tiposel = tipo.getSelectedItem().toString();
            if(clave.getText().toString().equals(clave1.getText().toString())) {
                if (clave.getText().toString().length() > 6){
                    if (tiposel.equals("Cliente")) {
                        valores = new String[]{nombres.getText().toString(), apellidos.getText().toString(), email.getText().toString(), clave.getText().toString(), telefono.getText().toString(), direccion.getText().toString()};
                        for (int i = 0; i < valores.length; i++) {
                            if (valores[i].equals("")) {
                                if (i != 5) {
                                    nombres.setError("Campo obligatorio");
                                    apellidos.setError("Campo obligatorio");
                                    email.setError("Campo obligatorio");
                                    clave.setError("Campo obligatorio");
                                    clave1.setError("Campo obligatorio");
                                    telefono.setError("Campo obligatorio");
                                    break;
                                }
                            }
                        }
                    } else {
                        if (image != null) {
                            valores = new String[]{nombres.getText().toString(), apellidos.getText().toString(), email.getText().toString(), clave.getText().toString(), telefono.getText().toString(), documento.getText().toString()};
                            for (int i = 0; i < valores.length; i++) {
                                if (valores[i].equals("")) {
                                    nombres.setError("Campo obligatorio");
                                    apellidos.setError("Campo obligatorio");
                                    email.setError("Campo obligatorio");
                                    clave.setError("Campo obligatorio");
                                    clave1.setError("Campo obligatorio");
                                    telefono.setError("Campo obligatorio");
                                    documento.setError("Campo obligatorio");
                                    break;
                                }
                            }
                        }
                    }
            }else{
                    clave.setError("La contraseña debe ser de minimo 6 caracteres");
                }
        }else{
                clave.setError("Las contraseñas no coinciden");
                clave1.setError("Las contraseñas no coinciden");
            }

            if(nombres.getError() != null || clave.getError() != null){
                Toast.makeText(this, "Formulario invalido", Toast.LENGTH_SHORT).show();
            }else{
                Tarea tarea = new Tarea();
                tarea.execute(tiposel);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombres = (EditText) findViewById(R.id.nombres);
        apellidos = (EditText) findViewById(R.id.apellidos);
        email = (EditText) findViewById(R.id.emailregistro);
        telefono = (EditText) findViewById(R.id.telefono);
        documento = (EditText) findViewById(R.id.documento);
        tipo = (Spinner) findViewById(R.id.spintipo);
        direccion = (EditText) findViewById(R.id.direccion);
        aviso = (TextView) findViewById(R.id.aviso);
        clave = (EditText) findViewById(R.id.clave);
        clave1 = (EditText) findViewById(R.id.clave1);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, usuarios);
        tipo.setAdapter(adapter);
        tipo.setSelection(0);
        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(tipo.getSelectedItemId()==0){
                    direccion.setVisibility(View.VISIBLE);
                    documento.setVisibility(View.GONE);
                    rostro.setVisibility(View.GONE);
                    aviso.setVisibility(View.GONE);
                }else{
                    documento.setVisibility(View.VISIBLE);
                    aviso.setVisibility(View.VISIBLE);
                    rostro.setVisibility(View.VISIBLE);
                    direccion.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rostro = (ImageButton)  findViewById(R.id.rostro);
        rostro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cara = new Intent(getApplicationContext(), RostroActivity.class);
                startActivityForResult(cara,activitycara);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_CANCELED){
            Toast.makeText(this, "No se cargo la foto", Toast.LENGTH_SHORT).show();
        }else{
            ruta = data.getExtras().getString("ruta");
            Log.i("conexion", ruta);
            image = BitmapFactory.decodeFile(ruta);
            rostro.setImageBitmap(image);
        }
    }

    private class Tarea extends AsyncTask<String,String,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

                Log.i("conexion", params[0].toLowerCase());
                String url = "http://192.168.0.14:8000/"+params[0].toLowerCase()+"s/";
                OkHttpClient client = new OkHttpClient();
                RequestBody body=null;
                if(params[0].equals("Domiciliario")) {
                    File cara = new File(ruta);
                    body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("rostro", cara.getName(), RequestBody.create(MediaType.parse("image/jpeg"), cara))
                            .addFormDataPart("nombres", valores[0])
                            .addFormDataPart("apellidos", valores[1])
                            .addFormDataPart("email", valores[2])
                            .addFormDataPart("clave", valores[3])
                            .addFormDataPart("telefono", valores[4])
                            .addFormDataPart("documento", valores[5])
                            .addFormDataPart("estado", "0")
                            .addFormDataPart("reputacion", "0").build();
                }else {
                    body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("nombres", valores[0])
                            .addFormDataPart("apellidos", valores[1])
                            .addFormDataPart("email", valores[2])
                            .addFormDataPart("clave", valores[3])
                            .addFormDataPart("telefono", valores[4])
                            .addFormDataPart("direccion", valores[5])
                            .addFormDataPart("estado", "0").build();
                }

                    Request req = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    try{
                        Response res = client.newCall(req).execute();
                        resul=true;
                        Log.i("conexion", res.body().toString());
                    }catch (Exception e){
                        Log.i("conexion", e.getMessage());
                        resul=false;
                    }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {
                Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(RegistroActivity.this, "Registro falló", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

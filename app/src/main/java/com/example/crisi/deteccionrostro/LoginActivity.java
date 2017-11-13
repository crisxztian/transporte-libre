package com.example.crisi.deteccionrostro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity  {

    EditText musuarioView;
    EditText mPasswordView;
    CheckBox remember;

    View mProgressView;
    View mLoginFormView;
    Button entrar,registrar;
    Boolean rec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String ema = leerValor(getApplicationContext(),"email");
        Log.i("errores", ema);
        if(!ema.isEmpty()){
            Intent sta = new Intent(getApplicationContext(),MenuActivity.class);
            startActivity(sta);
        }

        musuarioView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        remember = (CheckBox) findViewById(R.id.check);

        entrar = (Button) findViewById(R.id.entrar);
        entrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(remember.isChecked()){
                    rec=true;
                }else{
                    rec=false;
                }
                DoLogin login = new DoLogin();
                login.execute("");
            }
        });
        registrar = (Button) findViewById(R.id.sigin);
        registrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(getApplicationContext(),RegistroActivity.class);
                startActivity(reg);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mProgressView.setVisibility(View.GONE);

    }


    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 4;
    }

    public static void guardarValor(Context context, String keyPref, String valor) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString(keyPref, valor);
        editor.commit();
    }

    public static String leerValor(Context context, String keyPref) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getString(keyPref, "");
    }

    public class DoLogin extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        String user = musuarioView.getText().toString();
        String password = mPasswordView.getText().toString();
        @Override
        protected void onPreExecute() {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
            mProgressView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            mProgressView.setVisibility(View.GONE);
            if (isSuccess) {
                Toast.makeText(LoginActivity.this, r, Toast.LENGTH_SHORT).show();
        //        Intent mainmenu = new Intent(getApplicationContext(), LoginActivity.class);
        //        mainmenu.putExtra("idusuario", Integer.parseInt(key[0]));
        //        startActivity(mainmenu);
            }else{
                Toast.makeText(LoginActivity.this, r, Toast.LENGTH_SHORT).show();
                Log.i("errores",r);
            }

        }

        @Override
        protected String doInBackground(String... params) {

            if (user.trim().equals("") || password.trim().equals("")) {
                z = "Por favor ingrese usuario y contraseña";
            } else {
                    String url;
                    String url1 = "http://192.168.0.14:8000/clientes/?email="+user;
                    String url2 = "http://192.168.0.14:8000/domiciliarios/?email="+user;
                    for (int i = 0; i < 2; i++) {
                        if(i==0) {
                            url = url1;
                        }else{
                            url=url2;
                        }
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url(url)
                                        .build();
                                Response response = client.newCall(request).execute();

                                if (response.code() != 200 ) {
                                    z = "Error conectando a la base de datos";
                                } else {
                                    String res = response.body().string();
                                    Log.i("errores", res);
                                    JSONArray array;
                                    JSONObject jsonObj = null;
                                    if(!res.equals("[]")){
                                         array = new JSONArray(res);
                                         jsonObj = array.getJSONObject(0);
                                    }
                                    if(jsonObj == null){
                                        z = "Usuario inexistente";
                                        isSuccess = false;
                                    }
                                    else {
                                        if (jsonObj.getString("clave").equals(password)) {
                                            z = "Sesión iniciada";
                                            isSuccess = true;
                                            if (rec) {
                                                guardarValor(getApplicationContext(), "email", user);
                                            }
                                            Intent sta = new Intent(getApplicationContext(), MenuActivity.class);
                                            startActivity(sta);
                                            break;
                                        }else{
                                            z = "Credenciales invalidas";
                                            isSuccess = false;
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                isSuccess = false;
                                z = ex.getMessage();
                        }
                    }
            }
            return z;
        }
    }
}


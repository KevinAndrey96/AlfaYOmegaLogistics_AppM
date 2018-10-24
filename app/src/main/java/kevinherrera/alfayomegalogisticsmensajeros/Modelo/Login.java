package kevinherrera.alfayomegalogisticsmensajeros.Modelo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kevinherrera.alfayomegalogisticsmensajeros.Config.Config;
import kevinherrera.alfayomegalogisticsmensajeros.Config.JSONParser;
import kevinherrera.alfayomegalogisticsmensajeros.R;

public class Login extends AppCompatActivity {
    EditText NombreUsuario,ContrasenaUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        NombreUsuario = (EditText) findViewById(R.id.txtloginusuario);
        ContrasenaUsuario = (EditText) findViewById(R.id.txtlogincontrasena);
        Button Inicio=(Button) findViewById(R.id.btnlogininiciarsesion);
        //Button Registro=(Button) findViewById(R.id.btnloginRegistro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Inicio.setOnClickListener(clickinicio);
        //Registro.setOnClickListener(clickregistro);


        SharedPreferences User = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        SharedPreferences Pass = getSharedPreferences("Contrasena", Context.MODE_PRIVATE);

        if(User.equals("") && Pass.equals(""))
        {
            Toast.makeText(Login.this, "Por favor inicie sesión", Toast.LENGTH_SHORT).show();
        }
        else
        {
            NombreUsuario.setText(User.getString("Usuario", ""));
            ContrasenaUsuario.setText(Pass.getString("Contrasena", ""));
            Inicia();
            //redi();
        }


    }
    private View.OnClickListener clickregistro= new View.OnClickListener(){
        @Override
        public void onClick(View v)
        {
            /*Intent i=new Intent(Login.this,Registro.class);
            startActivity(i);*/
        }
    };
    private View.OnClickListener clickinicio= new View.OnClickListener(){
        public void onClick(View v)
        {
            Inicia();
        }
    };
    private void Inicia()
    {
        if(NombreUsuario.getText().toString().equals("") && ContrasenaUsuario.getText().toString().equals(""))
        {
            Toast.makeText(Login.this, "Por favor ingrese Usuario y Contraseña", Toast.LENGTH_SHORT).show();
        }
        else {
            if(NombreUsuario.getText().toString().equals("") || ContrasenaUsuario.getText().toString().equals("")){
                if (NombreUsuario.getText().toString().equals("")) {
                    Toast.makeText(Login.this, "Por favor ingrese su Usuario", Toast.LENGTH_SHORT).show();
                }
                if (ContrasenaUsuario.getText().toString().equals("")) {
                    Toast.makeText(Login.this, "Por favor ingrese su Contraseña", Toast.LENGTH_SHORT).show();
                }
            }else
            {
                new Enviar().execute();
            }
        }
    }

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    static Config C=new Config();
    private static String ENVIO_URL = C.ServidorURL+"Validar_login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Iniciando Sesión");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[5];
            Datos[0]=NombreUsuario.getText().toString();
            Datos[1]=ContrasenaUsuario.getText().toString();

            try {

                List params = new ArrayList();
                for (int i=0; i<Datos.length; i++)
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datos[i]));
                JSONObject json = jParser.makeHttpRequest(ENVIO_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    finish();
                    return json.getString(TAG_MESSAGE);
                } else {
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url.equals("Login OK")) {
                Guardar(NombreUsuario.getText().toString(), ContrasenaUsuario.getText().toString());
                redi();
            }
            else
            {
                Toast.makeText(Login.this, file_url, Toast.LENGTH_SHORT).show();
                NombreUsuario.setText("");
                ContrasenaUsuario.setText("");
            }
        }
    }

    private void redi()
    {
        Intent i= new Intent(Login.this,navigation.class);
        startActivity(i);
    }
    public void Guardar(String C, String P) {

        SharedPreferences preferencias = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("Usuario", C);
        editor.commit();

        SharedPreferences preferencias2 = getSharedPreferences("Contrasena", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferencias2.edit();
        editor2.putString("Contrasena", P);
        editor2.commit();

    }
    public void Reinicio()
    {
        SharedPreferences preferencias = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("Usuario", "");
        editor.commit();

        SharedPreferences preferencias2 = getSharedPreferences("Contrasena", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferencias2.edit();
        editor2.putString("Contrasena", "");
        editor2.commit();
    }
}

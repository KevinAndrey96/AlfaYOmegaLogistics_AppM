package kevinherrera.alfayomegalogisticsmensajeros.Modelo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class CambiarContrasena extends Activity {
    EditText ContraAnt,ContraNuev,ContraNuev2;
    String Usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiarcontrasena);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences User = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Usuario=User.getString("Usuario","");

        ContraAnt = (EditText) findViewById(R.id.txtcambiarcontraant);
        ContraNuev = (EditText) findViewById(R.id.txtcambiarcontranueva);
        ContraNuev2 = (EditText) findViewById(R.id.txtcambiarcontranueva2);

        Button btnCambiarContras=(Button) findViewById(R.id.btncambiarcontra);
        btnCambiarContras.setOnClickListener(clickcambiarcontrasena);

    }
    private View.OnClickListener clickcambiarcontrasena = new View.OnClickListener(){
        public void onClick(View v){
            if(ContraNuev.getText().toString().equals(""))
            {
                Toast.makeText(CambiarContrasena.this, "La nueva contraseña no puede ser vacia", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(ContraNuev.getText().toString().equals(ContraNuev2.getText().toString()) )
                {
                    new Enviar().execute();
                }
                else
                {
                    Toast.makeText(CambiarContrasena.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    static Config C=new Config();
    private static String ENVIO_URL = C.ServidorURL+"Envia_nuevacontra.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CambiarContrasena.this);
            pDialog.setMessage("Cambiando Contraseña");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[3];
            Datos[0]=Usuario;
            Datos[1]=ContraAnt.getText().toString();
            Datos[2]=ContraNuev.getText().toString();


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
            if (file_url != null) {
                Toast.makeText(CambiarContrasena.this, file_url, Toast.LENGTH_LONG).show();
                if(file_url.equals("Contraseña cambiada con exito"))
                {
                    finish();
                }
            }
        }
    }
}

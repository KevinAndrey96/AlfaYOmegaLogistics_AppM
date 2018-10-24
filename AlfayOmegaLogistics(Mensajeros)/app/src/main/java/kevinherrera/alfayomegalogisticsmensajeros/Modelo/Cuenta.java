package kevinherrera.alfayomegalogisticsmensajeros.Modelo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kevinherrera.alfayomegalogisticsmensajeros.Config.Config;
import kevinherrera.alfayomegalogisticsmensajeros.Config.JSONParser;
import kevinherrera.alfayomegalogisticsmensajeros.R;

/**
 * Created by Andrey on 02/09/2016.
 */
public class Cuenta extends Activity {
    TextView Usuario,Nombre,Correo,Nivel,placa;
    String U,N,Co,Ni,Plac;

    Config Con = new Config();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences User = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Palabra=User.getString("Usuario","");
        Usuario=(TextView) findViewById(R.id.txtcuentausuario);
        Nombre=(TextView) findViewById(R.id.txtcuentanombre);
        Correo=(TextView) findViewById(R.id.txtcuentacorreo);
        Nivel=(TextView) findViewById(R.id.txtcuentanivel);
        placa=(TextView) findViewById(R.id.tvplaca);

        Button btncamcon=(Button) findViewById(R.id.btncuentacambiarcontra);
        btncamcon.setOnClickListener(clickcambiarcontrasena);

        new Consulta().execute();
    }
    private View.OnClickListener clickcambiarcontrasena= new View.OnClickListener()
    {
        public void onClick(View v)
        {
            Intent i= new Intent(Cuenta.this, CambiarContrasena.class);
            startActivity(i);
        }
    };

    private ProgressDialog pDialog;
    private String Palabra = "";
    JSONParser jParser = new JSONParser();
    static Config C=new Config();
    ArrayList<HashMap<String, String>> List;
    private static String CONSULTA_URL = C.ServidorURL+"Lista_cuenta.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RECEIVE = "receive";

    private static final String TAG_DATO1 ="DATO1";
    private static final String TAG_DATO2 ="DATO2";
    private static final String TAG_DATO3 ="DATO3";
    private static final String TAG_DATO4 ="DATO4";

    JSONArray listadatos = null;
    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Cuenta.this);
            pDialog.setMessage("Obteniendo tus datos...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            String P = Palabra;
            List params = new ArrayList();

            params.add(new BasicNameValuePair("Parametro", P));

            JSONObject json = jParser.makeHttpRequest(CONSULTA_URL, "POST", params);

            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    listadatos = json.getJSONArray(TAG_RECEIVE);
                    String[] Datos=new String [4];
                    for (int i = 0; i < listadatos.length(); i++) {
                        JSONObject c = listadatos.getJSONObject(i);

                        Datos[0] = c.getString(TAG_DATO1);
                        Datos[1] = c.getString(TAG_DATO2);
                        Datos[2] = c.getString(TAG_DATO3);
                        Datos[3] = c.getString(TAG_DATO4);

                        HashMap map = new HashMap();

                        map.put(TAG_DATO1, ""+Datos[0]);
                        map.put(TAG_DATO2, ""+Datos[1]);
                        map.put(TAG_DATO3, "" + Datos[2]);
                        map.put(TAG_DATO4, "" + Datos[3]);

                        U=Palabra;
                        N=Datos[1];
                        Co=Datos[2];
                        Ni=Datos[3];
                        Plac=Datos[0];

                    }
                }
            } catch (JSONException e) {

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            Usuario.setText("Documento: " + U);
            Nombre.setText("" + N);
            Correo.setText("E-Mail: " + Co);
            Nivel.setText("Credito: " + Ni);
            placa.setText("Placa: "+Plac);
            pDialog.dismiss();

        }
    }
}

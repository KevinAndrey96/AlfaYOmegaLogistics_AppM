package kevinherrera.alfayomegalogisticsmensajeros.Modelo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kevinherrera.alfayomegalogisticsmensajeros.Config.Config;
import kevinherrera.alfayomegalogisticsmensajeros.Config.JSONParser;
import kevinherrera.alfayomegalogisticsmensajeros.R;


public class PorAsignar extends Activity {

    String Pin,Usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        empresaList = new ArrayList<HashMap<String, String>>();
        lista=(ListView) findViewById(R.id.lstglobal);

        SharedPreferences User = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Usuario=User.getString("Usuario", "");

        Palabra=Usuario;
        new Consulta().execute();
    }
    ProgressDialog pDialog;
    private String Palabra = "";

    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private static String CONSULTA_URL = C.ServidorURL+"Lista_serviciosporasignar.php";

    private static final String TAG_DATO1 ="DATO1";
    private static final String TAG_DATO2 ="DATO2";
    private static final String TAG_DATO3 ="DATO3";
    private static final String TAG_DATO4 ="DATO4";
    private static final String TAG_DATO5 ="DATO5";
    private static final String TAG_DATO6 ="DATO6";
    private static final String TAG_DATO7 ="DATO7";
    private static final String TAG_DATO8 ="DATO8";
    private static final String TAG_DATO9 ="DATO9";
    private static final String TAG_DATO10 ="DATO10";
    private static final String TAG_DATO11 ="DATO11";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RECEIVE = "receive";
    private static final String TAG_MESSAGE = "message";

    JSONParser jParser = new JSONParser();
    JSONArray products = null;
    ListView lista;

    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PorAsignar.this);
            pDialog.setMessage("Cargando...");
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
                    products = json.getJSONArray(TAG_RECEIVE);
                    String[] Datos=new String[11];
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        Datos[0] = c.getString(TAG_DATO1);
                        Datos[1] = c.getString(TAG_DATO2);
                        Datos[2] = c.getString(TAG_DATO3);
                        Datos[3] = c.getString(TAG_DATO4);
                        Datos[4] = c.getString(TAG_DATO5);
                        Datos[5] = c.getString(TAG_DATO6);
                        Datos[6] = c.getString(TAG_DATO7);
                        Datos[7] = c.getString(TAG_DATO8);
                        Datos[8] = c.getString(TAG_DATO9);
                        Datos[9] = c.getString(TAG_DATO10);
                        Datos[10] = c.getString(TAG_DATO11);

                        HashMap map = new HashMap();

                        map.put(TAG_DATO1, "Pin: "+Datos[0]);
                        map.put(TAG_DATO2, "Tipo: "+Datos[1]);
                        map.put(TAG_DATO3, "Estado: "+Datos[2]);
                        map.put(TAG_DATO4, "Deste: "+Datos[3]);
                        map.put(TAG_DATO5, "Hasta: "+Datos[4]);
                        map.put(TAG_DATO6, "Distancia: "+Datos[5]+" Km");
                        map.put(TAG_DATO7, "F. recogido: "+Datos[6]);
                        map.put(TAG_DATO8, "F. entregado: "+Datos[7]);
                        map.put(TAG_DATO9, "Descripción: "+Datos[8]);
                        map.put(TAG_DATO10, "Valor: $"+Datos[9]);
                        map.put(TAG_DATO11, "Mensajero: "+Datos[10]);

                        empresaList.add(map);
                    }
                    return json.getString(TAG_MESSAGE);
                }
                else
                {
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            //Toast.makeText(Teoria.this, file_url, Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            PorAsignar.this,
                            empresaList,
                            R.layout.activity_spservicios,
                            new String[]{
                                    TAG_DATO1,
                                    TAG_DATO2,
                                    TAG_DATO3,
                                    TAG_DATO4,
                                    TAG_DATO5,
                                    TAG_DATO6,
                                    TAG_DATO7,
                                    TAG_DATO8,
                                    TAG_DATO9,
                                    TAG_DATO10,
                                    TAG_DATO11,
                            },
                            new int[]{
                                    R.id.sppin,
                                    R.id.sptipodeservicio,
                                    R.id.spestadodeservicio,
                                    R.id.spdirecorigen,
                                    R.id.spdirecdestino,
                                    R.id.spdistancia,
                                    R.id.spfechasolici,
                                    R.id.spfechaentrega,
                                    R.id.spdescripcion,
                                    R.id.spvalorservicio,
                                    R.id.spmensajero,
                            });
                    lista.setAdapter(adapter);
                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Map<String, Object> map = (Map<String, Object>) lista.getItemAtPosition(position);
                            Pin = (String) map.get("DATO1");
                           // Toast.makeText(PorAsignar.this, Pin, Toast.LENGTH_SHORT).show();
                            Pin=Pin.substring(5);

                            FragmentManager fragmentManager = getFragmentManager();
                            DialogoSeleccion dialogo = new DialogoSeleccion();
                            dialogo.show(fragmentManager, "tagAlerta");


                            /*Intent l = new Intent(CategoriasCompletas.this, Palabras.class);
                            l.putExtra("Categoria", Categoria);
                            startActivity(l);*/
                        }
                    });
                }
            });
        }
    }
    @SuppressLint("ValidFragment")
    public class DialogoSeleccion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [2];
            items[0]="Si";
            items[1]="No";

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("¿Está Seguro que desea realizar este servicio?")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (items[item]) {
                                case "Si": {
                                    new Enviar().execute();
                                }
                                break;
                                case "No": {
                                }
                                break;
                            }
                        }
                    });

            return builder.create();
        }
    }
    private static String ENVIO_URL = C.ServidorURL+"Envia_asignacion.php";

    class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PorAsignar.this);
            pDialog.setMessage("Solicitando servicio");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[3];
            Datos[0]=Pin;
            Datos[1]=Usuario;

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
                Toast.makeText(PorAsignar.this, file_url, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
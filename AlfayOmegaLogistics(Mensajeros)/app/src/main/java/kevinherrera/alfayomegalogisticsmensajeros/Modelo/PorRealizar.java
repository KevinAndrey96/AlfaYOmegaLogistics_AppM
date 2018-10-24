package kevinherrera.alfayomegalogisticsmensajeros.Modelo;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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


public class PorRealizar extends Activity {

    String Pin,Usuario,Estado;
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
    private static String CONSULTA_URL = C.ServidorURL+"Lista_serviciosporrealizar2.php";

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
            pDialog = new ProgressDialog(PorRealizar.this);
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
                            PorRealizar.this,
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
                            /*new int[]{
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
                            });*/
                            new int[] {
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
                                    R.id.spmensajero
                            }){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = convertView;
                            for(int i=0;i<empresaList.size();i++){
                                if(v == null){
                                    LayoutInflater vi = (LayoutInflater)PorRealizar.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    v=vi.inflate(R.layout.activity_spservicios, null);
                                }
                                TextView ElDato1 = (TextView) v.findViewById(R.id.sppin);
                                TextView ElDato2 = (TextView) v.findViewById(R.id.sptipodeservicio);
                                TextView ElDato3 = (TextView) v.findViewById(R.id.spestadodeservicio);
                                TextView ElDato4 = (TextView) v.findViewById(R.id.spdirecorigen);
                                TextView ElDato5 = (TextView) v.findViewById(R.id.spdirecdestino);
                                TextView ElDato6 = (TextView) v.findViewById(R.id.spdistancia);
                                TextView ElDato7 = (TextView) v.findViewById(R.id.spfechasolici);
                                TextView ElDato8 = (TextView) v.findViewById(R.id.spfechaentrega);
                                TextView ElDato9 = (TextView) v.findViewById(R.id.spdescripcion);
                                TextView ElDato10 = (TextView) v.findViewById(R.id.spvalorservicio);
                                TextView ElDato11 = (TextView) v.findViewById(R.id.spmensajero);

                                ElDato1.setText(empresaList.get(position).get(TAG_DATO1));
                                ElDato2.setText(empresaList.get(position).get(TAG_DATO2));
                                ElDato3.setText(empresaList.get(position).get(TAG_DATO3));
                                ElDato4.setText(empresaList.get(position).get(TAG_DATO4));
                                ElDato5.setText(empresaList.get(position).get(TAG_DATO5));
                                ElDato6.setText(empresaList.get(position).get(TAG_DATO6));
                                ElDato7.setText(empresaList.get(position).get(TAG_DATO7));
                                ElDato8.setText(empresaList.get(position).get(TAG_DATO8));
                                ElDato9.setText(empresaList.get(position).get(TAG_DATO9));
                                ElDato10.setText(empresaList.get(position).get(TAG_DATO10));
                                ElDato11.setText(empresaList.get(position).get(TAG_DATO11));

                                if(empresaList.get(position).get(TAG_DATO3).equals("Estado: Asignado")){
                                    ElDato3.setText(empresaList.get(position).get(TAG_DATO3));
                                    ElDato3.setTextColor(PorRealizar.this.getResources().getColor(R.color.rojo));
                                }else if(empresaList.get(position).get(TAG_DATO3).equals("Estado: Recogido")){
                                    ElDato3.setText(empresaList.get(position).get(TAG_DATO3));
                                    ElDato3.setTextColor(PorRealizar.this.getResources().getColor(R.color.amarillo));

                                }else if(empresaList.get(position).get(TAG_DATO3).equals("Estado: Completado")){
                                    ElDato3.setText(empresaList.get(position).get(TAG_DATO3));
                                    ElDato3.setTextColor(PorRealizar.this.getResources().getColor(R.color.verde));
                                }
                            }
                            return v;
                        }
                    };
                    lista.setAdapter(adapter);

                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Map<String, Object> map = (Map<String, Object>) lista.getItemAtPosition(position);
                            Pin = (String) map.get("DATO1");
                            Pin=Pin.substring(5);
                            Estado = (String) map.get("DATO3");
                            Estado=Estado.substring(8);

                            FragmentManager fragmentManager = getFragmentManager();
                            DialogoSeleccion dialogo = new DialogoSeleccion();
                            dialogo.show(fragmentManager, "tagAlerta");

                        }
                    });
                }
            });
        }
    }

    public class DialogoSeleccion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [2];
            items[0]="Si";
            items[1]="No";

            String Texto="";
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            if(Estado.equals("Asignado"))
            {
                Texto="¿Está Seguro que desea recoger este servicio?";
            }
            else
            {
                if(Estado.equals("Recogido"))
                {
                    Texto="¿Está Seguro que desea completar este servicio?";
                }
            }
            builder.setTitle(Texto)
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
    private static String ENVIO_URL = C.ServidorURL+"Envia_realizacion.php";

    class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PorRealizar.this);
            pDialog.setMessage("Completando servicio");
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
            Datos[2]=Estado;

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
                Toast.makeText(PorRealizar.this, file_url, Toast.LENGTH_LONG).show();

            }
            if(Estado.equals("Asignado"))
            {
                finish();
            }
            else
            {
                if(Estado.equals("Recogido"))
                {
                    Intent i=new Intent(PorRealizar.this,FotoFirma.class);
                    i.putExtra("PIN",Pin);
                    startActivity(i);
                    finish();
                }
            }
        }
    }
}
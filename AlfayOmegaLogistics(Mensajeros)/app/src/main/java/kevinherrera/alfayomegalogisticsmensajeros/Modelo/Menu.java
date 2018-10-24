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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kevinherrera.alfayomegalogisticsmensajeros.Config.Config;
import kevinherrera.alfayomegalogisticsmensajeros.Config.JSONParser;
import kevinherrera.alfayomegalogisticsmensajeros.R;

public class Menu extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences User = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Usuario=User.getString("Usuario", "");

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener mlocListener = new MyLocationListener();
        mlocListener.setMainActivity(this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) mlocListener);

        Button CerrarSesion =(Button) findViewById(R.id.btnmenucerrarsesion);
        CerrarSesion.setOnClickListener(clickcerrarsesion);

        Button MiCuenta =(Button) findViewById(R.id.btnmenuperfil);
        MiCuenta.setOnClickListener(clickmicuenta);

        Button porasignar =(Button) findViewById(R.id.btnporasignar);
        porasignar.setOnClickListener(clickporasignar);

        Button porrealizar =(Button) findViewById(R.id.btnporrealizar);
        porrealizar.setOnClickListener(clickporrealizar);
    }
    private View.OnClickListener clickporasignar = new View.OnClickListener(){
        public void onClick(View v)
        {
            //Intent i=new Intent(Menu.this, PorAsignar.class);
            Intent i=new Intent(Menu.this, navigation.class);
            startActivity(i);
        }
    };
    private View.OnClickListener clickporrealizar = new View.OnClickListener(){
        public void onClick(View v)
        {
            Intent i=new Intent(Menu.this, PorRealizar.class);
            startActivity(i);
        }
    };
    private View.OnClickListener clickcerrarsesion = new View.OnClickListener(){
        public void onClick(View v)
        {
            FragmentManager fragmentManager = getFragmentManager();
            DialogoSeleccion dialogo = new DialogoSeleccion();
            dialogo.show(fragmentManager, "tagAlerta");
        }
    };
    private View.OnClickListener clickmicuenta = new View.OnClickListener(){
        public void onClick(View v)
        {
            Intent i=new Intent(Menu.this,Cuenta.class);
            startActivity(i);
        }
    };
    public class DialogoSeleccion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [2];
            items[0]="Si";
            items[1]="No";

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("¿Está Seguro?")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (items[item]) {
                                case "Si": {
                                    SharedPreferences preferencias = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferencias.edit();
                                    editor.putString("Usuario", "");
                                    editor.commit();

                                    SharedPreferences preferencias2 = getSharedPreferences("Contrasena", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferencias2.edit();
                                    editor2.putString("Contrasena", "");
                                    editor2.commit();

                                    Intent i = new Intent(Menu.this, Login.class);
                                    startActivity(i);
                                    finish();
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

    public void setLocation(Location loc) {

        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address address = list.get(0);
                    //messageTextView2.setText("Mi direcci—n es: \n" + address.getAddressLine(0));
                    //Toast.makeText(Menu.this, "Mi direcci—n es: \n" + address.getAddressLine(0), Toast.LENGTH_SHORT).show();
                    //DirD=address.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    String lati,longi,Coordenadas, Usuario;
    public class MyLocationListener implements LocationListener {
        Menu mainActivity;

        public Menu getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(Menu mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            String Text = "Mi ubicaci—n actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();

            lati=""+loc.getLatitude();
            longi=""+loc.getLongitude();

            //Toast.makeText(Menu.this, "Monitoreando "+lati+", "+longi, Toast.LENGTH_SHORT).show();

            Coordenadas=lati+","+longi;

            new Enviar().execute();
            this.mainActivity.setLocation(loc);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(Menu.this, "GPS Desactivado", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(Menu.this, "GPS Activado", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    ProgressDialog pDialog;

    static Config C=new Config();
    private static String ENVIO_URL = C.ServidorURL+"Envia_monitoreo.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RECEIVE = "receive";
    private static final String TAG_MESSAGE = "message";

    JSONParser jParser = new JSONParser();

    class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(Menu.this, "Monitoreando", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[3];
            Datos[0]=Usuario;
            Datos[1]=Coordenadas;

            try {

                List params = new ArrayList();
                for (int i=0; i<Datos.length; i++)
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datos[i]));

                JSONObject json = jParser.makeHttpRequest(ENVIO_URL, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
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
            if (file_url != null) {
                Toast.makeText(Menu.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}

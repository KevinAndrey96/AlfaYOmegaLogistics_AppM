package kevinherrera.alfayomegalogisticsmensajeros.Modelo;

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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kevinherrera.alfayomegalogisticsmensajeros.Config.Config;
import kevinherrera.alfayomegalogisticsmensajeros.Config.JSONParser;
import kevinherrera.alfayomegalogisticsmensajeros.R;

public class navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener mlocListener = new MyLocationListener();
        mlocListener.setMainActivity(this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) mlocListener);

        SharedPreferences User = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Usuario=User.getString("Usuario","");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.TVNameUser);
        nav_user.setText(Usuario);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.PorAsignarls) {
            Intent i=new Intent(navigation.this, PorAsignar1.class);
            startActivity(i);
        } else if (id == R.id.PorRealizarls) {
            Intent i=new Intent(navigation.this, PorRealizar1.class);
            startActivity(i);
        } else if (id == R.id.Perfills) {
            Intent i=new Intent(navigation.this,Cuenta.class);
            startActivity(i);
        } else if (id == R.id.Historialls) {
            Intent i=new Intent(navigation.this,Historial.class);
            startActivity(i);
        } else if (id == R.id.CerrarSesionls) {
            FragmentManager fragmentManager = getFragmentManager();
            DialogoSeleccion dialogo = new DialogoSeleccion();
            dialogo.show(fragmentManager, "tagAlerta");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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

                                    Intent i = new Intent(navigation.this, Login.class);
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
    int contador=4;
    public class MyLocationListener implements LocationListener {
        navigation mainActivity;

        public navigation getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(navigation mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            String Text = "Mi ubicación actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();

            lati=""+loc.getLatitude();
            longi=""+loc.getLongitude();

            Coordenadas=lati+","+longi;
            contador++;
            if(contador==5) {
                contador=0;
                if(anothercont==0)
                {
                    anothercont++;
                    Toast.makeText(navigation.this, "Su ubicación se está monitoreando desde este momento", Toast.LENGTH_SHORT).show();
                }
                new Enviar().execute();
            }
            this.mainActivity.setLocation(loc);

        }
        int anothercont=0;
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(navigation.this, "GPS Desactivado", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(navigation.this, "GPS Activado", Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(navigation.this, "Monitoreando", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(navigation.this, "Monitoreando: "+file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}

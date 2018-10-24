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
import android.graphics.Color;
import android.net.Uri;
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
 * Created by Andrey on 01/11/2016.
 */
public class ServiciosEspecificos extends Activity {
    TextView Pin,type, description, datesol, address1,address2, distance, value;
    String Corori,Cordes, Usuario, Pin1, BackP, Ste;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spserviciosespecificos);

        SharedPreferences User = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Usuario=User.getString("Usuario", "");

        Pin=(TextView) findViewById(R.id.esnumservice);
        type=(TextView) findViewById(R.id.estipo);
        description=(TextView) findViewById(R.id.esdescri);
        datesol=(TextView) findViewById(R.id.esfechasoli);
        address1=(TextView) findViewById(R.id.esdirecori);
        address2=(TextView) findViewById(R.id.esdirecdes);
        distance=(TextView) findViewById(R.id.esdistan);
        value=(TextView) findViewById(R.id.esvalor);

        Pin1=getIntent().getExtras().getString("Pin");
        Pin.setText("# Servicio "+Pin1);

        type.setText(getIntent().getExtras().getString("Tip"));
        description.setText(getIntent().getExtras().getString("Des"));
        datesol.setText(getIntent().getExtras().getString("Fec"));
        address1.setText(getIntent().getExtras().getString("Di1"));
        address2.setText(getIntent().getExtras().getString("Di2"));
        distance.setText(getIntent().getExtras().getString("Dis"));
        value.setText(getIntent().getExtras().getString("Val"));

        Corori=getIntent().getExtras().getString("Co1");
        Cordes=getIntent().getExtras().getString("Co2");

        Button verorigen  = (Button) findViewById(R.id.btnverorigen);
        Button verdestino  = (Button) findViewById(R.id.btnverdestino);
        Button aceptar  = (Button) findViewById(R.id.btnaceptar);

        verorigen.setOnClickListener(clickorigen);
        verdestino.setOnClickListener(clickdestino);
        aceptar.setOnClickListener(clickaceptar);

        int color=0;

        BackP = getIntent().getExtras().getString("Back");
        Ste = getIntent().getExtras().getString("Stat");

        if(getIntent().getExtras().getString("Back").equals("Historial"))
        {
            aceptar.setText("Tomar Servicio");
            color = Color.parseColor("#FFFF00");
            aceptar.setEnabled(false);
            aceptar.setVisibility(View.INVISIBLE);
        }
        if(getIntent().getExtras().getString("Back").equals("PorAsignar"))
        {
            aceptar.setText("Tomar Servicio");
            color = getResources().getColor(R.color.amarillo);//Color.parseColor(R.color.amarillo);
            aceptar.setBackground( getResources().getDrawable(R.drawable.botonamarillo));
        }
        else
        {
            if(getIntent().getExtras().getString("Stat").equals("Asignado"))
            {
                aceptar.setText("Recoger");
                color = getResources().getColor(R.color.rojo);//Color.parseColor(R.color.rojo);
                aceptar.setBackground( getResources().getDrawable(R.drawable.boton));

            }
            if(getIntent().getExtras().getString("Stat").equals("Recogido"))
            {
                aceptar.setText("Completar");
                color = getResources().getColor(R.color.verde);//Color.parseColor("#00FF00");
                aceptar.setBackground( getResources().getDrawable(R.drawable.botonverde));
            }
        }
        //aceptar.setBackgroundColor(color);
    }
    private View.OnClickListener clickaceptar =new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(BackP.equals("PorAsignar")) {
                FragmentManager fragmentManager = getFragmentManager();
                DialogoSeleccion dialogo = new DialogoSeleccion();
                dialogo.show(fragmentManager, "tagAlerta");
            }else
            {
                if(BackP.equals("PorRealizar")) {
                    FragmentManager fragmentManager2 = getFragmentManager();
                    DialogoSeleccion2 dialogo2 = new DialogoSeleccion2();
                    dialogo2.show(fragmentManager2, "tagAlerta");
                }

            }
        }
    };
    private View.OnClickListener clickorigen =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ubico(Corori,address1.getText().toString());
        }
    };
    private View.OnClickListener clickdestino =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ubico(Cordes,address2.getText().toString());
        }
    };

    public void ubico(String O, String D) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:" + O + "?q=" + O));
                startActivity(intent);
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
                                    new Enviar1().execute();
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

    ProgressDialog pDialog;
    private String Palabra = "";

    static Config C=new Config();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RECEIVE = "receive";
    private static final String TAG_MESSAGE = "message";

    JSONParser jParser = new JSONParser();
    JSONArray products = null;

    private static String ENVIO_URL1 = C.ServidorURL+"Envia_asignacion.php";
    class Enviar1 extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ServiciosEspecificos.this);
            pDialog.setMessage("Solicitando servicio");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[3];
            Datos[0]=Pin1;
            Datos[1]=Usuario;

            try {

                List params = new ArrayList();
                for (int i=0; i<Datos.length; i++)
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datos[i]));

                JSONObject json = jParser.makeHttpRequest(ENVIO_URL1, "POST", params);

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
                Toast.makeText(ServiciosEspecificos.this, file_url, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @SuppressLint("ValidFragment")
    public class DialogoSeleccion2 extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [2];
            items[0]="Si";
            items[1]="No";

            String Texto="";
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            if(Ste.equals("Asignado"))
            {
                Texto="¿Está Seguro que desea recoger este servicio?";
            }
            else
            {
                if(Ste.equals("Recogido"))
                {
                    Texto="¿Está Seguro que desea completar este servicio?";
                }
            }
            builder.setTitle(Texto)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (items[item]) {
                                case "Si": {
                                    new Enviar2().execute();
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
    private static String ENVIO_URL2 = C.ServidorURL+"Envia_realizacion.php";
    class Enviar2 extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ServiciosEspecificos.this);
            pDialog.setMessage("Completando servicio");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[3];
            Datos[0]=Pin1;
            Datos[1]=Usuario;
            Datos[2]=Ste;

            try {

                List params = new ArrayList();
                for (int i=0; i<Datos.length; i++)
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datos[i]));

                JSONObject json = jParser.makeHttpRequest(ENVIO_URL2, "POST", params);

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
                Toast.makeText(ServiciosEspecificos.this, file_url, Toast.LENGTH_LONG).show();

            }
            if(Ste.equals("Asignado"))
            {
                finish();
            }
            else
            {
                if(Ste.equals("Recogido"))
                {
                    Intent i=new Intent(ServiciosEspecificos.this,FotoFirma.class);
                    i.putExtra("PIN",Pin1);
                    startActivity(i);
                    finish();
                }
            }
        }
    }
}


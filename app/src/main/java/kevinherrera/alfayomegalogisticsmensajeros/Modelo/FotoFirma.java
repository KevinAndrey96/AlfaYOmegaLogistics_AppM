package kevinherrera.alfayomegalogisticsmensajeros.Modelo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.File;

import kevinherrera.alfayomegalogisticsmensajeros.Config.Config;
import kevinherrera.alfayomegalogisticsmensajeros.R;

/**
 * Created by Andrey on 30/10/2016.
 */
public class FotoFirma extends Activity {
    private Button camara;
    //private ImageView imagen;
    private Uri output, output2;
    private String foto,PIN;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_firma);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //imagen = (ImageView) findViewById(R.id.ImgFirma);
        camara = (Button) findViewById(R.id.btnenviafoto);
        camara.setOnClickListener(fotografia);

        PIN=getIntent().getExtras().getString("PIN");
    }
    private View.OnClickListener fotografia = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!PIN.equalsIgnoreCase("")){
                getCamara();

            }else{
                Toast.makeText(FotoFirma.this, "Error inesperado, intente de nuevo", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void getCamara(){
       // foto = Environment.getExternalStorageDirectory() +"/Firmas/" +PIN+".jpg";
        //file=new File(foto);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //output= Uri.fromFile(file);

        ContentValues values = new ContentValues();

        values.put (MediaStore.Images.Media.IS_PRIVATE, 1);
        values.put (MediaStore.Images.Media.TITLE, "Prueba");
        values.put (MediaStore.Images.Media.DESCRIPTION, "Alfa Y Omega");

        output2 = FotoFirma.this.getContentResolver().insert (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Toast.makeText(FotoFirma.this, foto, Toast.LENGTH_LONG).show();
        foto = getRealPathFromUri(output2);
        file = new File(foto);
        output2= Uri.fromFile(file);



        SharedPreferences preferencias5 = getSharedPreferences("Fichero", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor5 = preferencias5.edit();
        editor5.putString("Fichero", foto);
        editor5.commit();




        //Toast.makeText(FotoFirma.this, foto, Toast.LENGTH_LONG).show();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output2);
        startActivityForResult(intent, 1);
    }
    public String getRealPathFromUri(Uri contentUri) {

        Cursor cursor = null;
        try {

            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = FotoFirma.this.getContentResolver().query(contentUri,  proj, null, null, null);
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            return cursor.getString(column_index);
        } finally {

            if (cursor != null) {

                cursor.close();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
       /* if (requestCode == 1 && resultCode == RESULT_OK) {
               UploaderFoto nuevaTarea = new UploaderFoto();
               nuevaTarea.execute(foto);
//            Toast.makeText(FotoFirma.this, requestCode, Toast.LENGTH_SHORT).show();
        }
*/

        SharedPreferences Fiche = getSharedPreferences("Fichero", Context.MODE_PRIVATE);
        foto=Fiche.getString("Fichero", "");


        String[] Aux=foto.split("/");
        String Aux2=Aux[Aux.length-1];
        Aux[Aux.length-1]=PIN+".jpg";

        String ConcatenaGlobal="";

        for(int i=0;i<Aux.length-1;i++)
        {
            ConcatenaGlobal+=Aux[i]+"/";
        }
        ConcatenaGlobal+=Aux[Aux.length-1];



        //Toast.makeText(FotoFirma.this, foto, Toast.LENGTH_LONG).show();
        //Toast.makeText(FotoFirma.this, ConcatenaGlobal, Toast.LENGTH_LONG).show();
        File f1=new File(foto);
        File f2=new File(ConcatenaGlobal);
        f1.renameTo(f2);

        foto=ConcatenaGlobal;

        //Toast.makeText(FotoFirma.this, foto, Toast.LENGTH_SHORT).show();
        UploaderFoto nuevaTarea = new UploaderFoto();
        nuevaTarea.execute(foto);

    }
    Config C=new Config();

    public class UploaderFoto extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;
        String miFoto = "";
        @Override
        protected String doInBackground(String... args) {
            miFoto = foto;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                HttpPost httppost = new HttpPost(C.ServidorURL+"upload_foto.php");
                File file = new File(miFoto);
                MultipartEntity mpEntity = new MultipartEntity();
                ContentBody foto = new FileBody(file, "image/jpeg");
                mpEntity.addPart("fotoUp", foto);
                httppost.setEntity(mpEntity);
                httpclient.execute(httppost);
                httpclient.getConnectionManager().shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FotoFirma.this);
            pDialog.setMessage("Subiendo...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            Toast.makeText(FotoFirma.this, "Subida Con Exito", Toast.LENGTH_LONG).show();
            if (file_url != null) {
                Toast.makeText(FotoFirma.this, file_url, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

}

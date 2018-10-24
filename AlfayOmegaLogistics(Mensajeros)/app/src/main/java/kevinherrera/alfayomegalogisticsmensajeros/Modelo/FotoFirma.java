package kevinherrera.alfayomegalogisticsmensajeros.Modelo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
    private ImageView imagen;
    private Uri output;
    private String foto,PIN;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_firma);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imagen = (ImageView) findViewById(R.id.ImgFirma);
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
        foto = Environment.getExternalStorageDirectory() +"/Firmas/" +PIN+".jpg";
        file=new File(foto);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        output= Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, 1);
    }
    private Bitmap loadImage(String imgPath) {
        BitmapFactory.Options options;
        try {
            options = new BitmapFactory.Options();
            options.inSampleSize = 4;// 1/4 of origin image size from width and height
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
           Bitmap bMap = BitmapFactory.decodeFile(
                    Environment.getExternalStorageDirectory() +
                            "/Firmas/" + PIN + ".jpg");
           // Toast.makeText(FotoFirma.this, Environment.getExternalStorageDirectory()+"/Firmas/" + PIN + ".jpg", Toast.LENGTH_SHORT).show();
            imagen.setImageBitmap(bMap);
            UploaderFoto nuevaTarea = new UploaderFoto();
            nuevaTarea.execute(foto);

        }

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
            pDialog.setMessage("Subiendo firma");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(FotoFirma.this, file_url, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}

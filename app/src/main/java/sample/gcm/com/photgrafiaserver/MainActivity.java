package sample.gcm.com.photgrafiaserver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sample.gcm.com.photgrafiaserver.datos.Cedula;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "kerpie.hardware" ;
    //Atributos
    //Necesitamos un Boton y un imageView

    /**
     * Constantes para identificar la acci—n realizada (tomar una fotograf’a
     * o bien seleccionarla de la galer’a)
     */
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;

    private Button bt_hacerfoto;
    private Button btn_Upload;
    private TextView txtCedulas;
    private ImageButton camara;
    private ImageView img;
    private Uri output;
    private String foto;
    private File file;
    private File image;
    private File imagesFolder;

    ImageView photo;
    Button button;
    Bitmap photobmp;
    private double aleatorio = 0;

    private String cedula;
    public String Cedulas;
    private StringBuilder DatosCedula;
    private String Dta;
    private Cedula objetoCedula;
    private List<Cedula>listaCedula;
    private StringBuilder mensaje = new StringBuilder();
    //aAtributos GPS
    private float latitud;
    private float longitud;
    private String hora;
    private String fecha;
    private String fotos;
    private ImageButton PrecionaCamara;
    //Cosntructor

    //Insertar inserta;
    //creamos la actividad
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.camara);
        listaCedula=new ArrayList<Cedula>();
        //para crear una nueva foto diferente
        aleatorio = new Double(Math.random()*100).intValue();
        foto = Environment.getExternalStorageDirectory()+"/imagen" + aleatorio +".jpg";
        txtCedulas = (TextView)findViewById(R.id.cedula);
        //Ruecuperar parametros y los muestra en textview
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            mensaje.append("Cedula: \r\t");
            mensaje.append(bundle.getString("Cedula")+ "\r\n");
        }
        //txtCedulas.setTextSize(33);
        //txtCedulas.setText(mensaje);
        DatosCedula = mensaje;
        Dta = bundle.getString("Cedula");
        //Relacionamos con el XML
        //img = (ImageView)this.findViewById(R.id.imageView1);
        PrecionaCamara = (ImageButton)this.findViewById(R.id.imageButtonCamara);
        PrecionaCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri output = Uri.fromFile(new File(foto));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });
        bt_hacerfoto = (Button) this.findViewById(R.id.btnTomaFoto);
        //Añadimos el Listener Boton
        bt_hacerfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri output = Uri.fromFile(new File(foto));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });

        btn_Upload = (Button)findViewById(R.id.btnUpload);
        btn_Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Codifica la imagen con Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //Ruta donde esta la foto
                Bitmap bMap = BitmapFactory.decodeFile(
                        Environment.getExternalStorageDirectory() +
                                "/Tutorialeshtml5/" + "foto.jpg");

                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            }
        });




    }
    /**
     * Funcion que se ejecuta cuando concluye el intent en el que se
     * solicita una imagen
     * ya sea de la camara o de la galeria
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        img = (ImageView) this.findViewById(R.id.imageView1);
        img.setImageBitmap(BitmapFactory.decodeFile(foto));

        Bitmap bMap = BitmapFactory.decodeFile(
                Environment.getExternalStorageDirectory() +
                        "/imagen" + aleatorio + ".jpg");

        img.setImageBitmap(bMap);
        File file = new File(foto);
        if (requestCode == TAKE_PICTURE) {
            if (file.exists()) {
                SubirServidor nuevaFotoaSubir=new SubirServidor();
                nuevaFotoaSubir.execute(foto);
            }else{
                Toast.makeText(getApplicationContext(), "No se ha realizado la foto", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Clase para subir archivo al servidor
    class SubirServidor extends AsyncTask<String, String, Void>{

        //Atributos
        ProgressDialog progressDialog;
        String miFoto;

        @Override
        protected Void doInBackground(String... params) {

            //Instacia de Clase
            miFoto = params[0];

                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(
                        CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                //Ruta para php
                HttpPost httpPost = new HttpPost("http://192.168.1.124/imegen/upload.php");


                //Instancia de Clase para la foto
                File file = new File(miFoto);
                MultipartEntity multipartEntity = new MultipartEntity();
                ContentBody foto = new FileBody(file, "image/jpeg");
                multipartEntity.addPart("fotoUp", foto);

                httpPost.setEntity(multipartEntity);
                //Entra para ejecucion del servidor
                try {
                    httpClient.execute(httpPost);
                    httpClient.getConnectionManager().shutdown();
                }catch (ClientProtocolException e){
                    Log.e("Connect to /192.168.1.124:80 timed out", "Error !!" + e.toString());
                    e.printStackTrace();
                    showErrorVista();

                } catch (IOException e) {
                    Log.e("Connect to /192.168.1.124:80 timed out", "Error !!" + e.toString());
                    e.printStackTrace();
                    showErrorVista();

                }


            return null;


        }


        //Alerta de formulario
        private void showErrorVista(){
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("CI.");
                    builder.setMessage("Error de Conexion.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //startActivity(new Intent(VistaFormularioEjecuciones.this, MainActivity.class));
                                    //establecerAlarmaClickMasMinutos(7);
                                    //establecerAlarmaClick(1200);

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }


        //Metodo para el mensaje
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("CI");
            progressDialog.setMessage("Subiendo la Imagen, espere por favor");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        //Metodo de salilda
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            progressDialog.dismiss();
        }
    }

}

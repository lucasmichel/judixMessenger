package com.app.alg.judix.telaAssinatura;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.alg.judix.R;
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.telaAssinaturasMandado.DadosAssinaturaMandadoPartesRelacionadas;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lucas on 28/01/16.
 */
public class Tela extends AppCompatActivity {

    ProgressDialog progressDialog;
    String CAMPO_ID;
    String caminhoWebService;

    String telaEnviada;

    private TelaView doodleView; // drawing View
    private SensorManager sensorManager; // monitors accelerometer
    private float acceleration; // acceleration
    private float currentAcceleration; // current acceleration
    private float lastAcceleration; // last acceleration
    private AtomicBoolean dialogIsVisible = new AtomicBoolean(); // false


    private static String nomeFuncaoWebservice = null;
    private static String nomeCampoWebservice = null;
    private static String idCampoWebservice = null;

    // create menu ids for each menu option
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int WIDTH_MENU_ID = Menu.FIRST + 1;
    private static final int ERASE_MENU_ID = Menu.FIRST + 2;
    private static final int CLEAR_MENU_ID = Menu.FIRST + 3;
    private static final int SAVE_MENU_ID = Menu.FIRST + 4;

    // value used to determine whether user shook the device to erase
    private static final int ACCELERATION_THRESHOLD = 15000;

    // variable that refers to a Choose Color or Choose Line Width dialog
    private Dialog currentDialog;

    // called when this Activity is loaded
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assiantura); // inflate the layout

        // get reference to the DoodleView
        doodleView = (TelaView) findViewById(R.id.TelaView);
        //limpa a tela


        Bundle extras = getIntent().getExtras();
        telaEnviada = extras.getString("telaEnviada");


        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        nomeFuncaoWebservice = pref.getString("nomeFuncaoWebservice", null);
        nomeCampoWebservice = pref.getString("nomeCampoWebservice", null);
        idCampoWebservice = pref.getString("idCampoWebservice", null);


        // initialize acceleration values
        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        enableAccelerometerListening(); // listen for shake




    } // end method onCreate

    // when app is sent to the background, stop listening for sensor events
    @Override
    protected void onPause()
    {
        super.onPause();
        disableAccelerometerListening(); // don't listen for shake
    } // end method onPause

    // enable listening for accelerometer events
    private void enableAccelerometerListening()
    {
        // initialize the SensorManager
        sensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    } // end method enableAccelerometerListening

    // disable listening for accelerometer events
    private void disableAccelerometerListening()
    {
        // stop listening for sensor events
        if (sensorManager != null)
        {
            sensorManager.unregisterListener(
                    sensorEventListener,
                    sensorManager.getDefaultSensor(
                            SensorManager.SENSOR_ACCELEROMETER));
            sensorManager = null;
        } // end if
    } // end method disableAccelerometerListening

    // event handler for accelerometer events
    private SensorEventListener sensorEventListener =
            new SensorEventListener()
            {
                // use accelerometer to determine whether user shook device

                public void onSensorChanged(SensorEvent event)
                {
                    // ensure that other dialogs are not displayed
                    if (!dialogIsVisible.get())
                    {
                        // get x, y, and z values for the SensorEvent
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        // save previous acceleration value
                        lastAcceleration = currentAcceleration;

                        // calculate the current acceleration
                        currentAcceleration = x * x + y * y + z * z;

                        // calculate the change in acceleration
                        acceleration = currentAcceleration *
                                (currentAcceleration - lastAcceleration);



                        /*verifica a rotacao e apaga a tela RETIRADO DO PROJETO*/

                        // if the acceleration is above a certain threshold
                        /*if (acceleration > ACCELERATION_THRESHOLD){
                            // create a new AlertDialog Builder
                            AlertDialog.Builder builder = new AlertDialog.Builder(Tela.this);

                            // set the AlertDialog's message
                            builder.setMessage(R.string.message_erase);
                            builder.setCancelable(true);

                            // add Erase Button
                            builder.setPositiveButton(R.string.button_erase,
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            dialogIsVisible.set(false);
                                            doodleView.clear(); // clear the screen
                                        } // end method onClick
                                    } // end anonymous inner class
                            ); // end call to setPositiveButton

                            // add Cancel Button
                            builder.setNegativeButton(R.string.button_cancel,
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            dialogIsVisible.set(false);
                                            dialog.cancel(); // dismiss the dialog
                                        } // end method onClick
                                    } // end anonymous inner class
                            ); // end call to setNegativeButton

                            dialogIsVisible.set(true); // dialog is on the screen
                            builder.show(); // display the dialog
                        } // end if*/
                        /*verifica a rotacao e apaga a tela RETIRADO DO PROJETO*/


                    } // end if
                } // end method onSensorChanged

                // required method of interface SensorEventListener

                public void onAccuracyChanged(Sensor sensor, int accuracy)
                {
                } // end method onAccuracyChanged
            }; // end anonymous inner class

    // displays configuration options in menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu); // call super's method

        // add options to menu
        /*menu.add(Menu.NONE, COLOR_MENU_ID, Menu.NONE,R.string.menuitem_color);
        menu.add(Menu.NONE, WIDTH_MENU_ID, Menu.NONE,R.string.menuitem_line_width);
        menu.add(Menu.NONE, ERASE_MENU_ID, Menu.NONE,R.string.menuitem_erase);*/

        menu.add(Menu.NONE, CLEAR_MENU_ID, Menu.NONE,R.string.menuitem_clear);
        menu.add(Menu.NONE, SAVE_MENU_ID, Menu.NONE,R.string.menuitem_save_image);


        this.carregaAssinatura();


        return true; // options menu creation was handled
    } // end onCreateOptionsMenu




    private void carregaAssinatura(){
        if(telaEnviada.equals("DadosAssinaturaMandado")){
            doodleView.clear();
        }else{
            SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
            if(nomeFuncaoWebservice.equals("registrarAssinaturaOficialAndroid")){
                //carrega assinatura oficial
                String USU_Assinatura = pref.getString("USU_Assinatura", null);
                CAMPO_ID = pref.getString("USU_CPF", null);
                if(USU_Assinatura.length()>0){
                    if(!USU_Assinatura.equals("null")){
                        doodleView.setAssinaturaOficial(USU_Assinatura);
                        doodleView.refreshTela();
                    }
                }
            }else{
                //carrega assinatura mandado
                Gson gsonMandados = new Gson();
                String jsonMandado = pref.getString("objectMandado", "");
                Mandado mandado = new Mandado();
                mandado = gsonMandados.fromJson(jsonMandado, Mandado.class);
                CAMPO_ID = mandado.getMAN_ID();
                if(!mandado.getMAN_Imagem_Receptor().equals("null")){
                    doodleView.setAssinaturaOficial(mandado.getMAN_Imagem_Receptor());
                    doodleView.refreshTela();

                }
            }
        }
    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        // switch based on the MenuItem id
        switch (item.getItemId())
        {
            /*case COLOR_MENU_ID:
                //showColorDialog(); // display color selection dialog
                return true; // consume the menu event
            case WIDTH_MENU_ID:
                //showLineWidthDialog(); // display line thickness dialog
                return true; // consume the menu event
            case ERASE_MENU_ID:
                //doodleView.setDrawingColor(Color.WHITE); // line color white
                return true; // consume the menu event*/
            case CLEAR_MENU_ID:
                doodleView.clear(); // clear doodleView
                return true; // consume the menu event

            case SAVE_MENU_ID:

                try {

                    //salva a assinatura do usario
                    progressDialog = ProgressDialog.show(Tela.this, "", "Salvando...");
                    progressDialog.setCancelable(true);
                    progressDialog.setCanceledOnTouchOutside(false);

                    doodleView.getImageBase64(); // save the current images
                    final String imgBase64 = doodleView.getImageBase64(); // save the current images

                    if(telaEnviada.equals("DadosAssinaturaMandado")){

                        //this.salvarDadosAssinaturaMandado(imgBase64);

                        Intent it = new Intent();
                        it.putExtra("assianturaCapturada", imgBase64);
                        setResult(Activity.RESULT_OK, it);

                        finish();
                        return true;

                    }else{

                        String URL = "https://www.judix.com.br/modulos/ws/index.php";

                        JSONObject objEnviar = new JSONObject();
                        objEnviar.put(nomeCampoWebservice, imgBase64);
                        objEnviar.put(idCampoWebservice, CAMPO_ID);

                        JSONArray arrayDados = new JSONArray();
                        arrayDados.put(0, objEnviar);

                        JSONObject jsonEnviar = new JSONObject();
                        jsonEnviar.put("mensagem",nomeFuncaoWebservice);
                        jsonEnviar.put("sucesso", "true");
                        jsonEnviar.put("dados", arrayDados);

                        RequestQueue queue = Volley.newRequestQueue(this);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                Request.Method.POST,
                                URL,
                                jsonEnviar,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try{

                                            String msgm = response.getString("mensagem");
                                            String sucesso = response.getString("sucesso");
                                            progressDialog.dismiss();

                                            Toast.makeText(Tela.this, msgm, Toast.LENGTH_SHORT).show();
                                            if(sucesso.equals("true")){

                                                SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                                if(nomeFuncaoWebservice.equals("registrarAssinaturaOficialAndroid")){

                                                    editor.putString("USU_Assinatura", String.valueOf(imgBase64));
                                                    editor.apply();
                                                }else{
                                                    //recupera o mandado
                                                    Gson gsonMandados = new Gson();
                                                    String jsonMandado = sharedPreferences.getString("objectMandado", "");
                                                    Mandado mandado = new Mandado();
                                                    mandado = gsonMandados.fromJson(jsonMandado, Mandado.class);
                                                    //seta a assiantura
                                                    mandado.setMAN_Imagem_Receptor(imgBase64);

                                                    //repassa o objeto pra sessao
                                                    Gson gson = new Gson();
                                                    String gsonMandado = gson.toJson(mandado);
                                                    editor.putString("objectMandado", String.valueOf(gsonMandado));
                                                    editor.apply();

                                                }

                                                saveImage();

                                                /*if(nomeFuncaoWebservice.equals("registrarAssinaturaMandadoAndroid")){                                                                                                        Intent it = new Intent();
                                                    it.putExtra("assianturaCapturada", imgBase64);
                                                    setResult(Activity.RESULT_OK, it);
                                                    finish();
                                                }else{
                                                    finish();
                                                }*/
                                                Intent it = new Intent();
                                                it.putExtra("assianturaCapturada", imgBase64);
                                                setResult(Activity.RESULT_OK, it);
                                                finish();


                                            }

                                        }catch (Exception e){
                                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                                            Toast.makeText(Tela.this, "Erro registrar assinatura: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(Tela.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        Log.d("JUDIX", "Error logarAndroid: " + error.toString());
                                        //error.printStackTrace();
                                    }
                                }) {
                        };

                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                                5000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        queue.add(jsonObjectRequest);
                        //queue.start();

                    }






                }catch (Exception e){
                    Toast.makeText(Tela.this, "Erro resposta do servidor: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("JUDIX", "Error logarAndroid: " + e.getMessage().toString());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                return true; // consume the menu event
        } // end switch

        return super.onOptionsItemSelected(item); // call super's method
    } // end method onOptionsItemSelected

    // display a dialog for selecting color
    private void showColorDialog()
    {
        // create the dialog and inflate its content
        currentDialog = new Dialog(this);
        currentDialog.setContentView(R.layout.color_dialog);
        currentDialog.setTitle(R.string.title_color_dialog);
        currentDialog.setCancelable(true);

        // get the color SeekBars and set their onChange listeners
        final SeekBar alphaSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.alphaSeekBar);
        final SeekBar redSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.redSeekBar);
        final SeekBar greenSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.greenSeekBar);
        final SeekBar blueSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.blueSeekBar);

        // register SeekBar event listeners
        alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);

        // use current drawing color to set SeekBar values
        final int color = doodleView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        // set the Set Color Button's onClickListener
        Button setColorButton = (Button) currentDialog.findViewById(R.id.setColorButton);
        setColorButton.setOnClickListener((View.OnClickListener) setColorButtonListener);

        dialogIsVisible.set(true); // dialog is on the screen
        currentDialog.show(); // show the dialog
    } // end method showColorDialog

    // OnSeekBarChangeListener for the SeekBars in the color dialog
    private SeekBar.OnSeekBarChangeListener colorSeekBarChanged =
            new SeekBar.OnSeekBarChangeListener()
            {

                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser)
                {
                    // get the SeekBars and the colorView LinearLayout
                    SeekBar alphaSeekBar =
                            (SeekBar) currentDialog.findViewById(R.id.alphaSeekBar);
                    SeekBar redSeekBar =
                            (SeekBar) currentDialog.findViewById(R.id.redSeekBar);
                    SeekBar greenSeekBar =
                            (SeekBar) currentDialog.findViewById(R.id.greenSeekBar);
                    SeekBar blueSeekBar =
                            (SeekBar) currentDialog.findViewById(R.id.blueSeekBar);
                    View colorView =
                            (View) currentDialog.findViewById(R.id.colorView);

                    // display the current color
                    colorView.setBackgroundColor(Color.argb(
                            alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                            greenSeekBar.getProgress(), blueSeekBar.getProgress()));
                } // end method onProgressChanged

                // required method of interface OnSeekBarChangeListener

                public void onStartTrackingTouch(SeekBar seekBar)
                {
                } // end method onStartTrackingTouch

                // required method of interface OnSeekBarChangeListener

                public void onStopTrackingTouch(SeekBar seekBar)
                {
                } // end method onStopTrackingTouch
            }; // end colorSeekBarChanged

    // OnClickListener for the color dialog's Set Color Button
    private DialogInterface.OnClickListener setColorButtonListener = new DialogInterface.OnClickListener()
    {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            // get the color SeekBars
            SeekBar alphaSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.alphaSeekBar);
            SeekBar redSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.redSeekBar);
            SeekBar greenSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.greenSeekBar);
            SeekBar blueSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.blueSeekBar);

            // set the line color
            doodleView.setDrawingColor(Color.argb(
                    alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                    greenSeekBar.getProgress(), blueSeekBar.getProgress()));
            dialogIsVisible.set(false); // dialog is not on the screen
            currentDialog.dismiss(); // hide the dialog
            currentDialog = null; // dialog no longer needed
        } // end method onClick
    }; // end setColorButtonListener

    // display a dialog for setting the line width
    private void showLineWidthDialog()
    {
        // create the dialog and inflate its content
        currentDialog = new Dialog(this);
        currentDialog.setContentView(R.layout.width_dialog);
        currentDialog.setTitle(R.string.title_line_width_dialog);
        currentDialog.setCancelable(true);

        // get widthSeekBar and configure it
        SeekBar widthSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.widthSeekBar);
        widthSeekBar.setOnSeekBarChangeListener(widthSeekBarChanged);
        widthSeekBar.setProgress(doodleView.getLineWidth());

        // set the Set Line Width Button's onClickListener
        Button setLineWidthButton =
                (Button) currentDialog.findViewById(R.id.widthDialogDoneButton);
        setLineWidthButton.setOnClickListener((View.OnClickListener) setLineWidthButtonListener);

        dialogIsVisible.set(true); // dialog is on the screen
        currentDialog.show(); // show the dialog
    } // end method showLineWidthDialog

    // OnSeekBarChangeListener for the SeekBar in the width dialog
    private SeekBar.OnSeekBarChangeListener widthSeekBarChanged =
            new SeekBar.OnSeekBarChangeListener()
            {
                Bitmap bitmap = Bitmap.createBitmap( // create Bitmap
                        400, 100, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap); // associate with Canvas


                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser)
                {
                    // get the ImageView
                    ImageView widthImageView = (ImageView)
                            currentDialog.findViewById(R.id.widthImageView);

                    // configure a Paint object for the current SeekBar value
                    Paint p = new Paint();
                    p.setColor(doodleView.getDrawingColor());
                    p.setStrokeCap(Paint.Cap.ROUND);
                    p.setStrokeWidth(progress);

                    // erase the bitmap and redraw the line
                    bitmap.eraseColor(Color.WHITE);
                    canvas.drawLine(30, 50, 370, 50, p);
                    widthImageView.setImageBitmap(bitmap);
                } // end method onProgressChanged

                // required method of interface OnSeekBarChangeListener

                public void onStartTrackingTouch(SeekBar seekBar)
                {
                } // end method onStartTrackingTouch

                // required method of interface OnSeekBarChangeListener

                public void onStopTrackingTouch(SeekBar seekBar)
                {
                } // end method onStopTrackingTouch
            }; // end widthSeekBarChanged

    // OnClickListener for the line width dialog's Set Line Width Button
    private DialogInterface.OnClickListener setLineWidthButtonListener =
            new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // get the color SeekBars
                    SeekBar widthSeekBar =
                            (SeekBar) currentDialog.findViewById(R.id.widthSeekBar);

                    // set the line color
                    doodleView.setLineWidth(widthSeekBar.getProgress());
                    dialogIsVisible.set(false); // dialog is not on the screen
                    currentDialog.dismiss(); // hide the dialog
                    currentDialog = null; // dialog no longer needed
                } // end method onClick
            }; // end setColorButtonListener













    public void saveImage()
    {

        OutputStream output;
        String recentImageInCache;

        String strDiretorio = Environment.getExternalStorageDirectory().toString()+"/Judix/";
        File dir = new File(strDiretorio);
        // Create a name for the saved image
        File file = new File(dir, "assinatura.png");
        try {

            output = new FileOutputStream(file);

            Bitmap bitmap = doodleView.getImageBitmap();
            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




        /*

        // use "Doodlz" followed by current time as the image file name
        String fileName = "assinatura" + System.currentTimeMillis();

        // create a ContentValues and configure new image's data
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        // get a Uri for the location to save the file
        Uri uri =  getContext().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try
        {
            // get an OutputStream to uri
            OutputStream outStream =  getContext().getContentResolver().openOutputStream(uri);

            // copy the bitmap to the OutputStream
            Bitmap bitmap = doodleView.getImageBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            // flush and close the OutputStream
            outStream.flush(); // empty the buffer
            outStream.close(); // close the stream

            // display a message indicating that the image was saved
            Toast message = Toast.makeText(getContext(),
                    R.string.message_saved, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show(); // display the Toast
        } // end try
        catch (IOException ex)
        {
            // display a message indicating that the image was saved
            Toast message = Toast.makeText(getContext(),
                    R.string.message_error_saving, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show(); // display the Toast
        } // end catch

        */

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentDeRetorno) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Intent intentDeRetornoi = new Intent();
                intentDeRetorno.putExtra("retorno", 1);
                int resultCodei = RESULT_OK;
                setResult (resultCodei, intentDeRetornoi);
                finish();
            }
        }
    }


/*    protected void salvarDadosAssinaturaMandado(String imgBase64){



        //recupera os dados da sessao


        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);

        final String ASM_TipoDocumento = pref.getString("ASM_TipoDocumento", null);
        final String ASM_Nome = pref.getString("ASM_Nome", null);
        final String ASM_Agente = pref.getString("ASM_Agente", null);
        final String ASM_NumeroDocumento = pref.getString("ASM_NumeroDocumento", null);
        final String MAN_ID = pref.getString("MAN_ID", null);
        final String USUARIO_ID= pref.getString("USUARIO_ID", null);
        final String imgBase64Interno = imgBase64;





        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();



        final ProgressDialog progressDialog = ProgressDialog.show(Tela.this, "", "Gravando a assinatura, aguarde...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        //final int MAN_ID, final int CEM_ID, final String MAN_CertidaoTexto


        String URL = "https://www.judix.com.br/modulos/ws/index.php";
        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados

        try {



            JSONArray arrayDados = new JSONArray();



            JSONObject objEnviarAssinatura = new JSONObject();

            objEnviarAssinatura.put("ASM_Assintura", imgBase64Interno);
            objEnviarAssinatura.put("ASM_Agente", ASM_Agente);
            objEnviarAssinatura.put("ASM_Nome", ASM_Nome);
            objEnviarAssinatura.put("ASM_TipoDocumento", ASM_TipoDocumento);
            objEnviarAssinatura.put("ASM_NumeroDocumento", ASM_NumeroDocumento);
            objEnviarAssinatura.put("MAN_ID", MAN_ID);
            objEnviarAssinatura.put("USUARIO_ID", USUARIO_ID );

            arrayDados.put(0, objEnviarAssinatura);

            jsonEnviar.put("mensagem","registrarAssinaturasMandadoAndroid");
            jsonEnviar.put("sucesso", "true");
            jsonEnviar.put("dados", arrayDados);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                jsonEnviar,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String msgm = response.getString("mensagem");
                            String sucesso = response.getString("sucesso");
                            progressDialog.dismiss();

                            if(sucesso.equals("true")){
                                Toast.makeText(Tela.this, msgm, Toast.LENGTH_SHORT).show();

                                Intent it = new Intent();
                                it.putExtra("assianturaCapturada", imgBase64Interno);
                                setResult(Activity.RESULT_OK, it);
                                finish();

                            }else{
                                Toast.makeText(Tela.this, msgm, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(Tela.this, "Erro converssao dados tela de dados assinatura mandado: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Tela.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        Log.d("JUDIX", "Error logarAndroid: " + error.toString());
                        //error.printStackTrace();
                    }
                }) {
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }*/

}

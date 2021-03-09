package com.app.alg.judix;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends Activity {

    private static final int MY_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_BLUETOOTH = 2;
    private static final int MY_ACCESS_NETWORK_STATE = 3;
    private static final int MY_CAMERA = 4;
    private static final int MY_WRITE_INTERNAL_STORAGE = 5;

    @TargetApi(Build.VERSION_CODES.M)
    @Override




    protected void onCreate(Bundle savedInstanceState) {

        //Log.w("before","Logcat save");
        // File logFile = new File( + "/log.txt" );
        try {
            super.onCreate(savedInstanceState);

            int currentapiVersion = android.os.Build.VERSION.SDK_INT;

            if (currentapiVersion >= 23) {

                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        //String a = "oi";
                    } else {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);

                    }

                }else{
                    try {
                        this.criaLogInicia();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.testaPermissaoBluethoot();
                }

            }


        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    private void criaLogInicia() throws IOException
    {

        Process process = Runtime.getRuntime().exec("logcat -d");
        process = Runtime.getRuntime().exec( "logcat -f " + "/storage/emulated/0/"+"JudixLog.txt");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);




        File fsd = Environment.getExternalStorageDirectory();
        String filePath = fsd.getAbsolutePath() + "/Judix";

        File diretorio = new File(filePath);
        if(!diretorio.exists()){
            diretorio.mkdirs();
        }


        /*String strDiretorio = Environment.getExternalStorageDirectory().toString()+"/Judix/";
        File diretorio = new File(strDiretorio);

        if (!diretorio.exists()) {
            diretorio.mkdirs(); //mkdir() cria somente um diretório, mkdirs() cria diretórios e subdiretórios.
        }*/

        //pra passar o objeto para o SharedPreferences transforma e m json
        SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // repassa o diretorio pra a
        Gson gson = new Gson();
        String gsonMandado = gson.toJson(diretorio);
        editor.putString("objectDiretorio", String.valueOf(gsonMandado));
        editor.apply();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
                Intent intent = new Intent();
                intent.setClass(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, 1000);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void testaPermissaoBluethoot()
    {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH)) {

                } else {

                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH},MY_BLUETOOTH);

                }

            }else{
                this.testarPermissaoInternet();
            }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void testarPermissaoInternet()
    {

        if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {

            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_NETWORK_STATE},MY_ACCESS_NETWORK_STATE);
            }
        }else{
            this.testaPermissaoCamera();
        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    private void testaPermissaoCamera()
    {

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},MY_CAMERA);
            }
        }else{
            try {
                this.criaLogInicia();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void testarPermissaoEscritaInterna()
    {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},23);
            }
        }else{

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    this.testaPermissaoBluethoot();
                } else {

                    finish();
                }
                return;
            }


            case MY_BLUETOOTH: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    this.testarPermissaoInternet();
                } else {

                    finish();
                }
                return;
            }


            case MY_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    try {
                        this.criaLogInicia();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}

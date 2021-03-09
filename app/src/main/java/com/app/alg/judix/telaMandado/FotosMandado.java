package com.app.alg.judix.telaMandado;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.alg.judix.R;
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.util.Constantes;
import com.app.alg.judix.util.FilesHandler;
import com.app.alg.judix.util.Funcoes;
import com.app.alg.judix.util.ImagensAdapter;
import com.app.alg.judix.util.RealPathUtil;
import com.app.alg.judix.util.novo;
import com.google.gson.Gson;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.provider.Telephony.Mms.Part.FILENAME;


public class FotosMandado extends AppCompatActivity{
    Mandado mandadoEscolhido;
    GridView img_grid;
    FloatingActionButton fab;
    private ImagensAdapter adapter;
    private Uri outputFileUri;
    private static final int REQUEST_CODE_PICTURE = 301;
    //private static final int REQUEST_CODE_PICTURE_KITKAT = 302;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_mandado);

        //identifica o mandado
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref.getString("objectMandado", "");
        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);

        String mandado = "Fotos do Mandado: " + mandadoEscolhido.getMAN_Numero_Processo();



        /*verifica se existe o diretorio se não tiver cria*/

        File f = new File(Environment.getExternalStorageDirectory() + "/judix/"+mandadoEscolhido.getMAN_ID().toString());
        if(!f.isDirectory()) {
            //cria a pasta com o id do mandado
            FilesHandler dirHandler = new FilesHandler();
            dirHandler.setDirMandado(mandadoEscolhido.getMAN_ID().toString());
            try {
                dirHandler.criarDiretorios();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        setTitle(mandado.toString());

        //Celulares com menos memoria tendem a finalizar a activity perdendo assim os dados em memoria
        if (savedInstanceState != null) {
            outputFileUri = savedInstanceState.getParcelable("outputFileUri");
        }

        img_grid = (GridView) findViewById(R.id.img_grid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FilesHandler dirHandler = new FilesHandler();
        dirHandler.setDirMandado(mandadoEscolhido.getMAN_ID().toString()+ File.separator+"Imgs");
        try {
            dirHandler.criarDiretorios();
        } catch (IOException e) {
            e.printStackTrace();
        }



        adapter = new ImagensAdapter(Environment.getExternalStorageDirectory() + File.separator +
                Constantes.APP_DIR + File.separator +
                mandadoEscolhido.getMAN_ID().toString() + File.separator + "Imgs" + File.separator, this);


        img_grid.setAdapter(adapter);
        img_grid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        img_grid.setMultiChoiceModeListener(new MultiChoiceModeListener());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageIntent();
            }
        });
    }




    private void openImageIntent() {
        // Determine Uri of camera image to save.
        String path = Environment.getExternalStorageDirectory() + File.separator +
                Constantes.APP_DIR + File.separator +
                mandadoEscolhido.getMAN_ID().toString() + File.separator+"Imgs" + File.separator;
        final File root = new File(path);
//        root.mkdirs();  //Se ainda não foi criado

        final String fname = (System.currentTimeMillis() / 1000) + ".jpg"; //Implemente sua logica de nomear arquivos
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = FotosMandado.this.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

//        if (Build.VERSION.SDK_INT <19){
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Selecione"), REQUEST_CODE_PICTURE);
//        } else {
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//            startActivityForResult(intent, REQUEST_CODE_PICTURE);
//        }

        // Filesystem
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Selecione");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, REQUEST_CODE_PICTURE);
    }









    //Celulares com menos memoria tendem a finalizar a activity perdendo assim os dados em memoria
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        icicle.putParcelable("outputFileUri", outputFileUri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Boolean addImagem=true;
        String caminhofinal ="";

        Uri selectedImageUri;

        if (resultCode == FotosMandado.RESULT_OK &&
                requestCode == REQUEST_CODE_PICTURE) {
            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                if (data.getData() == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    isCamera = action != null && action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                }
            }

            ;
            if (isCamera) {
                selectedImageUri = outputFileUri;
            } else {
                //copia o arquivo que está no sistema de arquivos para a pasta de imagens do app
                try {


                    selectedImageUri = data.getData();
                    //this.appendLog("URI", selectedImageUri.toString());
                    //this.appendLog("arquivoOrigem", "file://"+novo.getPath(FotosMandado.this, selectedImageUri));
                    //this.appendLog("arquivoDestino", "file://"+novo.getPath(this, outputFileUri));


                    //this.appendLog("arquivoDestino", FilesHandler.getImagePath(outputFileUri, this));
                    ;

                    //this.appendLog("origemURI", selectedImageUri.toString());
                    //this.appendLog("origem", FilesHandler.getImagePath(selectedImageUri, this));
                    //this.appendLog("destinoURI", outputFileUri.toString());
                    //this.appendLog("destino", FilesHandler.getImagePath(outputFileUri, this));

                    FilesHandler.copyFile(FilesHandler.getImagePath(selectedImageUri, this), FilesHandler.getImagePath(outputFileUri, this));
                    //FilesHandler.copyFile(RealPathUtil.getFilePathNovo(this, selectedImageUri), RealPathUtil.getFilePathNovo(this, outputFileUri));


                    //FilesHandler.copyFile(novo.getPath(this, selectedImageUri), novo.getPath(this, outputFileUri));

                    ///*if (URLUtil.isValidUrl(String.valueOf(outputFileUri))) {
                        //adapter.addItem(outputFileUri.toString());
                        //adapter.notifyDataSetChanged();
                        //Log.d("selectedImageUri ", selectedImageUri.toString());
                    //}*/



                } catch (FileNotFoundException e){
                    addImagem=false;
                    Toast.makeText(FotosMandado.this, " Caminho de imagem invalida!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    addImagem=false;
                    Toast.makeText(FotosMandado.this, " Imagem inválida!", Toast.LENGTH_SHORT).show();
                    //e.printStackTrace();
                }
                selectedImageUri = outputFileUri;









                /*if (addImagem) {
                    adapter.addItem(outputFileUri.toString());
                    adapter.notifyDataSetChanged();
                }*/
            }
            if (addImagem) {

                //redimsneiona a imagem

                try {
                    Bitmap bitmap = null;
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputFileUri);
                    bitmap = Funcoes.scaleDown(bitmap, 400, false);

                    SaveImage(bitmap, FilesHandler.getImagePath(outputFileUri, this));

                    adapter.addItem(selectedImageUri.toString());
                    adapter.notifyDataSetChanged();
                    Log.d("selectedImageUri ", selectedImageUri.toString());

                } catch (IOException e) {
                    addImagem=false;
                    Toast.makeText(FotosMandado.this, " Erro ao redimensionar a Imagem!", Toast.LENGTH_SHORT).show();
                }



            }
        }
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            FotosMandado.this.getMenuInflater().inflate(R.menu.menu_cab_edit_doc, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            adapter.clearSelection();
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_item_delete:
                    boolean deleted = true;
                    final CountDownTimer cdt;
                    final int itens = adapter.getSelectedCount();
                    Toast.makeText(FotosMandado.this, itens + " removidos", Toast.LENGTH_SHORT).show();

//                    final ArrayList<String> toDelete = new ArrayList<>();
//                    for (int i = 0; i < itens; i++) {
//                        toDelete.add(adapter.getItem(adapter.getSelected(i)));
//                    }

                    for (int i = 0; i < itens; i++) {

                        String[] parts = adapter.getItem(adapter.getSelected(i)).split("/");
                        Integer tot = parts.length;
                        String nomeArquivo = parts[parts.length - 1];
                        String pasta = parts[parts.length - 3];

                        String caminhoExclusao = "/"+parts[parts.length - 7]+"/"+parts[parts.length - 6]+"/"+parts[parts.length - 5]+"/"+parts[parts.length - 4]+"/"+parts[parts.length - 3]+"/"+parts[parts.length - 2]+"/"+parts[parts.length - 1];


                        File fileDeletede = new File(caminhoExclusao);
                        if(fileDeletede.exists()) {
                            deleted = fileDeletede.delete();
                        }


                    }

                    if (deleted) {
                        adapter = new ImagensAdapter(Environment.getExternalStorageDirectory() + File.separator +
                                Constantes.APP_DIR + File.separator +
                                mandadoEscolhido.getMAN_ID().toString() + File.separator + "Imgs" + File.separator, FotosMandado.this);
//                        adapter.clearSelection();
                        mode.finish();
                        adapter.notifyDataSetChanged();
                        img_grid.invalidateViews();
                        img_grid.setAdapter(adapter);

                    }

                    return true;


//                    Snackbar snackbar = Snackbar.make(fab, "Deseja desfazer?", Snackbar.LENGTH_LONG);
//                    cdt = new CountDownTimer(snackbar.getDuration(), 1000) {
//                        public void onTick(long millisUntilFinished) { }
//
//                        public void onFinish() {
//                            for (int i = 0; i < toDelete.size(); i++) {
//                                File file = new File(toDelete.get(i));
//                                file.delete();
//                            }
//                        }
//                    };
//
//                    snackbar.setAction("Desfazer", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            cdt.cancel();
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
//                    snackbar.show();
//                    cdt.start();


                case R.id.menu_item_upload:
                    final int itens2 = adapter.getSelectedCount();
                    File fileUpload = null;
                    for (int i = 0; i < itens2; i++) {
                        fileUpload = new File(adapter.getItem(adapter.getSelected(i)));
                    }
                    //this.execUpload(fileUpload);
            }
            return true;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {
            // TODO Auto-generated method stub
            int selectCount = img_grid.getCheckedItemCount();
            switch (selectCount) {
                case 1:
                    mode.setTitle("" + selectCount);
                    break;
                default:
                    mode.setTitle("" + selectCount);
                    break;
            }

            if (checked) {
                adapter.addSelection(position);
            } else {
                int count = adapter.getSelectedCount();
                for (int i = 0; i < count; i++) {
                    if (adapter.getSelected(i) == position)
                        adapter.removeSelection(i);
                }


//                ArrayList<Integer> indexes = adapter.getSelecteds();
//                for (Integer i : indexes ) {
//                    adapter.removeSelection(i);
//                }
            }
        }


    }

    public void appendLog(String titulo , String text)
    {
        File logFile = new File("sdcard/"+titulo+".txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }






    //nao excluir
    private void SaveImage(Bitmap finalBitmap, String caminhoArquivo) {


        String[] parts = caminhoArquivo.split("/");
        Integer tot = parts.length;
        String nomeArquivo = parts[parts.length - 1];
        String pasta = parts[parts.length - 3];

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Judix/" + pasta + "/Imgs/");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File fileCriar = new File(myDir, nomeArquivo);
        File fileExcluir = new File(myDir, nomeArquivo);
        if (fileExcluir.exists()){
            fileExcluir.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(fileCriar);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void _SaveImage(Bitmap finalBitmap, String caminhoArquivo) {

        /*String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/judix/"+caminhoArquivo);
        myDir.mkdirs();
        String fname = nomeArquivo;*/

        /*String string = "004-034556";
        String[] parts = string.split("-");
        String part1 = parts[0]; // 004
        String part2 = parts[1]; // 034556*/

        String[] parts = caminhoArquivo.split("/");
        Integer tot = parts.length;
        String nomeArquivo = parts[parts.length-1];
        String pasta = parts[parts.length-3];


        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/Judix/"+"/"+pasta+"/Imgs/"+nomeArquivo);
        myDir.mkdirs();
        String fname = nomeArquivo;


        /*FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
        fos.write(string.getBytes());
        fos.close();*/




        //File file = new File (caminhoArquivo);
        File file = new File (fname);
        if (file.exists ()) {
            file.delete();
        }
        try {
            //FileOutputStream out = new FileOutputStream(file);
            FileOutputStream out = openFileOutput(String.valueOf(file), Context.MODE_PRIVATE);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }*/





}
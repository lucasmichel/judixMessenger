package com.app.alg.judix.telaCertidoes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.alg.judix.R;
import com.app.alg.judix.model.Certidao;
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.telaMandado.FotosMandado;
import com.app.alg.judix.telaMandado.MandadoActivity;
import com.app.alg.judix.util.Constantes;
import com.app.alg.judix.util.Funcoes;
import com.app.alg.judix.util.MyVolleyAsyncTask;
import com.app.alg.judix.util.TesteVoley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CertidaoDoMandado extends AppCompatActivity implements View.OnClickListener {


    public static Activity activityCertidaoDoMandado;



    //String USU_Assinatura;
    Mandado mandadoEscolhido;
    Certidao certidaoEscolhida;


    TextView textNumProcesso;
    TextView textViewIdProcesso;
    TextView textViewNomeCertidao;
    TextView textViewTipoCertidao;
    TextView textNomeDestinatario;
    TextView textCabecalho;
    TextView textRodape;
    TextView textLocalDataHora;
    TextView textNomeOficial;

    TextView editTextTexto;

    Button buttonColherAssinaturas;
    Button buttonAlterarTextoCertidao;

    String textoRetornoDialogCertidao;
    String USU_ID;
    String USU_Assinatura;

    private String KEY_IMAGE = "image";
    private String KEY_EXTENSION = "extension";
    private String ID_MANDADO_TRANSMISSAO_FOTO = "idMandado";
    private String FOTO_NOME = "fotoNome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityCertidaoDoMandado = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certidao_do_mandado);
        setTitle("Certidão de Oficialato");

        textNumProcesso = (TextView) findViewById(R.id.textViewNumProcesso);
        textViewIdProcesso = (TextView) findViewById(R.id.textViewIdProcesso);
        textNomeDestinatario = (TextView) findViewById(R.id.textViewDestinatario);
        textCabecalho = (TextView) findViewById(R.id.textViewCabecalho);
        textRodape = (TextView) findViewById(R.id.textViewRodape);
        textLocalDataHora = (TextView) findViewById(R.id.textViewLocalDataHora);
        textNomeOficial = (TextView) findViewById(R.id.textViewNomeOficial);
        textViewNomeCertidao = (TextView) findViewById(R.id.textViewNomeCertidao);
        textViewTipoCertidao = (TextView) findViewById(R.id.textViewTipoCertidao);
        editTextTexto = (TextView) findViewById(R.id.editTextTexto);

        buttonColherAssinaturas = (Button) findViewById(R.id.buttonColherAssinaturas);
        buttonColherAssinaturas.setOnClickListener(this);

        buttonAlterarTextoCertidao = (Button) findViewById(R.id.buttonAlterarTextoCertidao);
        buttonAlterarTextoCertidao.setOnClickListener(this);

        //identifica o mandado
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref.getString("objectMandado", "");
        String jsonMandado2  = jsonMandado;
        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);

        //identifica a certidao
        Gson gsonCertidao = new Gson();
        String jsonCertidao = pref.getString("objectCertidao", "");
        this.certidaoEscolhida = gsonCertidao.fromJson(jsonCertidao, Certidao.class);

        USU_Assinatura = pref.getString("USU_Assinatura", "");

        String USU_Nome = pref.getString("USU_Nome", "");
        USU_ID = pref.getString("USU_ID", "");

        textNumProcesso.setText("Número processo: "+this.mandadoEscolhido.getMAN_Numero_Processo().toString());
        textViewIdProcesso.setText("Id Judix: "+this.mandadoEscolhido.getMAN_ID().toString());
        textNomeDestinatario.setText(this.mandadoEscolhido.getMAN_Tipo_Destinatario().toString()+": "+this.mandadoEscolhido.getMAN_Destinatario().toString());
        textCabecalho.setText(this.certidaoEscolhida.getCabecalho().toString());
        editTextTexto.setText(this.certidaoEscolhida.getTexto());
        textRodape.setText(this.certidaoEscolhida.getRodape());
        textNomeOficial.setText(USU_Nome);

        String tipo = "";
        if(this.certidaoEscolhida.getTipo().equals("P")){
            tipo = "POSITIVA";
        }else{
            tipo = "NEGATIVA";
        }
        textViewNomeCertidao.setText(this.certidaoEscolhida.getNome());
        textViewTipoCertidao.setText(tipo);
        if(USU_Assinatura.length() > 0){
            if(!USU_Assinatura.equals("null")) {
                //this.carregarAssinaturaOficial(USU_Assinatura);
            }
        }
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonColherAssinaturas:

                //this.entregaMandado("oi");
                /*if(textoRetornoDialogCertidao!=null){
                    if(textoRetornoDialogCertidao.length()>1){
                        this.entregaMandado();
                        this.execSalvarCertidao(textoRetornoDialogCertidao);
                    }else{
                        this.entregaMandado();
                        this.execSalvarCertidao(editTextTexto.getText().toString());
                    }
                }else{

                    this.execSalvarCertidao(editTextTexto.getText().toString());
                }*/

                if(this.USU_Assinatura.length()<=0){
                    Toast toast = Toast.makeText(this,"Impossivel a tranmissão do mandado! Oficial sem assinatura cadastrada!", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    this.execSalvarCertidao(editTextTexto.getText().toString());
                    break;
                }




            case R.id.buttonAlterarTextoCertidao:
                //listaMandados();

                SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("textoCertidaoEscolhida", this.certidaoEscolhida.getTexto().toString());
                editor.apply();

                Intent intent = new Intent(CertidaoDoMandado.this, dialog_salvar_alteracao_certidao.class);

                intent.putExtra("textoCertidaoEscolhida", this.certidaoEscolhida.getTexto().toString());
                startActivityForResult(intent, 1);



                break;

            default:
                break;
        }
    }


    private List<JSONObject> entregaMandado(){
        //manda as fotos
        boolean retorno = true;

        //conta as fotos
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator +
                Constantes.APP_DIR + File.separator +
                mandadoEscolhido.getMAN_ID().toString() + File.separator + "Imgs" );
        File[] files = dir.listFiles();


        List<JSONObject> listaJsonObjectFotos = new ArrayList<JSONObject>();

        if(files != null){
            if(files.length>0){
                //Toast.makeText(CertidaoDoMandado.this, "Total foto: "+files.length, Toast.LENGTH_SHORT).show();


                for(int intFoto=0; intFoto < files.length; intFoto++) {
                    File foto = files[intFoto];
                    foto.getAbsolutePath();

                    try {
                        JSONObject fotoJson = this.enviarFoto(foto, "Foto_" + intFoto);

                        listaJsonObjectFotos.add(fotoJson);
                        //retorno = true;

                        //this.enviaFoto(foto, "Foto:"+intFoto); ANTIGO NÂO USAR
                    } catch (Exception e) {
                        //retorno = false;
                        e.printStackTrace();
                    }
                }
            }

        }
        return listaJsonObjectFotos;

    }

    /*public void enviaFoto(File fileUpload, String textoDialog){
        Bitmap bitmap = BitmapFactory.decodeFile(fileUpload.getPath());
        final String extension = fileUpload.getPath().substring(fileUpload.getPath().lastIndexOf(".") + 1, fileUpload.getPath().length());
        String base64=null;
        if(extension.equals("jpg")){
            base64 = Funcoes.getStringImageJPG(bitmap);
        }else{
            base64 = Funcoes.getStringImagePNG(bitmap);
        }
        final String textoBASE64 = base64;
        final String idMandado = this.mandadoEscolhido.getMAN_ID();
        final String nomeFoto = textoDialog+"."+extension;

        final File fileUploadInterno = fileUpload;
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(CertidaoDoMandado.this,"Enviando...",textoDialog,false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://www.judix.com.br/modulos/ws/recebeFoto.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(CertidaoDoMandado.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(CertidaoDoMandado.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Getting Image Name
                String name = "uploadImagem";

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, textoBASE64);
                params.put(KEY_EXTENSION, extension);
                params.put(ID_MANDADO_TRANSMISSAO_FOTO, idMandado);
                params.put(FOTO_NOME, nomeFoto);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(CertidaoDoMandado.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }*/




    private JSONObject enviarFoto(File fileUpload, String textoDialog){

        //Toast.makeText(CertidaoDoMandado.this, "Enviando "+textoDialog, Toast.LENGTH_SHORT).show();

        Bitmap bitmap = BitmapFactory.decodeFile(fileUpload.getPath());
        final String extension = fileUpload.getPath().substring(fileUpload.getPath().lastIndexOf(".") + 1, fileUpload.getPath().length());
        String base64=null;
        if(extension.equals("jpg")){
            base64 = Funcoes.getStringImageJPG(bitmap);
        }else{
            base64 = Funcoes.getStringImagePNG(bitmap);
        }
        final String textoBASE64 = base64;
        final String idMandado = this.mandadoEscolhido.getMAN_ID();
        final String nomeFoto = textoDialog+"."+extension;

        //final File fileUploadInterno = fileUpload;

        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();

        /*final ProgressDialog progressDialog = ProgressDialog.show(CertidaoDoMandado.this, "", "Gravando a certidão, aguarde...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);*/

        //final int MAN_ID, final int CEM_ID, final String MAN_CertidaoTexto


        //String URL = "https://www.judix.com.br/modulos/ws/index.php";
        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados

        try {
            objEnviar.put("MAN_ID", idMandado);
            objEnviar.put("FOT_Endereco", textoBASE64);
            objEnviar.put("nomeFoto", nomeFoto);
            objEnviar.put("extension", extension);


            /*JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            jsonEnviar.put("mensagem","registrarFotoMandado");
            jsonEnviar.put("sucesso", "true");
            jsonEnviar.put("dados", arrayDados);*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return objEnviar;

        /*
        final String strNomeFoto =textoDialog;
        RequestQueue queue = Volley.newRequestQueue(this);

        //RequestFuture<JSONObject> future = RequestFuture.newFuture();


        //https://stackoverflow.com/questions/41724692/android-volley-synchronous-request-not-working
        //https://stackoverflow.com/questions/16904741/can-i-do-a-synchronous-request-with-volley
        //https://www.google.com.br/search?q=android+volley+synchronous+request&oq=request+volley+asinc&aqs=chrome.1.69i57j0.18879j0j7&sourceid=chrome&ie=UTF-8


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
                            //progressDialog.dismiss();

                            if(sucesso.equals("true")){
                                Toast.makeText(CertidaoDoMandado.this, "Foto "+strNomeFoto+" eniviada com sucesso!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(CertidaoDoMandado.this, msgm, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            //progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(CertidaoDoMandado.this, "Erro converssao envio fotos mandado: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(CertidaoDoMandado.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
        //queue.start();*/
    }















    private void execSalvarCertidao(final String MAN_CertidaoTexto){

        //recebe uma lista de fotos pra mandar junto no post
        List<JSONObject> listaJsonObjectFotos = this.entregaMandado();
        List<JSONObject> listaJsonObjectFotos2 = listaJsonObjectFotos;



        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();

        final ProgressDialog progressDialog = ProgressDialog.show(CertidaoDoMandado.this, "", "Gravando a certidão, aguarde...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        //final int MAN_ID, final int CEM_ID, final String MAN_CertidaoTexto


        String URL = "https://www.judix.com.br/modulos/ws/index.php";
        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados

        try {


            objEnviar.put("MAN_CertidaoTexto", MAN_CertidaoTexto.toString());
            objEnviar.put("MAN_ID", mandadoEscolhido.getMAN_ID().toString());
            objEnviar.put("CEM_ID", certidaoEscolhida.getId().toString());
            objEnviar.put("USUARIO_ID", USU_ID.toString() );


            //JSONArray arrayDados = new JSONArray();


            String fotoEnviar = "";

            for(int intI=0; intI<listaJsonObjectFotos.size(); intI++ ){
                JSONObject foto = new JSONObject();
                foto = listaJsonObjectFotos.get(intI);

                //JSONObject objEnviarAssinatura = new JSONObject();

                //arrayDados.put(intI, foto);
                //objEnviar.put(intI, foto );

                fotoEnviar += foto.get("MAN_ID").toString()+"|"+foto.get("nomeFoto").toString()+"|"+foto.get("extension").toString()+"|"+foto.get("FOT_Endereco").toString()+"#";

            }
            objEnviar.put("FOTO_", fotoEnviar );


            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);


            jsonEnviar.put("mensagem","registrarCertidaoMandadoAndroid");
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
                                Toast.makeText(CertidaoDoMandado.this, "Transmissão realizada com sucesso!", Toast.LENGTH_SHORT).show();
                                //abre a tela de gerencia de assinatura..
                                /*Intent intentAssinaturas = new Intent(CertidaoDoMandado.this, ListaAssinaturasMandadoActivity.class);
                                startActivity(intentAssinaturas);*/

                                Intent intentDeRetorno = new Intent();
                                intentDeRetorno.putExtra("retorno", 1);
                                int resultCode = RESULT_OK;
                                setResult (resultCode, intentDeRetorno);
                                finish();


                                //MandadoActivity.activityMandadoActivity.finish();
                                //ListaCertidoes.activityListaCertidoes.finish();
                                //finish();
                            }else{
                                Toast.makeText(CertidaoDoMandado.this, msgm, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(CertidaoDoMandado.this, "Erro converssao dados tela certidao mandado: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(CertidaoDoMandado.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int a = requestCode;


        switch (requestCode) {
            case 1:
                textoRetornoDialogCertidao = data.getExtras().getString("textoAlteracaoCertidao");
                editTextTexto.setText(textoRetornoDialogCertidao);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {

        Intent intentDeRetorno = new Intent();
        intentDeRetorno.putExtra("retorno", 1);
        int resultCode = RESULT_OK;
        setResult (resultCode, intentDeRetorno);
        finish();

        //return;
    }
}

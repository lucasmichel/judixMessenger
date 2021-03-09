package com.app.alg.judix.telaMandado;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.app.alg.judix.model.Oficial;
import com.app.alg.judix.telaArquivosAnexos.ListaArquivosAnexos;
import com.app.alg.judix.telaAssinaturasMandado.ListaAssinaturasMandadoActivity;
import com.app.alg.judix.telaCertidoes.ListaCertidoes;
import com.app.alg.judix.util.FilesHandler;
import com.app.alg.judix.util.Funcoes;
import com.app.alg.judix.util.Mask;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


//import com.github.barteksc.pdfviewer.PDFView;
//import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
//import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
//import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
//import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
//import com.github.barteksc.pdfviewer.util.FitPolicy;
//import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MandadoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {


    public static Activity activityMandadoActivity;

    RelativeLayout layout;
    Mandado mandadoEscolhido;
    String idOficialLogado;

    String strCaminhoPDF = null;
    File dirCache=null;

    ProgressDialog mProgressDialog;

    Integer pageNumber = 1;

    //private static final int EXTERNAL_STORAGE_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityMandadoActivity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandado);

        //identifica o mandado
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref.getString("objectMandado", "");
        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);

        //String nomeUsuario = "Oficial: "+pref.getString("USU_Nome", null);
        this.idOficialLogado = pref.getString("USU_ID","");


        this.carregarMandado();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);//para os icones coloridos
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void carregarMandado(){

        //define se carrega um pdf ou webview
        if(this.mandadoEscolhido.getMAN_ArquivoTipo().equals("T")){
            //Toast.makeText(MandadoActivity.this, "abre texto web", Toast.LENGTH_SHORT).show();
            this.carregarHTML();
        }else{
            //Toast.makeText(MandadoActivity.this, "abre texto PDF" , Toast.LENGTH_SHORT).show();
            this.carregarPDF();
        }

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void carregarPDF(){



        //usar este pdf

        //https://github.com/barteksc/AndroidPdfViewer



        File pdfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+mandadoEscolhido.getMAN_ID()+".pdf");


        if(pdfile.exists()){
            PDFView pdfView = (PDFView) findViewById(R.id.pdfView);

            pdfView.fromFile(pdfile)
                    .defaultPage(pageNumber)
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageNumber = page;
                        }
                    })
                    .load();
        }else{
            Toast.makeText(MandadoActivity.this,"Arquivo nao encontrado no servidor!", Toast.LENGTH_LONG).show();
            this.finish();
        }




        /*
        layout = (RelativeLayout) findViewById(R.id.relativeLayoutMandado);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        ImageButton btPDF =new ImageButton(getApplicationContext());
        btPDF.setImageResource(R.mipmap.ic_action_foursquare);
        btPDF.setLayoutParams(params);

        btPDF.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // it was the 1st button

                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Judix/"+mandadoEscolhido.getMAN_ID()+".pdf");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);

                        //Toast.makeText(MandadoActivity.this,"Aqui: ", Toast.LENGTH_LONG).show();
                    }
                }
        );

        layout.addView(btPDF);*/
    }

    private void carregarHTML(){
        final ProgressDialog pd = ProgressDialog.show(MandadoActivity.this, "", "Carregando mandado...", true);

        layout = (RelativeLayout) findViewById(R.id.relativeLayoutMandado);
        final WebView mWebview =new WebView(getApplicationContext());

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //Toast.makeText(MandadoActivity.this, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pd.show();
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                pd.dismiss();

                //String webUrl = mWebview.getUrl();

            }

        });
        mWebview.loadUrl("http://www.judix.com.br/modulos/ws/verArquivoHtmlMandado.php?MAN_ID=" + this.mandadoEscolhido.getMAN_ID());
        layout.addView(mWebview);
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
        getMenuInflater().inflate(R.menu.mandado, menu);
        String mandadoId = "ID Judix: "+mandadoEscolhido.getMAN_ID();
        String mandadoNumero = "Proc.: "+mandadoEscolhido.getMAN_Numero_Processo();
        String medida= "Medida: "+ mandadoEscolhido.getMAN_MedidaJudicial();
        String destinatiario= "Destinatário: "+ mandadoEscolhido.getMAN_Destinatario();
        String data= "Data: "+ mandadoEscolhido.getMAN_DataHoraCadastro();

        //setTitle(mandado.toString());

        TextView textViewMenuMandadoID = (TextView) findViewById(R.id.textViewMenuMandadoID);
        textViewMenuMandadoID.setText(mandadoId.toString());

        TextView textViewMenuNumeroMandado = (TextView) findViewById(R.id.textViewMenuMandadoNumero);
        textViewMenuNumeroMandado.setText(mandadoNumero.toString());

        TextView textViewMenuMedidaMandado = (TextView) findViewById(R.id.textViewMenuMandadoMedidaJuducial);
        textViewMenuMedidaMandado.setText(medida.toString());

        TextView textViewMenuMandadoDestinatario = (TextView) findViewById(R.id.textViewMenuMandadoDestinatario);
        textViewMenuMandadoDestinatario.setText(destinatiario.toString());

        TextView textViewMenuMandadoRecepcao = (TextView) findViewById(R.id.textViewMenuMandadoRecepcao);
        textViewMenuMandadoRecepcao.setText(data.toString());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.openAssinaturaMandado) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.toString()){

            case "Dados Receptor":

                Intent intentDoc = new Intent(MandadoActivity.this, DocumentoDestinatarioMandado.class);
                startActivity(intentDoc);

                //this.abrirRota(mandadoEscolhido);
                //Toast.makeText(MandadoActivity.this, "falta implementar: " , Toast.LENGTH_SHORT).show();
                //return true; // consume the menu event


                drawer.closeDrawer(GravityCompat.START); //pra fechar o menu
                return true;

            case "Recolher Assinaturas":
                /*Toast toast = Toast.makeText(this,"Assinatura mandado", Toast.LENGTH_LONG);
                toast.show();

                SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nomeFuncaoWebservice", "registrarAssinaturaMandadoAndroid");
                editor.putString("nomeCampoWebservice", "MAN_Imagem_Receptor");
                editor.putString("idCampoWebservice", "MAN_ID");
                editor.apply();

                Intent intent = new Intent(MandadoActivity.this, Tela.class);
                intent.putExtra("telaEnviada", "Mandado");
                startActivityForResult(intent, 1);*/

                Intent intentAssinaturas = new Intent(MandadoActivity.this, ListaAssinaturasMandadoActivity.class);
                startActivity(intentAssinaturas);


                drawer.closeDrawer(GravityCompat.START); //pra fechar o menu
                return true;


            case "Imprimir Mandado":
                Toast.makeText(MandadoActivity.this, "Ininciando a impressão..." , Toast.LENGTH_SHORT).show();
                //return true; // consume the menu event

                this.executaImprimir();
                this.execRegistrarImpressaoMandadoAndroid();

                drawer.closeDrawer(GravityCompat.START); //pra fechar o menu
                return true;

            case "Anexar Fotos":
                Intent intentFoto = new Intent(MandadoActivity.this, FotosMandado.class);
                startActivity(intentFoto);
                //return true; // consume the menu event


                drawer.closeDrawer(GravityCompat.START); //pra fechar o menu
                return true;

            case "Emitir Certidão":
                Intent intentCertidao = new Intent(MandadoActivity.this, ListaCertidoes.class);
                startActivityForResult(intentCertidao, 1);
                //return true; // consume the menu event

                drawer.closeDrawer(GravityCompat.START); //pra fechar o menu
                //this.finish();
                return true;

            case "Documentos Anexos":







                Intent intentArquivoAnexo = new Intent(MandadoActivity.this, ListaArquivosAnexos.class);
                intentArquivoAnexo.putExtra("compartilhar", false);
                startActivityForResult(intentArquivoAnexo, 1);
                drawer.closeDrawer(GravityCompat.START); //pra fechar o menu
                //this.finish();
                return true;



            case "Compartilhar":


                //LayoutInflater inflater = getLayoutInflater();


//mudar esse laout pra escolher entre zap ou entre email
                AlertDialog alerta;

                // Get the layout inflater
                LayoutInflater inflater = getLayoutInflater();


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //builder.setView(inflater.inflate(R.layout.layout_dialog, null, false));


                String[] items = {"Whatsapp","E-mail"};

                int checkedItem = -1;
                builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Toast.makeText(MandadoActivity.this, "Whatsapp", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                Toast.makeText(MandadoActivity.this, "E-mail", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });




                View view = (View) inflater.inflate(R.layout.layout_dialog, null);
                builder.setCustomTitle(view);

                TextView tituloDialog = (TextView) view.findViewById(R.id.tituloDialog);

                tituloDialog.setText("Compartilhar Mandado");

                //builder.setTitle("Comartilhar com whatsapp");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                input.addTextChangedListener(Mask.insert(Mask.CELULAR_MASK, input));
                //validar telefone..

                try {
                    String fone = null;
                    if( this.mandadoEscolhido.getMAN_FoneCompartilhamentoZap().toString()!=null ){
                        if(!this.mandadoEscolhido.getMAN_FoneCompartilhamentoZap().toString().equals("NULL")){
                            if(!this.mandadoEscolhido.getMAN_FoneCompartilhamentoZap().toString().equals("null")){
                                if( this.mandadoEscolhido.getMAN_FoneCompartilhamentoZap().toString().length() > 0){
                                    fone = this.mandadoEscolhido.getMAN_FoneCompartilhamentoZap().toString();
                                }
                            }
                        }
                    }
                    input.setInputType(InputType.TYPE_CLASS_PHONE);
                    input.setText(fone);
                }catch (Exception e){
                    e.getMessage();
                }





                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try{
                            Funcoes func = new Funcoes();

                            String numeroPronto = "55"+input.getText().toString();
                            numeroPronto = Funcoes.retirarPontuacao(numeroPronto);

                            //String urlEnviar = "https://wa.me/"+numeroPronto+"/?text=https://judix.com.br/ci/site/mandado/"+mandadoEscolhido.getMAN_ID();
                            //String url = "https://api.whatsapp.com/send/?phone=5581989838324&text=Ol%C3%A1%2C+gostaria+de+um+or%C3%A7amento.&app_absent=0";


                            String urlEnviar = "https://api.whatsapp.com/send?phone="+numeroPronto+"&text=https://judix.com.br/ci/site/mandado/"+mandadoEscolhido.getHash()+"&app_absent=0";
                            //String urlEnviar = "https://api.whatsapp.com/send?phone="+numeroPronto+"&text=BALAIO_DE_GATO&app_absent=0";




                            // String urlEnviar = "https://wa.me/"+numeroPronto+"&text=https://judix.com.br/ci/site/mandado/"+mandadoEscolhido.getMAN_ID();
                            //String urlEnviar = "https://wa.me/"+numeroPronto+"&text=TESTE_DA_GOMA";
                            //String urlEnviar2 = urlEnviar;

                            if(func.checkConexao(MandadoActivity.this)){


                                String toNumber = numeroPronto; // contains spaces.
                                toNumber = toNumber.replace("+", "").replace(" ", "");






                                //String mensaje = "https://judix.com.br/ci/site/mandado/"+mandadoEscolhido.getMAN_ID();
                                //String url = "https://api.whatsapp.com/send?phone="+ toNumber +"&text=" + URLEncoder.encode(mensaje, "UTF-8");
                                //sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                                //sendIntent.putExtra(Intent.EXTRA_TEXT, urlEnviar);

                                Toast toast = Toast.makeText(MandadoActivity.this,urlEnviar, Toast.LENGTH_LONG);
                                toast.show();
                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                sendIntent.setData(Uri.parse(urlEnviar));
                                sendIntent.setPackage("com.whatsapp");
                                //sendIntent.putExtra(Intent.EXTRA_TEXT, urlEnviar);
                                startActivity(sendIntent);


                                /*Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                sendIntent.setType("text/plain");
                                sendIntent.setPackage("com.whatsapp");
                                sendIntent.putExtra(Intent.EXTRA_TEXT, urlEnviar);
                                startActivity(sendIntent);*/

                                //Intent shareIntent = Intent.createChooser(sendIntent, null);
                                //startActivity(shareIntent);




                                /*Intent sendIntent = new Intent("android.intent.action.MAIN");
                                sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                                //sendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, toNumber + "@s.whatsapp.net");
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://judix.com.br/ci/site/mandado/"+mandadoEscolhido.getMAN_ID());

                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.setPackage("com.whatsapp");
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);*/

                                MandadoActivity a = (MandadoActivity) MandadoActivity.activityMandadoActivity;



                                a.registraCompartilhamentoMandado(mandadoEscolhido.getMAN_ID(), input.getText().toString());


                            }else{
                                Toast toast = Toast.makeText(MandadoActivity.this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }catch (Exception e){
                            Toast toast = Toast.makeText(MandadoActivity.this,e.getMessage().toString(), Toast.LENGTH_LONG);
                            toast.show();
                        }




                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alerta = builder.create();
                alerta.show();




                /*Intent intentArquivoAnexoCompartilhar = new Intent(MandadoActivity.this, ListaArquivosAnexos.class);
                intentArquivoAnexoCompartilhar.putExtra("compartilhar", true);
                startActivityForResult(intentArquivoAnexoCompartilhar, 1);*/
                //return true; // consume the menu event

                //Toast.makeText(MandadoActivity.this, "Lista anexo doc " , Toast.LENGTH_SHORT).show();

                drawer.closeDrawer(GravityCompat.START); //pra fechar o menu
                //this.finish();
                return true;


            case "Imprimir Rodapé":

                this.imprimirRodapeMandado();



                //Toast.makeText(MandadoActivity.this, "Lista anexo doc " , Toast.LENGTH_SHORT).show();

                drawer.closeDrawer(GravityCompat.START); //pra fechar o menu
                //this.finish();
                return true;


            case "Pacjud":
                String url = "https://depositojudicial.caixa.gov.br/sigsj_internet/depositos-judiciais/justica-estadual/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;

            case "Voltar":
                this.finish();
                return true;
        }
        return true;
    }


    private void execRegistrarImpressaoMandadoAndroid(){

        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();

        final ProgressDialog progressDialog = ProgressDialog.show(MandadoActivity.this, "", "Registrando impressão...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);


        String URL = "https://www.judix.com.br/modulos/ws/index.php";
        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados

        try {


            objEnviar.put("MAN_ID", mandadoEscolhido.getMAN_ID().toString());
            objEnviar.put("USUARIO_ID", this.idOficialLogado.toString());

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);


            jsonEnviar.put("mensagem","registrarImpressaoMandadoAndroid");
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
                                //Toast.makeText(AgendamentoMandado.this, msgm, Toast.LENGTH_SHORT).show();
                                //Intent intent = new Intent(AgendamentoMandado.this, AgendamentoMandadoLista.class);
                                //startActivity(intent);
                                //finish();
                            }else{
                                Toast.makeText(MandadoActivity.this, msgm, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(MandadoActivity.this, "Erro converssao dados tela mandado: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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






    /*public void carregarPDF(){
        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(MandadoActivity.this);
        mProgressDialog.setMessage("Fazendo download do mandado");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        // execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(MandadoActivity.this);
        downloadTask.execute(mandadoEscolhido.getMAN_ARQ_Arquivo());

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();


                //File diretorio = this.getDirFromSDCard();

                output = new FileOutputStream(dirCache.toString()+File.separator+mandadoEscolhido.getMAN_ID()+".pdf");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {

                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Erro ao fazer download: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }


    }*/


    @Override
    public void onStop() {
        super.onStop();
        //if (VERBOSE) Log.v(TAG, "-- ON STOP --");
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







    private void executaImprimir(){
        //1- cria as imagnes do pdf
        //seleciona a impressora
        //imprimi



        //this.openListaBluetooth();
        this.printDocumentPDF();

    }

    private void openListaBluetooth(){
        Intent intentDoc = new Intent(MandadoActivity.this, ListaBluetoothActivity.class);
        startActivity(intentDoc);
    }




    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void printDocumentPDF(){
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) +
                " imprmindo mandado "+this.mandadoEscolhido.getMAN_Numero_Processo();

        printManager.print(jobName, new MyPrintDocumentAdapter(this),
                null);
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    public class MyPrintDocumentAdapter extends PrintDocumentAdapter{
        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = 0;

        public MyPrintDocumentAdapter(Context context){
            this.context = context;
        }


        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle metadata) {

            myPdfDocument = new PrintedPdfDocument(context, newAttributes);



            ////totalpages = myPdfDocument.getPages().size();

            pageHeight =
                    newAttributes.getMediaSize().getHeightMils()/1000 * 72;
            pageWidth =
                    newAttributes.getMediaSize().getWidthMils()/1000 * 72;

            if (cancellationSignal.isCanceled() ) {
                callback.onLayoutCancelled();
                return;
            }

            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);


            /*if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }*/

        }


        @Override
        public void onWrite(PageRange[] pages,
                            ParcelFileDescriptor destination,
                            CancellationSignal cancellationSignal,
                            WriteResultCallback callback) {

            FileInputStream input = null;
            FileOutputStream output = null;

            try {

                File pdfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+mandadoEscolhido.getMAN_ID()+".pdf");

                input = new FileInputStream(pdfile );
                output = new FileOutputStream(destination.getFileDescriptor());

                byte[] buf = new byte[1024];
                int bytesRead;

                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

            } catch (FileNotFoundException ee){
                //Catch exception
            } catch (Exception e) {
                //Catch exception
            } finally {
                try {
                    input.close();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }




            /*
            for (int i = 0; i < totalpages; i++) {
                if (pageInRange(pages, i))
                {
                    PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                            pageHeight, i).create();

                    PdfDocument.Page page =
                            myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }
                    drawPage(page, i);
                    myPdfDocument.finishPage(page);
                }
            }

            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pages);*/

        }



        private boolean pageInRange(PageRange[] pageRanges, int page)
        {
            for (int i = 0; i<pageRanges.length; i++)
            {
                if ((page >= pageRanges[i].getStart()) &&
                        (page <= pageRanges[i].getEnd()))
                    return true;
            }
            return false;
        }


        private void drawPage(PdfDocument.Page page,
                              int pagenumber) {
            Canvas canvas = page.getCanvas();

            pagenumber++; // Make sure page numbers start at 1

            int titleBaseLine = 72;
            int leftMargin = 54;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            canvas.drawText(
                    "Test Print Document Page " + pagenumber,
                    leftMargin,
                    titleBaseLine,
                    paint);

            paint.setTextSize(14);
            canvas.drawText("This is some test content to verify that custom document printing works", leftMargin, titleBaseLine + 35, paint);

            if (pagenumber % 2 == 0)
                paint.setColor(Color.RED);
            else
                paint.setColor(Color.GREEN);

            PdfDocument.PageInfo pageInfo = page.getInfo();


            canvas.drawCircle(pageInfo.getPageWidth()/2,
                    pageInfo.getPageHeight()/2,
                    150,
                    paint);
        }


        /*@Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {
        }*/

    }























































































    private void imprimirRodapeMandado(){
        //this.mandadoEscolhido de onde vem as info pra imprimir


        String arqu = Environment.getExternalStorageDirectory().toString()+ File.separator+"Judix"+File.separator+this.mandadoEscolhido.getMAN_ID()+File.separator+"rodape_"+this.mandadoEscolhido.getMAN_ID()+".pdf";
        File arquivoAbrir = new File(arqu);

        SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gsonDiretorio = new Gson();
        String jsonDiretorio = sharedPreferences.getString("objectDiretorio", "");
        this.dirCache = gsonDiretorio.fromJson(jsonDiretorio, File.class);

        if (!arquivoAbrir.exists()) {
            this.gerarRodapeMandado(this.mandadoEscolhido);
        }else{
            //abre
            printDocumentRodape();
            //imprimirNotificacao(ListaArquivosAnexos.this, arquivoAbrir);
        }



    }


    private void gerarRodapeMandado( Mandado objMandado ){
        final Mandado notfy = objMandado;

        final ProgressDialog progressDialog = ProgressDialog.show(MandadoActivity.this, "", "Gerando o rodapé...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        try{

            JSONObject objEnviar = new JSONObject();
            objEnviar.put("MAN_ID", notfy.getMAN_ID());

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            JSONObject jsonEnviar = new JSONObject();
            jsonEnviar.put("mensagem","gerarRodapeMandado");
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

                                //Toast.makeText(LoginActivity.this, msgm, Toast.LENGTH_SHORT).show();

                                //Toast.makeText(ListaNotificacaoMandado.this, msgm, Toast.LENGTH_SHORT).show();
                                if(sucesso.equals("true")){
                                    //baixa o arquivo...



                                    //download

                                    // instantiate it within the onCreate method
                                    mProgressDialog = new ProgressDialog(MandadoActivity.this);
                                    mProgressDialog.setMessage("Fazendo download do rodapé");
                                    mProgressDialog.setIndeterminate(true);
                                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    mProgressDialog.setCancelable(true);

                                    String arqu = Environment.getExternalStorageDirectory().toString()+ File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+"rodape_"+notfy.getMAN_ID()+".pdf";

                                    // execute this when the downloader must be fired
                                    final MandadoActivity.DownloadTaskRodape downloadTask = new MandadoActivity.DownloadTaskRodape (MandadoActivity.this, arqu);

                                    downloadTask.execute(notfy.getMAN_ID());

                                    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            downloadTask.cancel(true);
                                        }
                                    });


                                }
                            }catch (Exception e){
                                progressDialog.dismiss();
                                Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                                Toast.makeText(MandadoActivity.this, "Erro converssao dados tela mandado gerar rodape: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(MandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            Log.d("JUDIX", "Error notificacaoAndroid: " + error.toString());
                            //error.printStackTrace();
                        }
                    }) {
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    20 * 1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);
            //queue.start();

        }catch (Exception e ){
            progressDialog.dismiss();
            Toast.makeText(MandadoActivity.this, "Erro ao gerar o rodapé: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }



    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class  DownloadTaskRodape extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private String CaminhoArquivo = null;

        public DownloadTaskRodape(Context context, String caminhoArquivo) {
            this.context = context;
            this.CaminhoArquivo = caminhoArquivo;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {

                String strUrl = "https://judix.com.br/modulos/mandado/gerenciar/fotoMandado/rodape_"+sUrl[0]+".pdf";

                URL url = new URL(strUrl);//https://judix.com.br/modulos/mandado/gerenciar/fotoMandado/notificacao11.pdf
                //String nomeArquivo = sUrl[1];

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();


                //File diretorio = this.getDirFromSDCard();


                //output = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator+mandadoEscolhido.getMAN_ID()+".pdf");
                //cria a pasta
                FilesHandler dirHandler = new FilesHandler();
                dirHandler.setDirMandado(mandadoEscolhido.getMAN_ID().toString());
                try {
                    dirHandler.criarDiretorios();

                    //String arqu = Environment.getExternalStorageDirectory().toString()+ File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+"notificacao"+sUrl[0];

                    String caminho = dirCache.toString()+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+"rodape_"+sUrl[0]+".pdf";

                    output = new FileOutputStream(caminho);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    return ignored.toString();
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Erro ao fazer download do rodapé: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"Download do rodapé concluído com sucesso!", Toast.LENGTH_SHORT).show();


            printDocumentRodape();


        }


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void printDocumentRodape(){
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) +
                " imprmindo RODAPE "+this.mandadoEscolhido.getMAN_ID();


        String caminho = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+"rodape_"+mandadoEscolhido.getMAN_ID()+".pdf";

        printManager.print(jobName, new PrintRodapeMandadoAdpter(this, caminho),
                null);
    }


    public void registraCompartilhamentoMandado(String idMandado, String telefone){

        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();

        final ProgressDialog progressDialog = ProgressDialog.show(MandadoActivity.this, "", "Registrando compartilhamento...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";
        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados

        try {

            objEnviar.put("MAN_ID", idMandado.toString());
            objEnviar.put("HMC_TipoStatus", "COMPARTILHADO");
            objEnviar.put("MAN_FoneCompartilhamentoZap", telefone.toString());

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);


            jsonEnviar.put("mensagem","registrarCompartilhamentoMandado");
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
                                //Toast.makeText(AgendamentoMandado.this, msgm, Toast.LENGTH_SHORT).show();
                                //Intent intent = new Intent(AgendamentoMandado.this, AgendamentoMandadoLista.class);
                                //startActivity(intent);
                                //finish();
                            }else{
                                Toast.makeText(MandadoActivity.this, msgm, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(MandadoActivity.this, "Erro converssao dados tela mandado: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
}
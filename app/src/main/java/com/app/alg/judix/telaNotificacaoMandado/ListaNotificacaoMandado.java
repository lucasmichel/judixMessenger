package com.app.alg.judix.telaNotificacaoMandado;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Build;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import com.app.alg.judix.model.Notificacao;
import com.app.alg.judix.util.FilesHandler;
import com.app.alg.judix.util.Funcoes;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListaNotificacaoMandado extends AppCompatActivity {
    ProgressDialog progressDialog;
    ProgressDialog mProgressDialog;

    Button buttonAdicionarNotificaco;
    Mandado mandadoEscolhido;
    ListView mainListViewNotificacao = null;

    Notificacao notificacaoEscolhida = null;

    File dirCache= null;

    private static final ArrayList<Notificacao> listaDeNotificacoes = new ArrayList<Notificacao>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notificacao_mandado_novo);



        mainListViewNotificacao = (ListView) findViewById(R.id.listViewNotificacoes);

        buttonAdicionarNotificaco = (Button) findViewById(R.id.buttonAddNotificacao);
        buttonAdicionarNotificaco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                Notificacao notificacaoEscolhidaAqui = new Notificacao();
                String gsonNot = gson.toJson(notificacaoEscolhidaAqui);
                editor.putString("objectNotificacao", String.valueOf(gsonNot));
                editor.apply();

                Intent intent = new Intent(ListaNotificacaoMandado.this, NotificacaoMandado.class);
                //intent.putExtra("telaEnviada", "DadosAssinaturaMandado");
                startActivityForResult(intent, 1);

            }
        });




        //identifica o mandado
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref.getString("objectMandado", "");
        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);
        setTitle("Notificações do mandado: "+this.mandadoEscolhido.getMAN_ID().toString());


        try{
            Funcoes func = new Funcoes();

            if(func.checkConexao(ListaNotificacaoMandado.this)){
                this.listarNotificacoes();
            }else{
                Toast toast = Toast.makeText(this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                toast.show();
            }
        }catch (Exception e){
            Toast toast = Toast.makeText(this,e.getMessage().toString(), Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private void listarNotificacoes() {
        progressDialog = ProgressDialog.show(ListaNotificacaoMandado.this, "", "Carregando notificações...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        JSONObject objEnviar = new JSONObject();
        JSONObject jsonEnviar = new JSONObject();

        try {


            objEnviar.put("MAN_ID", this.mandadoEscolhido.getMAN_ID().toString());

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            jsonEnviar.put("mensagem","listarNotificacaoMandadoAndroid");
            jsonEnviar.put("sucesso", "true");
            jsonEnviar.put("dados", arrayDados);

        } catch (JSONException e) {
            Toast.makeText(ListaNotificacaoMandado.this, "Erro ao gerar a consulta das notificações do mandado."+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }



        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                jsonEnviar,
                new Response.Listener<JSONObject>() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String msgm = response.getString("mensagem");
                            String sucesso = response.getString("sucesso");
                            progressDialog.dismiss();

                            //Toast.makeText(LoginActivity.this, msgm, Toast.LENGTH_SHORT).show();

                            if(sucesso.equals("true")){

                                if(response.getJSONArray("dados").length() > 0){
                                    JSONArray dados = response.getJSONArray("dados");

                                    //dados.length() faz um for e preenceh os itens da exibição
                                    int tot = dados.length();
                                    listaDeNotificacoes.clear();
                                    for(int i=0;  i<tot; i++){
                                        JSONObject itemDados = (JSONObject) dados.get(i);
                                        Notificacao notificacao = new Notificacao();

                                        notificacao.setNOT_ID(itemDados.getString("NOT_ID"));
                                        notificacao.setNOT_Telefone(itemDados.getString("NOT_Telefone"));
                                        notificacao.setNOT_Endereco(itemDados.getString("NOT_Endereco"));
                                        notificacao.setNOT_DataEncontro(itemDados.getString("NOT_DataEncontro"));
                                        notificacao.setNOT_HoraEncontro(itemDados.getString("NOT_HoraEncontro"));
                                        notificacao.setNOT_DataHoraCadastro(itemDados.getString("NOT_DataHoraCadastro"));
                                        notificacao.setUSU_Cadastro_ID(itemDados.getString("USU_Cadastro_ID"));
                                        notificacao.setMAN_ID(itemDados.getString("MAN_ID"));
                                        notificacao.setUSU_Alteracao_ID(itemDados.getString("USU_Alteracao_ID"));
                                        notificacao.setNOT_DataHoraAlteracao(itemDados.getString("NOT_DataHoraAlteracao"));

                                        listaDeNotificacoes.add(notificacao);

                                    }

                                }else {
                                    Toast.makeText(ListaNotificacaoMandado.this, "Nenhum dado retornado, contate o adminstrador do sistema.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ListaNotificacaoMandado.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                            }



                            final ItemNotificacaoAdapter adapterNot = new ItemNotificacaoAdapter(ListaNotificacaoMandado.this, listaDeNotificacoes);
                            mainListViewNotificacao.setAdapter(adapterNot);
                            adapterNot.notifyDataSetChanged();

                            mainListViewNotificacao.setClickable(true);
                            mainListViewNotificacao.setLongClickable(true);

                            mainListViewNotificacao.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

                                @Override
                                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                                    // Get the info on which item was selected
                                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

                                    int posicao = info.position;
                                    Notificacao not = new Notificacao();
                                    not = listaDeNotificacoes.get(info.position);
                                    notificacaoEscolhida = not;


                                    MenuInflater inflater = getMenuInflater();
                                    inflater.inflate(R.menu.menu_clica_item_notificacao , menu);


                                    LayoutInflater layoutInflater = getLayoutInflater();
                                    View view = (View) layoutInflater.inflate(
                                            R.layout.menu_titulo_notificaco_clicado, null);
                                    menu.setHeaderView(view);

                                    TextView tituloMenu2 = (TextView) view.findViewById(R.id.tituloMenuNotificacoClicado);
                                    tituloMenu2.setText("Notificação :"+notificacaoEscolhida.getNOT_ID());


                                    /*if(assinaturaMandadoEscolhido.getId().equals("-1") || assinaturaMandadoEscolhido.getId().equals("-2")){
                                        Toast.makeText(ListaAssinaturasMandadoActivity.this, "Não pode excluir.", Toast.LENGTH_SHORT).show();
                                    }else{



                                        MenuInflater inflater = getMenuInflater();
                                        inflater.inflate(R.menu.menu_clica_item_assintura_mandado , menu);


                                        LayoutInflater layoutInflater = getLayoutInflater();
                                        View view = (View) layoutInflater.inflate(
                                                R.layout.menu_titulo_mandado_clicado, null);
                                        menu.setHeaderView(view);

                                        TextView tituloMenu2 = (TextView) view.findViewById(R.id.tituloMenuMandadoClicado);
                                        tituloMenu2.setText("Assinatura de :"+assinaturaMandadoEscolhido.getNome());
                                    }*/



                                }

                            });

                            mainListViewNotificacao.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    // TODO Auto-generated method stub

                                    //Toast.makeText(ListaNotificacaoMandado.this, "item notificacao: " + arg2, Toast.LENGTH_SHORT).show();

                                    return false;
                                }
                            });



                        }catch (Exception e){
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(ListaNotificacaoMandado.this, "Erro converssao dados tela login: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListaNotificacaoMandado.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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



        if(requestCode == 1 && resultCode == Activity.RESULT_OK){


            listarNotificacoes();
            //Do whatever you want with yourData
        }


        /*switch (requestCode) {
            case 1:
                AssinaturaMandado myObject = (AssinaturaMandado) data.getExtras().getSerializable("OBJETO_ASSINATURA");
                AssinaturaMandado myObject2 = myObject;

                Toast.makeText(ListaAssinaturasMandadoActivity.this, "criar o objeto assinatura acicionar a lista e fechar a tela de dados ", Toast.LENGTH_SHORT).show();

                break;
        }*/
        //super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        //carregar a sessao com o mandado escolhido
        //pra passar o objeto para o SharedPreferences transforma e m json
        /*SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        Gson gson = new Gson();
        String gsonMandado = gson.toJson(mandadoEscolhido);
        editor.putString("objectMandado", String.valueOf(gsonMandado));
        editor.apply();*/



        // switch based on the MenuItem id
        switch (item.toString())
        {


            case "Imprimir":
                try {
                    this.imprimirNotificacao(this.notificacaoEscolhida);
                } catch (IOException e) {
                    //e.printStackTrace();
                    Toast.makeText(ListaNotificacaoMandado.this, "Imprimir Notificação ERRO: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
                return true; // consume the menu event


            case "Alterar":
                this.editarNotificacao();
                //Toast.makeText(ListaNotificacaoMandado.this, "Alterar Notificação: " + notificacaoEscolhida.getNOT_ID().toString(), Toast.LENGTH_SHORT).show();
                return true; // consume the menu event

            case "Excluir":
                /*Intent intent = new Intent(ListaMandadoActivity.this, AgendamentoMandadoLista.class);
                startActivity(intent);*/
                this.excluirNotificacao(notificacaoEscolhida);
                //Toast.makeText(ListaNotificacaoMandado.this, "Excluir Notificação: " + notificacaoEscolhida.getNOT_ID().toString(), Toast.LENGTH_SHORT).show();
                return true; // consume the menu event

        }
        return true;
    }

    private void editarNotificacao(){


        //carregar a sessao com o mandado escolhido
        //pra passar o objeto para o SharedPreferences transforma e m json
        SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        Gson gson = new Gson();
        String gsonNot = gson.toJson(notificacaoEscolhida);
        editor.putString("objectNotificacao", String.valueOf(gsonNot));
        editor.apply();

        Intent intent = new Intent(ListaNotificacaoMandado.this, NotificacaoMandado.class);
        //intent.putExtra("telaEnviada", "DadosAssinaturaMandado");
        startActivityForResult(intent, 1);
    }

    private void excluirNotificacao(Notificacao objNot){
        final Notificacao notfy = objNot;

        progressDialog = ProgressDialog.show(ListaNotificacaoMandado.this, "", "Excluindo a notificação...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        try{

            JSONObject objEnviar = new JSONObject();
            objEnviar.put("NOT_ID", notfy.getNOT_ID());

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            JSONObject jsonEnviar = new JSONObject();
            jsonEnviar.put("mensagem","excluirNotificacaoMandadoAndroid");
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

                                    /*SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("USU_Telefone", USU_Telefone);
                                    editor.putString("USU_Email", USU_Email);
                                    editor.putString("USU_Transmissao", USU_Transmissao);
                                    editor.putString("USU_EnderecoContato", USU_EnderecoContato);
                                    editor.apply();*/

                                    //Toast.makeText(AlterarDadosOficialActivity.this, "Dados alterados com sucesso.", Toast.LENGTH_SHORT).show();

                                    ListaNotificacaoMandado.this.listarNotificacoes();
                                    /*Intent intentDeRetorno = new Intent();
                                    intentDeRetorno.putExtra("retorno", 1);
                                    int resultCode = RESULT_OK;
                                    setResult (resultCode, intentDeRetorno);*/

                                    //finish();*/
                                }
                            }catch (Exception e){
                                progressDialog.dismiss();
                                //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                                Toast.makeText(ListaNotificacaoMandado.this, "Erro converssao dados tela notificacao: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(ListaNotificacaoMandado.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            Log.d("JUDIX", "Error notificacaoAndroid: " + error.toString());
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

        }catch (Exception e ){
            progressDialog.dismiss();
            Toast.makeText(ListaNotificacaoMandado.this, "Erro ao excluir a notificacao: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }


















    public void imprimirNotificacao(Notificacao obj) throws IOException {


        //verifica se existe se existir abre se nao existe faz o download e abre


        //String arqu = Environment.getExternalStorageDirectory().toString()+File.separator+"Judix"+File.separator+nomeArquivo;
        String arqu = Environment.getExternalStorageDirectory().toString()+ File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+"notificacao"+obj.getNOT_ID()+".pdf";

        File arquivoAbrir = new File(arqu);


        SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gsonDiretorio = new Gson();
        String jsonDiretorio = sharedPreferences.getString("objectDiretorio", "");
        this.dirCache = gsonDiretorio.fromJson(jsonDiretorio, File.class);

        if (!arquivoAbrir.exists()) {
            this.gerarNotificacaoMandado(this.notificacaoEscolhida);
        }else{
            //abre
            printDocumentPDF();
            //imprimirNotificacao(ListaArquivosAnexos.this, arquivoAbrir);
        }
    }





    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class  DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private String CaminhoArquivo = null;

        public DownloadTask(Context context, String caminhoArquivo) {
            this.context = context;
            this.CaminhoArquivo = caminhoArquivo;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {

                String strUrl = "https://judix.com.br/modulos/mandado/gerenciar/fotoMandado/notificacao"+sUrl[0]+".pdf";

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

                    String caminho = dirCache.toString()+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+"notificacao"+sUrl[0]+".pdf";

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
                Toast.makeText(context,"Erro ao fazer download: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"Download concluído com sucesso!", Toast.LENGTH_SHORT).show();


                printDocumentPDF();


        }


    }












































    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void printDocumentPDF(){
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) +
                " imprmindo notificação "+this.notificacaoEscolhida.getNOT_ID();

        printManager.print(jobName, new MyPrintDocumentAdapter(this),
                null);
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
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

                File pdfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+"notificacao"+notificacaoEscolhida.getNOT_ID()+".pdf");

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





    private void gerarNotificacaoMandado(Notificacao objNot){
        final Notificacao notfy = objNot;

        progressDialog = ProgressDialog.show(ListaNotificacaoMandado.this, "", "Gerando a notificação...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        try{

            JSONObject objEnviar = new JSONObject();
            objEnviar.put("NOT_ID", notfy.getNOT_ID());

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            JSONObject jsonEnviar = new JSONObject();
            jsonEnviar.put("mensagem","gerarNotificacaoMandado");
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
                                    mProgressDialog = new ProgressDialog(ListaNotificacaoMandado.this);
                                    mProgressDialog.setMessage("Fazendo download da Notificação");
                                    mProgressDialog.setIndeterminate(true);
                                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    mProgressDialog.setCancelable(true);

                                    String arqu = Environment.getExternalStorageDirectory().toString()+ File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+"notificacao"+notfy.getNOT_ID()+".pdf";

                                    // execute this when the downloader must be fired
                                    final ListaNotificacaoMandado.DownloadTask downloadTask = new ListaNotificacaoMandado.DownloadTask(ListaNotificacaoMandado.this, arqu);

                                    downloadTask.execute(notfy.getNOT_ID());

                                    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            downloadTask.cancel(true);
                                        }
                                    });


                                }
                            }catch (Exception e){
                                progressDialog.dismiss();
                                //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                                Toast.makeText(ListaNotificacaoMandado.this, "Erro converssao dados tela notificacao: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(ListaNotificacaoMandado.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            Log.d("JUDIX", "Error notificacaoAndroid: " + error.toString());
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

        }catch (Exception e ){
            progressDialog.dismiss();
            Toast.makeText(ListaNotificacaoMandado.this, "Erro ao gerar a notificacao: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }


}

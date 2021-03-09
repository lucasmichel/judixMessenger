package com.app.alg.judix.telaArquivosAnexos;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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
import com.app.alg.judix.model.ArquivoAnexoMandado;
import com.app.alg.judix.model.AssinaturaMandado;
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.telaAssinaturasMandado.ItemAssinaturaMandadoAdapter;
import com.app.alg.judix.telaAssinaturasMandado.ListaAssinaturasMandadoActivity;
import com.app.alg.judix.telaMandado.ListaMandadoActivity;
import com.app.alg.judix.telaMandado.MandadoActivity;
import com.app.alg.judix.util.FilesHandler;
import com.app.alg.judix.util.Funcoes;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListaArquivosAnexos extends AppCompatActivity {

    ProgressDialog progressDialog;
    ProgressDialog mProgressDialog;

    Mandado mandadoEscolhido;
    ListView mainListViewArquivoAnexos = null;
    ItemArquivoAnexoMandadoAdapter adapterArquivo = null;
    File dirCache= null;

    Integer pageNumber = 1;

    private static final ArrayList<ArquivoAnexoMandado> listaDeArquivos = new ArrayList<ArquivoAnexoMandado>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_arquivos_anexos);


        // Retrieve the parameter.
        Bundle args = this.getIntent().getExtras();
        boolean compartilhar = (boolean) args.getBoolean("compartilhar");


        //identifica o mandado
        SharedPreferences pref1 = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref1.getString("objectMandado", "");

        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);

        String mandado = "Anexos do Mandado: " + mandadoEscolhido.getMAN_Numero_Processo();
        setTitle(mandado.toString());


        if(compartilhar){
            //compartilhar arquivos
            this.executaCompartilharArquivos();
        }else{
            //listar arquivos
            this.executaListagemArquivos();
        }




    }


    private void executaCompartilharArquivos(){
        Toast.makeText(ListaArquivosAnexos.this, "Compartilhar... " , Toast.LENGTH_SHORT).show();





        //-1 - baixa a lista de arquivos existentes
        this.getListaArquivosExistentes();



        //2 - se tiver cria a lista de uri e manda abrir a intent
        //3 - se nao tiver recupera a lista de arquivos
        //4 - baixa os arquivos
        //5 - cria a lista de uri e manda abrir a intent
    }


    private void getListaArquivosExistentes(){

        try{
            Funcoes func = new Funcoes();

            if(func.checkConexao(ListaArquivosAnexos.this)){


                PackageManager pm=getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = "YOUR TEXT HERE";

                    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(this, "WhatsApp não está Instalado", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast toast = Toast.makeText(this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                toast.show();
            }
        }catch (Exception e){
            Toast toast = Toast.makeText(this,e.getMessage().toString(), Toast.LENGTH_LONG);
            toast.show();
        }


    }


    private void executaListagemArquivos(){
        listaDeArquivos.clear();

        try{
            Funcoes func = new Funcoes();

            if(func.checkConexao(ListaArquivosAnexos.this)){
                this.listarArquivosAnexos();
            }else{
                Toast toast = Toast.makeText(this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                toast.show();
            }
        }catch (Exception e){
            Toast toast = Toast.makeText(this,e.getMessage().toString(), Toast.LENGTH_LONG);
            toast.show();
        }
    }



    private void listarArquivosAnexos() {



        progressDialog = ProgressDialog.show(ListaArquivosAnexos.this, "", "Carregando arquivos...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        JSONObject objEnviar = new JSONObject();
        JSONObject jsonEnviar = new JSONObject();

        try {
            objEnviar.put("MAN_ID", this.mandadoEscolhido.getMAN_ID());

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            jsonEnviar.put("mensagem","listarAnexosMandadoAndroid");
            jsonEnviar.put("sucesso", "true");
            jsonEnviar.put("dados", arrayDados);

        } catch (JSONException e) {
            Toast.makeText( ListaArquivosAnexos.this, "Erro ao gerar a consulta dos arquivos anexos do mandado."+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

                                    listaDeArquivos.clear();

                                    //dados.length() faz um for e preenceh os itens da exibição
                                    int tot = dados.length();
                                    //listaDeAssinaturas.clear();
                                    for(int i=0;  i<tot; i++){

                                        JSONObject itemDados = (JSONObject) dados.get(i);


                                        ArquivoAnexoMandado arquivo = new ArquivoAnexoMandado();

                                        arquivo.setId(itemDados.getString("ARQ_ID"));
                                        arquivo.setEndereco(itemDados.getString("MAN_ARQ_Arquivo"));
                                        arquivo.setNome(itemDados.getString("MAN_ARQ_Nome"));

                                        listaDeArquivos.add(arquivo);



                                    }

                                }else {
                                    Toast.makeText(ListaArquivosAnexos.this, "Nenhum dado retornado, contate o adminstrador do sistema.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ListaArquivosAnexos.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                            }
                            mainListViewArquivoAnexos = (ListView) findViewById(R.id.listViewArquivosAnexosMandado);


                            adapterArquivo = new ItemArquivoAnexoMandadoAdapter(ListaArquivosAnexos.this, listaDeArquivos);
                            mainListViewArquivoAnexos.setAdapter(adapterArquivo);
                            adapterArquivo.notifyDataSetChanged();

                            mainListViewArquivoAnexos.setClickable(true);
                            mainListViewArquivoAnexos.setLongClickable(true);

                            mainListViewArquivoAnexos.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                                @Override
                                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                                    int posicao = info.position;
                                    ArquivoAnexoMandado arquivo = listaDeArquivos.get(info.position);
                                    try {
                                        abrirArquivo(arquivo);
                                    } catch (IOException e) {
                                        Toast.makeText(ListaArquivosAnexos.this, "Erro ao abrir arquivo: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                            });

                            mainListViewArquivoAnexos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    // TODO Auto-generated method stub
                                    return false;
                                }
                            });



                        }catch (Exception e){
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(ListaArquivosAnexos.this, "Erro converssao dados tela lsitaarquivoanexos: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListaArquivosAnexos.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void abrirArquivo(ArquivoAnexoMandado obj) throws IOException {









        //verifica se existe se existir abre se nao existe faz o download e abre

        String[] parts = obj.getEndereco().split("/");

        String nomeArquivo = parts[parts.length-1];

        //String arqu = Environment.getExternalStorageDirectory().toString()+File.separator+"Judix"+File.separator+nomeArquivo;
        String arqu = Environment.getExternalStorageDirectory().toString()+File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+nomeArquivo;






        File arquivoAbrir = new File(arqu);


        SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gsonDiretorio = new Gson();
        String jsonDiretorio = sharedPreferences.getString("objectDiretorio", "");
        this.dirCache = gsonDiretorio.fromJson(jsonDiretorio, File.class);

        if (!arquivoAbrir.exists()) {
            //download

            // instantiate it within the onCreate method
            mProgressDialog = new ProgressDialog(ListaArquivosAnexos.this);
            mProgressDialog.setMessage("Fazendo download do arquivo");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);

            // execute this when the downloader must be fired
            final DownloadTask downloadTask = new DownloadTask(ListaArquivosAnexos.this, arqu);

            downloadTask.execute(obj.getEndereco(), nomeArquivo);

            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                }
            });



        }else{
            abrirPdf(obj);
            //abre
            //openFile(ListaArquivosAnexos.this, arquivoAbrir);
        }
    }

    public void abrirPdf(ArquivoAnexoMandado obj){

        ArquivoAnexoMandado a = obj;
        /*
        *
                Intent intent = new Intent(MandadoActivity.this, Tela.class);
                intent.putExtra("telaEnviada", "Mandado");
                startActivityForResult(intent, 1);
        *
        * */
        String[] parts = obj.getEndereco().split("/");
        String nomeArquivo = parts[parts.length-1];


        String caminho = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+nomeArquivo;
        File pdfile = new File(caminho);

        if(pdfile.exists()){

            Intent viewPDF = new Intent(ListaArquivosAnexos.this, viewPdfArquivoAnexo.class);
            viewPDF.putExtra("ARQUIVO_PDF", nomeArquivo);
            viewPDF.putExtra("ID_MANDADO", mandadoEscolhido.getMAN_ID());
            startActivity(viewPDF);

            //PDFView pdfView = (PDFView) findViewById(R.id.pdfView);

            /*pdfView.fromFile(pdfile)
                    .defaultPage(pageNumber)
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageNumber = page;
                        }
                    })
                    .load();*/
        }else{
            //String[] parts = obj.getEndereco().split("/");

            //String nomeArquivo = parts[parts.length-1];

            Toast.makeText(ListaArquivosAnexos.this,"Arquivo "+nomeArquivo+" nao encontrado!", Toast.LENGTH_LONG).show();
            this.finish();
        }
    }


    public static void openFile(Context context, File url) throws IOException {

        try{
            // Create URI
            File file=url;
            Uri uri = Uri.fromFile(file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if(url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if(url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if(url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if(url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }


            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Nenhum aplicativo disponível para visualização do pdf", Toast.LENGTH_LONG).show();
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
                URL url = new URL(sUrl[0]);
                String nomeArquivo = sUrl[1];

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

                    String caminho = dirCache.toString()+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+nomeArquivo;

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


                File arquivoAbrir = new File(this.CaminhoArquivo);
                try {
                    openFile(ListaArquivosAnexos.this, arquivoAbrir);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*Intent intent = new Intent(ListaArquivosAnexos.this, MandadoActivity.class);
                startActivityForResult(intent,1);*/


                /*Intent intent = new Intent(ListaMandadoActivity.this, MandadoActivity.class);
                startActivity(intent);*/


                /*
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Judix/"+ mandadoEscolhido.getMAN_ID()+".pdf");
                Intent intent = new Intent(ListaMandadoActivity.this, MandadoActivity.class);
                intent.setDataAndType(Uri.fromFile(file),"application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);*/

        }



        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 10) {
                if (resultCode == RESULT_OK) {
                    // Get result from the result intent.
                    String result = data.getStringExtra("myResult");

                    // Do something with result...
                }
            }
        }


    }


}

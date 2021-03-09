package com.app.alg.judix.telaMandado;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.app.alg.judix.model.Endereco;
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.model.Notificacao;
import com.app.alg.judix.telaNotificacaoMandado.ListaNotificacaoMandado;
import com.app.alg.judix.util.FilesHandler;
import com.app.alg.judix.util.Funcoes;
import com.app.alg.judix.util.GPSHelper;
import com.google.android.gms.vision.barcode.Barcode;
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListaMandadoActivity extends AppCompatActivity {


    Endereco mandadoEndereco;
    EditText txtProcessoPesquisa;
    Spinner spinnerPrioridadePesquisa;
    ImageButton btnPesquisaMandado;
    ProgressDialog progressDialog;

    String idOficialLogado;
    Mandado mandadoEscolhido;

    ProgressDialog mProgressDialog;
    File dirCache=null;

    ListView mainListView = null;

    ///para o spin
    private List<String> nomes = new ArrayList<String>();
    String nome = "";


    String caminhoDGO;

    private final ArrayList<Mandado> listaDeMandados = new ArrayList<Mandado>();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mandado);


        //identifica o mandado
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        this.idOficialLogado = pref.getString("USU_ID","");


        txtProcessoPesquisa = (EditText) findViewById(R.id.txtProcessoPesquisa);
        btnPesquisaMandado = (ImageButton) findViewById(R.id.btnPesquisaMandado);

        btnPesquisaMandado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaMandados();
            }
        });

        btnPesquisaMandado.requestFocus();
        btnPesquisaMandado.setFocusableInTouchMode(true);

        //Adicionando Nomes no ArrayList
        nomes.add("BAIRRO");
        nomes.add("PRIORIDADE");
        nomes.add("PLANTÃO");
        nomes.add("AGENDAMENTO");


                //Identifica o Spinner no layout
        spinnerPrioridadePesquisa = (Spinner) findViewById(R.id.spinnerPrioridadePesquisa);
        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nomes);



        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridadePesquisa.setAdapter(spinnerArrayAdapter);

        //Método do Spinner para capturar o item selecionado
        spinnerPrioridadePesquisa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                //pega nome pela posição
                //nome = parent.getItemAtPosition(posicao).toString();


                /*switch (parent.getItemAtPosition(posicao).toString()) {
                    case "BAIRRO":  nome = "BAIRRO";
                        listaMandados();
                        break;
                    case "PRIORIDADE":  nome = "PRIORIDADE";
                        listaMandados();
                        break;
                    case "PLANTÃO":  nome = "PLANTÃO";
                        listaMandados();
                        break;
                    case "AGENDAMENTO":  nome = "AGENDAMENTO";
                        listaMandados();
                        break;
                    default: nome = "";
                        listaMandados();
                        break;
                }*/

                //imprime um Toast na tela com o nome que foi selecionado
                //Toast.makeText(ListaMandadoActivity.this, "Nome Selecionado: " + nome, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });







        try{
            Funcoes func = new Funcoes();

            if(func.checkConexao(ListaMandadoActivity.this)){
                listaMandados();
            }else{
                Toast toast = Toast.makeText(this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                toast.show();
            }
        }catch (Exception e){
            Toast toast = Toast.makeText(this,e.getMessage().toString(), Toast.LENGTH_LONG);
            toast.show();
        }

        btnPesquisaMandado.requestFocus();

    }





    private void listaMandados() {


        progressDialog = ProgressDialog.show(ListaMandadoActivity.this, "", "Carregando...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados


        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        String listarPor = pref.getString("exibirMandado", null);
        //todos
        //pendente
        //concluido


        Funcoes func = new Funcoes();

        JSONObject objEnviar = new JSONObject();

        JSONObject jsonEnviar = new JSONObject();

        try {



            //pega a data e hora por conta do agendamento
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = df.format(c.getTime());


            objEnviar.put("USU_CPF", func.getUsuarioCPF(ListaMandadoActivity.this));
            objEnviar.put("MAN_Numero_Processo", txtProcessoPesquisa.getText().toString());
            objEnviar.put("MAN_OrdenarPor", spinnerPrioridadePesquisa.getSelectedItem().toString());
            objEnviar.put("MAN_ListarPor", listarPor);

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            jsonEnviar.put("mensagem","consultarMandadoAndroid");
            jsonEnviar.put("sucesso", "true");
            jsonEnviar.put("dados", arrayDados);

        } catch (JSONException e) {
            Toast.makeText(ListaMandadoActivity.this, "Erro gerar consulta mandado."+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

                                if(response.getJSONArray("dados").length() > 0) {
                                    JSONArray dados = response.getJSONArray("dados");

                                    JSONArray dados2 = dados;

                                    //dados.length() faz um for e preenceh os itens da exibição
                                    int tot = dados.length();

                                    SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
                                    String listarPor = pref.getString("exibirMandado", null);
                                    //todos
                                    //pendente
                                    //concluido

                                    if (listarPor.equals("todos")) {
                                        setTitle("Entrada - Qtd: " + tot);
                                    }
                                    if (listarPor.equals("pendente")) {
                                        setTitle("Pendentes - Qtd: " + tot);
                                    }
                                    if (listarPor.equals("concluido")) {
                                        setTitle("Concluídos - Qtd: " + tot);
                                    }

                                    //limpa pra sempre trazer uma nova
                                    listaDeMandados.clear();
                                    for (int i = 0; i < tot; i++) {
                                        JSONObject itemDados = (JSONObject) dados.get(i);
                                        Mandado mandado = new Mandado();

                                        mandado.setMAN_ID(itemDados.getString("MAN_ID"));
                                        mandado.setENT_ID_Origem(itemDados.getString("ENT_ID_Origem"));
                                        mandado.setENT_ID_Destino(itemDados.getString("ENT_ID_Destino"));

                                        mandado.setTPR_ID(itemDados.getString("TPR_ID"));
                                        mandado.setTIM_ID(itemDados.getString("TIM_ID"));
                                        mandado.setTID_ID(itemDados.getString("TID_ID"));
                                        mandado.setMAN_Tipo_Destinatario(itemDados.getString("MAN_Tipo_Destinatario"));
                                        mandado.setEND_ENDERECO_ID(itemDados.getString("END_ENDERECO_ID"));
                                        mandado.setMAN_Numero_Processo(itemDados.getString("MAN_Numero_Processo"));
                                        mandado.setMAN_Processo_Nome1(itemDados.getString("MAN_Processo_Nome1"));
                                        mandado.setMAN_Processo_Nome2(itemDados.getString("MAN_Processo_Nome2"));
                                        mandado.setMAN_Imagem_Emissor(itemDados.getString("MAN_Imagem_Emissor"));
                                        mandado.setMAN_Imagem_Receptor(itemDados.getString("MAN_Imagem_Receptor"));
                                        mandado.setMAN_Prioridade(itemDados.getString("MAN_Prioridade"));
                                        mandado.setMAN_OutraParte(itemDados.getString("MAN_OutraParte"));
                                        mandado.setMAN_DataHoraCadastro(itemDados.getString("MAN_DataHoraCadastro"));
                                        mandado.setMAN_DataHoraAlteracao(itemDados.getString("MAN_DataHoraAlteracao"));

                                        mandado.setMAN_Destinatario(itemDados.getString("MAN_Destinatario"));
                                        mandado.setMAN_MedidaJudicial(itemDados.getString("MAN_MedidaJudicial"));
                                        mandado.setMAN_Bairro(itemDados.getString("MAN_Bairro"));

                                        mandado.setMAN_ArquivoTipo(itemDados.getString("MAN_ArquivoTipo"));
                                        mandado.setMAN_ARQ_Arquivo(itemDados.getString("MAN_ARQ_Arquivo"));

                                        mandado.setMAN_DestinatarioTipoDoc(itemDados.getString("MAN_DestinatarioTipoDoc"));
                                        mandado.setMAN_DestinatarioDoc(itemDados.getString("MAN_DestinatarioDoc"));

                                        mandado.setCEM_ID(itemDados.getString("CEM_ID"));
                                        mandado.setMAN_CertidaoTexto(itemDados.getString("MAN_CertidaoTexto"));
                                        mandado.setEND_PontoReferencia(itemDados.getString("END_PontoReferencia"));
                                        mandado.setMAN_FoneAutor(itemDados.getString("MAN_FoneAutor"));
                                        mandado.setHIM_DataHoraCadastro(itemDados.getString("HIM_DataHoraCadastro"));
                                        mandado.setHIM_TipoStatus(itemDados.getString("HIM_TipoStatus"));
                                        mandado.setMAN_intimadoSecretaria(itemDados.getString("MAN_intimadoSecretaria"));
                                        mandado.setMAN_FoneCompartilhamentoZap(itemDados.getString("MAN_FoneCompartilhamentoZap"));
                                        mandado.setHash(itemDados.getString("hash"));
                                        listaDeMandados.add(mandado);

                                    }


                                    mainListView = (ListView) findViewById(R.id.listViewMandados);
                                    final ItemMandadoAdapter adapterMandado = new ItemMandadoAdapter(ListaMandadoActivity.this, listaDeMandados, listarPor);
                                    mainListView.setAdapter(adapterMandado);
                                    adapterMandado.notifyDataSetChanged();


                                    //so cria o menu se não fo conlcuido
                                    if (!listarPor.equals("concluido")) {
                                        mainListView.setClickable(true);
                                        mainListView.setLongClickable(true);

                                        mainListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                                            @Override
                                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                                                // Get the info on which item was selected
                                                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;


                                                int posicao = info.position;

                                                Mandado mandado = new Mandado();
                                                mandado = listaDeMandados.get(info.position);
                                                mandadoEscolhido = mandado;

                                                MenuInflater inflater = getMenuInflater();
                                                inflater.inflate(R.menu.menu_clica_item_mandado, menu);


                                                LayoutInflater layoutInflater = getLayoutInflater();
                                                View view = (View) layoutInflater.inflate(
                                                        R.layout.menu_titulo_mandado_clicado, null);
                                                menu.setHeaderView(view);


                                                TextView tituloMenu2 = (TextView) view.findViewById(R.id.tituloMenuMandadoClicado);

                                                tituloMenu2.setText("ID:" + mandado.getMAN_ID() + " \nPro.:" + mandado.getMAN_Numero_Processo());


                                                //menu.setHeaderTitle("Mandado: " + mandado.getMAN_Numero_Processo());

                                                //menu.setHeaderIcon(R.drawable.splash_screen_judix_azul);
                                            }


                                        });


                                        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                            @Override
                                            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                                // TODO Auto-generated method stub
                                                return false;
                                            }
                                        });
                                    }

                                }else {
                                    Toast.makeText(ListaMandadoActivity.this, "Nenhum dado retornado, contate o adminstrador do sistema.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ListaMandadoActivity.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                                SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
                                String listarPor = pref.getString("exibirMandado", null);
                                //todos
                                //pendente
                                //concluido
                                if(listarPor.equals("todos")){
                                    setTitle("Todos - Qtd: 0");
                                }
                                if(listarPor.equals("pendente")){
                                    setTitle("Pendentes - Qtd: 0" );
                                }
                                if(listarPor.equals("concluido")){
                                    setTitle("Concluídos - Qtd: 0");
                                }
                                listaDeMandados.clear();
                                ItemMandadoAdapter adapterMandado = new ItemMandadoAdapter(ListaMandadoActivity.this, listaDeMandados, listarPor);
                                //doodleView = (TelaView) findViewById(R.id.TelaView);
                                mainListView = (ListView) findViewById(R.id.listViewMandados);
                                //mainListView = new ListView(this);
                                mainListView.setAdapter(adapterMandado);


                            }

                        }catch (Exception e){
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(ListaMandadoActivity.this, "Erro converssao dados tela login: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListaMandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
    public boolean onContextItemSelected(MenuItem item) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        //carregar a sessao com o mandado escolhido
        //pra passar o objeto para o SharedPreferences transforma e m json
        SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        Gson gson = new Gson();
        String gsonMandado = gson.toJson(mandadoEscolhido);
        editor.putString("objectMandado", String.valueOf(gsonMandado));
        editor.apply();



        // switch based on the MenuItem id
        switch (item.toString())
        {

            case "Abrir":
                this.abrirMandado();
                return true; // consume the menu event

            case "Agendar":
                Intent intent = new Intent(ListaMandadoActivity.this, AgendamentoMandadoLista.class);
                startActivity(intent);
                //Toast.makeText(ListaMandadoActivity.this, "Agendamento: " + mandadoEscolhido.getMAN_Numero_Processo(), Toast.LENGTH_SHORT).show();
                return true; // consume the menu event

            case "Localizar":
                //this.abrirMapa(mandadoEscolhido);
                this.abrirLocalizacao();
                //Toast.makeText(ListaMandadoActivity.this, "Localizacao: " + mandadoEscolhido.getMAN_Numero_Processo(), Toast.LENGTH_SHORT).show();
                return true; // consume the menu event

            case "Notificar":
                Intent intent2 = new Intent(ListaMandadoActivity.this, ListaNotificacaoMandado.class);
                startActivity(intent2);
                //Toast.makeText(ListaMandadoActivity.this, "Agendamento: " + mandadoEscolhido.getMAN_Numero_Processo(), Toast.LENGTH_SHORT).show();
                return true; // consume the menu event


            case "Visualizar DGO":
                this.visualizarDGOMandado();
                //Toast.makeText(ListaMandadoActivity.this, "Mapa: " + mandadoEscolhido.getMAN_Numero_Processo(), Toast.LENGTH_SHORT).show();
                return true; // consume the menu event*/

            /*case "Mapa":
                this.abrirRota(mandadoEscolhido);
                //Toast.makeText(ListaMandadoActivity.this, "Mapa: " + mandadoEscolhido.getMAN_Numero_Processo(), Toast.LENGTH_SHORT).show();
                return true; // consume the menu event*/

            /*case "Certidão":
                Intent intentCertidao = new Intent(ListaMandadoActivity.this, ListaCertidoes.class);
                startActivity(intentCertidao);
                return true; // consume the menu event*/


            /*case "Fotos":
                Intent intentFoto = new Intent(ListaMandadoActivity.this, FotosMandado.class);
                startActivity(intentFoto);
                return true; // consume the menu event*/

        }
        return true;
    }



















































































    private void abrirMapa(Mandado mandado){


        final JSONObject[] locationEnderecoMandado = {new JSONObject()};
        final Context ctx = this;

        final ProgressDialog progressDialog = ProgressDialog.show(ctx, "", "Verificadno latitude e longitude do endereço...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);


        //String origem = "Rua André Vidal de Negreiros, 06, Moreno  Pernambuco";
        //String origem = "Rua Adalto Barbosa, 288, Moreno  Pernambuco";

        //String URL = "https://maps.googleapis.com/maps/api/directions/json?origin="+origem+"&destination="+destino+"&key=AIzaSyAPfPXkbxkllvk2ga2Q0o4dBWrAu_RI0U0";

        //String URLl = URL;

        String URL = null;
        try {

            URL = this.getUrlLatLong("Av. Barbosa de Lima", "149", "recife", "pe");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        RequestQueue queue = Volley.newRequestQueue(ctx);

        //String UrlCity = "http://maps.googleapis.com/maps/api/geocode/json?address=" + NameString + "&sensor=false";

        JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject location;
                try {
                    // Get JSON Array called "results" and then get the 0th
                    // complete object as JSON
                    locationEnderecoMandado[0] = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

                    double la = (double) locationEnderecoMandado[0].get("lat");
                    double lo = (double) locationEnderecoMandado[0].get("lng");

                    String url = "geo:"+la+","+lo+"";

                    //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:37.827500,-122.481670"));
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    //i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    ctx.startActivity(i);


                    progressDialog.dismiss();

                    // Get the value of the attribute whose name is
                    // "formatted_string"
                    //stateLocation = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                    // System.out.println(stateLocation.toString());
                } catch (JSONException e1) {
                    progressDialog.dismiss();
                    Toast.makeText(ctx, "Erro1 " + e1.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ctx, "Erro2 " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stateReq);
        queue.start();


    }





















    private void abrirLocalizacao() {


        final Context ctx = this;

        progressDialog = ProgressDialog.show(ListaMandadoActivity.this, "", "Carregando endereço, aguarde...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados


        Funcoes func = new Funcoes();
        JSONObject objEnviar = new JSONObject();
        JSONObject jsonEnviar = new JSONObject();



        final String labelMapaMandado = "Mandado: "+this.mandadoEscolhido.getMAN_Numero_Processo();


        try {

            objEnviar.put("END_ID", this.mandadoEscolhido.getEND_ENDERECO_ID());


            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            jsonEnviar.put("mensagem","buscarEnderecoMandadoAndroid");
            jsonEnviar.put("sucesso", "true");
            jsonEnviar.put("dados", arrayDados);

        } catch (JSONException e) {
            Toast.makeText(ListaMandadoActivity.this, "Erro gerar consulta do endereço do mandado: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                jsonEnviar,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("LongLogTag")
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String msgm = response.getString("mensagem");
                            String sucesso = response.getString("sucesso");
                            progressDialog.dismiss();

                            //Toast.makeText(LoginActivity.this, msgm, Toast.LENGTH_SHORT).show();

                            if(sucesso.equals("true")){

                                if(response.getJSONArray("dados").length() > 0) {
                                    JSONArray dados = response.getJSONArray("dados");


                                    JSONObject itemDados = (JSONObject) dados.get(0);

                                    //final String locName = "1600 Amphitheatre Parkway, Mountain View, CA";

                                    final String locName = itemDados.getString("END_Numero")+" "+itemDados.getString("END_Logradouro")+" "+itemDados.getString("END_Bairro")+", "+itemDados.getString("END_Municipio")+", "+itemDados.getString("END_UF")+", Brasil";
                                    //String locNamea = itemDados.getString("END_Numero")+" "+itemDados.getString("END_Logradouro")+", "+itemDados.getString("END_Municipio")+", "+itemDados.getString("END_UF")+", Brasil";


                                    Geocoder coder = new Geocoder(ListaMandadoActivity.this);
                                    List<Address> address;
                                    Barcode.GeoPoint p1 = null;


                                    address = coder.getFromLocationName(locName,5);
                                    if (address==null) {
                                        //retorna erro sem latitude e longitude
                                        Toast.makeText(ListaMandadoActivity.this, "O JUDIX não conseguiu gerar as cordenadas geograficas do endereço: "+locName+" ", Toast.LENGTH_SHORT).show();

                                    }else{
                                        Address location=address.get(0);
                                        /*location.getLatitude();
                                        location.getLongitude();*/
                                        double la =  location.getLatitude();
                                        double lo =  location.getLongitude();

                                        String url = "geo:"+la+","+lo+"";


                                        String label = "Mandado: ";
                                        String uriBegin = "geo:" + la + "," + lo;
                                        String query = la + "," + lo + "(" + labelMapaMandado + ")";
                                        String encodedQuery = Uri.encode(query);
                                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                                        Uri uri = Uri.parse(uriString);
                                        Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                                        startActivity(mapIntent);
                                    }



                                    //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:37.827500,-122.481670"));
                                    /*Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    ctx.startActivity(i);*/


                                    /*String uri = String.format(Locale.ENGLISH, "geo:%f,%f", la, lo);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    ctx.startActivity(intent);*/















                                    /*FUNCIONA!!!!!!!!
                                    Uri gmmIntentUri = Uri.parse("geo:0,0?q="+locName);
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                        startActivity(mapIntent);
                                    }
                                    FUNCIONA!!!!!!!!*/




                                    //Log.d("ENDERECO_CONSULTA_PONTOS_GPS", locNamea);
                                    //"149 Av. Barbosa de Lima, Recife, PE, Brasil";
                                    /*try {
                                        final List<Address> list = geocoder.getFromLocationName(locName, 2);

                                        Log.d("TotalEnderecoEncontrado", String.valueOf(list.size()));
                                        Log.d("bairro", itemDados.getString("END_Logradouro"));

                                        if ( ! (list == null || list.isEmpty()) ) {



                                            final Address address = list.get(0);


                                            double la = (double) address.getLatitude();
                                            double lo = (double) address.getLongitude();

                                            String url = "geo:"+la+","+lo+"";




                                            //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:37.827500,-122.481670"));
                                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            //i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                            ctx.startActivity(i);


                                            progressDialog.dismiss();
                                            //System.out.println("Geocoder backend present, (lat,lon) = (" + address.getLatitude() + ", " + address.getLongitude() + ")");
                                        }
                                        else {
                                            System.out.println("Geocoder backend not present");
                                        }
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }*/

                                }else {
                                    Toast.makeText(ListaMandadoActivity.this, "Nenhum dado retornado, contate o adminstrador do sistema.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ListaMandadoActivity.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){
                            Toast.makeText(ListaMandadoActivity.this, "Erro converssao dados tela mapamandadoactivity: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            Log.d("AQUI!", e.getMessage().toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListaMandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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











    private void abrirRota(Mandado mandado){

        Context ctx = this;

        GPSHelper gps = new GPSHelper(ctx);
        if(gps.isGPSenabled()){
            Toast.makeText(ctx, "GPS: Sim", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ctx, MapaMandadoActivity.class);
            ctx.startActivity(intent);
        }else{
            Toast.makeText(ctx, "O seu GPS não está ativo!", Toast.LENGTH_SHORT).show();
        }

    }



    public static String getUrlLatLong(String logaradouro, String numero, String cidade, String uf) throws UnsupportedEncodingException {

        //$strEndereco = trim($arrStrDadosEnd[0]["CONF_EnderecoLogradouro"]).", ".trim($arrStrDadosEnd[0]["CONF_EnderecoNumero"]).", ".trim($arrStrDadosEnd[0]["CONF_EnderecoCidade"])." ".trim($arrStrDadosEnd[0]["CONF_EnderecoUf"]);
        //http://maps.googleapis.com/maps/api/geocode/json?address=".str_replace(" ","+", urlencode($strEndereco))."&sensor=false

        String endereco = logaradouro+", "+numero+", "+cidade+" "+uf;
        /*StringBuilder enderecoString = new StringBuilder ( ) ;
        enderecoString.append(logaradouro.toString());
        enderecoString.append(",");
        enderecoString.append(numero.toString());
        enderecoString.append(",");
        enderecoString.append(cidade.toString());
        enderecoString.append(" ");
        enderecoString.append(uf.toString());*/



        StringBuilder urlString = new StringBuilder ( ) ;
        urlString . append ( "http://maps.googleapis.com/maps/api/geocode/json" ) ;
        urlString . append("?address=") ;
        urlString . append ( URLEncoder.encode(endereco, "utf8") ) ;
        urlString . append("&sensor=false") ;// + URLEncoder.encode(input, "utf8")
        return urlString . toString ( ) ;

    }

















    private void abrirMandado(){

        //pra passar o objeto para o SharedPreferences transforma e m json
        SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        Gson gson = new Gson();
        String gsonMandado = gson.toJson(mandadoEscolhido);
        editor.putString("objectMandado", String.valueOf(gsonMandado));
        editor.apply();

        Gson gsonDiretorio = new Gson();
        String jsonDiretorio = sharedPreferences.getString("objectDiretorio", "");
        this.dirCache = gsonDiretorio.fromJson(jsonDiretorio, File.class);


        if (mandadoEscolhido.getMAN_ArquivoTipo().equals("T")){
            this.carregaHTML();

        }else{
            this.carregaPDF();
        }

    }


    private void carregaHTML(){
        //Toast.makeText(ListaMandadoActivity.this, "Abrir: " + mandadoEscolhido.getMAN_Numero_Processo(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ListaMandadoActivity.this, MandadoActivity.class);
        startActivityForResult(intent, 1);
        this.execRegistrarMandadoLidoAndroid();

    }

    private void carregaPDF(){

        //verifica se o pdf ja existe se não existir busca um novo

        //verificar se ja existe o diretorio se não existir cria um novo
        String strPDF = Environment.getExternalStorageDirectory().toString()+File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+mandadoEscolhido.getMAN_ID()+".pdf";
        File PDF = new File(strPDF);

        String a = PDF.toString();

        if (!PDF.exists()) {


            //cria a pasta com o id do mandado
            FilesHandler dirHandler = new FilesHandler();
            dirHandler.setDirMandado(mandadoEscolhido.getMAN_ID().toString());
            try {
                dirHandler.criarDiretorios();
            } catch (IOException e) {
                e.printStackTrace();
            }


            // instantiate it within the onCreate method
            mProgressDialog = new ProgressDialog(ListaMandadoActivity.this);
            mProgressDialog.setMessage("Fazendo download do mandado");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);

            // execute this when the downloader must be fired
            final DownloadTask downloadTask = new DownloadTask(ListaMandadoActivity.this);

            downloadTask.execute(mandadoEscolhido.getMAN_ARQ_Arquivo());

            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                }
            });
            this.execRegistrarMandadoLidoAndroid();
        }else{
            Intent intent = new Intent(ListaMandadoActivity.this, MandadoActivity.class);
            startActivityForResult(intent, 1);
            this.execRegistrarMandadoLidoAndroid();
        }

    }



    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class  DownloadTask extends AsyncTask<String, Integer, String> {

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
                    return "Erro ao requisitar arquivo ao servidor, HTTP retornado: " + connection.getResponseCode()
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

                    String caminho = dirCache.toString()+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+mandadoEscolhido.getMAN_ID()+".pdf";

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
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ListaMandadoActivity.this, MandadoActivity.class);
                startActivityForResult(intent,1);


                /*Intent intent = new Intent(ListaMandadoActivity.this, MandadoActivity.class);
                startActivity(intent);*/


                /*
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Judix/"+ mandadoEscolhido.getMAN_ID()+".pdf");
                Intent intent = new Intent(ListaMandadoActivity.this, MandadoActivity.class);
                intent.setDataAndType(Uri.fromFile(file),"application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);*/

        }


    }




    private void execRegistrarMandadoLidoAndroid(){

        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();

        final ProgressDialog progressDialog = ProgressDialog.show(ListaMandadoActivity.this, "", "Aguarde...");
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


            jsonEnviar.put("mensagem","registrarMandadoLidoAndroid");
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
                                Toast.makeText(ListaMandadoActivity.this, msgm, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(ListaMandadoActivity.this, "Erro converssao dados tela login: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ListaMandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intentDeRetorno) {
        //certidao == 1

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //   =  intentDeRetorno.getStringExtra("retorno");
                //      trata os dados retornados
                listaMandados();
            }
        }


    }





























































    private void visualizarDGOMandado(){
        final Mandado notfy = this.mandadoEscolhido;

        progressDialog = ProgressDialog.show(ListaMandadoActivity.this, "", "Carregando o DGO...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        try{

            JSONObject objEnviar = new JSONObject();
            objEnviar.put("MAN_ID", notfy.getMAN_ID());

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            JSONObject jsonEnviar = new JSONObject();
            jsonEnviar.put("mensagem","gerarDGOMandado");
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
                                String url = response.getString("url");
                                progressDialog.dismiss();

                                //Toast.makeText(LoginActivity.this, msgm, Toast.LENGTH_SHORT).show();

                                //Toast.makeText(ListaNotificacaoMandado.this, msgm, Toast.LENGTH_SHORT).show();
                                if(sucesso.equals("true")){
                                    //baixa o arquivo...



                                    String[] dados = url.split("/");

                                    caminhoDGO = dados[(dados.length-1)];


                                    //download

                                    // instantiate it within the onCreate method
                                    mProgressDialog = new ProgressDialog(ListaMandadoActivity.this);
                                    mProgressDialog.setMessage("Fazendo download do DGO");
                                    mProgressDialog.setIndeterminate(true);
                                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    mProgressDialog.setCancelable(true);

                                    String arqu = Environment.getExternalStorageDirectory().toString()+ File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+notfy.getMAN_ID()+"_DGO.pdf";

                                    // execute this when the downloader must be fired
                                    final ListaMandadoActivity.DownloadTask2 downloadTask2 = new ListaMandadoActivity.DownloadTask2(ListaMandadoActivity.this, arqu);

                                    downloadTask2.execute(url);

                                    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            downloadTask2.cancel(true);
                                        }
                                    });


                                }
                            }catch (Exception e){
                                progressDialog.dismiss();
                                //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                                Toast.makeText(ListaMandadoActivity.this, "Erro converssao dados tela notificacao: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(ListaMandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ListaMandadoActivity.this, "Erro ao gerar a notificacao: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }







    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class  DownloadTask2 extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private String CaminhoArquivo = null;

        public DownloadTask2(Context context, String caminhoArquivo) {
            this.context = context;
            this.CaminhoArquivo = caminhoArquivo;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {



                //String strUrl = "https://judix.com.br/modulos/mandado/gerenciar/fotoMandado/"+sUrl[0]+"_DGO.pdf";
                String strUrl = sUrl[0];


                String[] dados = strUrl.split("/");

                String nomeArquivo = dados[(dados.length-1)];


                URL url = new URL(strUrl);
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

                    //pra passar o objeto para o SharedPreferences transforma e m json
                    SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();


                    Gson gson = new Gson();
                    String gsonMandado = gson.toJson(mandadoEscolhido);
                    editor.putString("objectMandado", String.valueOf(gsonMandado));
                    editor.apply();

                    Gson gsonDiretorio = new Gson();
                    String jsonDiretorio = sharedPreferences.getString("objectDiretorio", "");
                    File dirCache = gsonDiretorio.fromJson(jsonDiretorio, File.class);


                    String caminho1 = dirCache.toString()+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+nomeArquivo;



                    output = new FileOutputStream(caminho1);

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
                " imprmindo DGO "+this.mandadoEscolhido.getMAN_ID();

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


                //ATENCAO AQI
                //File pdfile = new File(caminhoDGO);
                String caminhoPrint =Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+caminhoDGO;
                File pdfile = new File(caminhoPrint );

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



}













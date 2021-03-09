package com.app.alg.judix.telaAssinaturasMandado;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.app.alg.judix.model.AssinaturaMandado;
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.telaAssinatura.Tela;
import com.app.alg.judix.telaMandado.DocumentoDestinatarioMandado;
import com.app.alg.judix.util.Funcoes;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListaAssinaturasMandadoActivity extends AppCompatActivity {

    public static Activity activityListaAssinaturasMandadoActivity;

    ImageButton btnPesquisaMandado;
    ProgressDialog progressDialog;

    Mandado mandadoEscolhido;

    ProgressDialog mProgressDialog;

    Button buttonAdicionarAssinatura;
    Button buttonAdicionarAssinaturaDestinatario;

    AssinaturaMandado assinaturaMandadoEscolhido = new AssinaturaMandado();

    ListView mainListViewAssinatura = null;

    String assinaturaOficial="";
    String nomeOficial = "";
    String CPFOficial = "";

    ItemAssinaturaMandadoAdapter adapterAssinatura = null;

    private static final ArrayList<AssinaturaMandado> listaDeAssinaturas = new ArrayList<AssinaturaMandado>();


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityListaAssinaturasMandadoActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_assinatura_mandado);

        buttonAdicionarAssinaturaDestinatario = (Button) findViewById(R.id.buttonAdicionarAssinaturaDestinatario);
        buttonAdicionarAssinaturaDestinatario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDoc = new Intent(ListaAssinaturasMandadoActivity.this, DocumentoDestinatarioMandado.class);
                startActivityForResult(intentDoc, 1);
            }
        });

        buttonAdicionarAssinatura = (Button) findViewById(R.id.buttonAdicionarAssinatura);
        buttonAdicionarAssinatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaAssinaturasMandadoActivity.this, DadosAssinaturaMandadoPartesRelacionadas.class);
                intent.putExtra("telaEnviada", "DadosAssinaturaMandado");
                startActivityForResult(intent, 1);
            }
        });



        //identifica o mandado
        SharedPreferences pref1 = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref1.getString("objectMandado", "");
        assinaturaOficial = pref1.getString("USU_Assinatura", "");
        nomeOficial = pref1.getString("USU_Nome", "");
        CPFOficial = pref1.getString("USU_CPF", "");

        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);

        /*String docDestMan  = this.mandadoEscolhido.getMAN_DestinatarioDoc();
        if (!docDestMan.equals("null")){
            buttonAdicionarAssinaturaDestinatario.setVisibility(View.INVISIBLE);
        }else{
            //bloqueia a adicao se ja existir
            buttonAdicionarAssinaturaDestinatario.setVisibility(View.VISIBLE);
        }*/

        try{
            Funcoes func = new Funcoes();

            if(func.checkConexao(ListaAssinaturasMandadoActivity.this)){
                this.listarAssinaturas();
            }else{
                Toast toast = Toast.makeText(this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                toast.show();
            }
        }catch (Exception e){
            Toast toast = Toast.makeText(this,e.getMessage().toString(), Toast.LENGTH_LONG);
            toast.show();
        }

        //btnPesquisaMandado.requestFocus();

    }

    /*private void execSalvarAssinaturasMandado(){

        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();

        final ProgressDialog progressDialog = ProgressDialog.show(ListaAssinaturasMandadoActivity.this, "", "Gravando as assinaturas, aguarde...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        //final int MAN_ID, final int CEM_ID, final String MAN_CertidaoTexto


        String URL = "https://www.judix.com.br/modulos/ws/index.php";
        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados

        try {



            JSONArray arrayDados = new JSONArray();
            for(int intI=0; intI<listaDeAssinaturas.size(); intI++ ){
                AssinaturaMandado assinatura = new AssinaturaMandado();
                assinatura = listaDeAssinaturas.get(intI);

                JSONObject objEnviarAssinatura = new JSONObject();

                objEnviarAssinatura.put("ASM_Assintura", assinatura.getAssinatura().toString());
                objEnviarAssinatura.put("ASM_Agente", assinatura.getAgente().toString());
                objEnviarAssinatura.put("ASM_Nome", assinatura.getNome().toString());
                objEnviarAssinatura.put("ASM_TipoDocumento", assinatura.getTipoDocumento().toString());
                objEnviarAssinatura.put("ASM_NumeroDocumento", assinatura.getNumeroDocumento().toString());
                objEnviarAssinatura.put("MAN_ID", mandadoEscolhido.getMAN_ID().toString());

                arrayDados.put(intI, objEnviarAssinatura);

            }




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
                                Toast.makeText(ListaAssinaturasMandadoActivity.this, msgm, Toast.LENGTH_SHORT).show();
                                //abre a tela de gerencia de assinatura..
                                //Intent intentAssinaturas = new Intent(ListaAssinaturasMandadoActivity.this, ListaAssinaturasMandadoActivity.class);
                                //startActivity(intentAssinaturas);
                                finish();
                            }else{
                                Toast.makeText(ListaAssinaturasMandadoActivity.this, msgm, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(ListaAssinaturasMandadoActivity.this, "Erro converssao dados tela lista assinatura mandado: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ListaAssinaturasMandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
    }*/









    private void listarAssinaturas() {
        inicializarLista();


        progressDialog = ProgressDialog.show(ListaAssinaturasMandadoActivity.this, "", "Carregando assinaturas...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        JSONObject objEnviar = new JSONObject();
        JSONObject jsonEnviar = new JSONObject();

        try {


            objEnviar.put("MAN_ID", this.mandadoEscolhido.getMAN_ID());

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            jsonEnviar.put("mensagem","listarAssinaturasMandadoAndroid");
            jsonEnviar.put("sucesso", "true");
            jsonEnviar.put("dados", arrayDados);

        } catch (JSONException e) {
            Toast.makeText(ListaAssinaturasMandadoActivity.this, "Erro ao gerar a consulta das assinaturas do mandado."+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                                    //listaDeAssinaturas.clear();
                                    for(int i=0;  i<tot; i++){
                                        JSONObject itemDados = (JSONObject) dados.get(i);
                                        AssinaturaMandado assinatura = new AssinaturaMandado();

                                        assinatura.setId(itemDados.getString("ASM_ID"));
                                        assinatura.setNome(itemDados.getString("ASM_Nome"));
                                        assinatura.setTipoDocumento(itemDados.getString("ASM_TipoDocumento"));
                                        assinatura.setAgente(itemDados.getString("ASM_Agente"));
                                        assinatura.setAssinatura(itemDados.getString("ASM_Assintura"));
                                        assinatura.setNumeroDocumento(itemDados.getString("ASM_NumeroDocumento"));

                                        listaDeAssinaturas.add(assinatura);
                                    }

                                }else {
                                    Toast.makeText(ListaAssinaturasMandadoActivity.this, "Nenhum dado retornado, contate o adminstrador do sistema.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ListaAssinaturasMandadoActivity.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                            }

                            mainListViewAssinatura = (ListView) findViewById(R.id.listViewAssinaturasMandado);

                            adapterAssinatura = new ItemAssinaturaMandadoAdapter(ListaAssinaturasMandadoActivity.this, listaDeAssinaturas);
                            mainListViewAssinatura.setAdapter(adapterAssinatura);
                            adapterAssinatura.notifyDataSetChanged();

                            mainListViewAssinatura.setClickable(true);
                            mainListViewAssinatura.setLongClickable(true);

                            mainListViewAssinatura.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                                @Override
                                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                                    // Get the info on which item was selected
                                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

                                    int posicao = info.position;

                                    assinaturaMandadoEscolhido = listaDeAssinaturas.get(info.position);

                                    if(assinaturaMandadoEscolhido.getId().equals("-1") || assinaturaMandadoEscolhido.getId().equals("-2")){


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




                                    }



                                }

                            });

                            mainListViewAssinatura.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    // TODO Auto-generated method stub
                                    return false;
                                }
                            });



                        }catch (Exception e){
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(ListaAssinaturasMandadoActivity.this, "Erro converssao dados tela login: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListaAssinaturasMandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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



    /*public void atualizaLista(){


        mainListViewAssinatura = (ListView) findViewById(R.id.listViewAssinaturasMandado);
        final ItemAssinaturaMandadoAdapter adapterAssinatura = new ItemAssinaturaMandadoAdapter(ListaAssinaturasMandadoActivity.this, listaDeAssinaturas);
        mainListViewAssinatura.setAdapter(adapterAssinatura);
        adapterAssinatura.notifyDataSetChanged();

        mainListViewAssinatura.setClickable(true);
        mainListViewAssinatura.setLongClickable(true);

        mainListViewAssinatura.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                // Get the info on which item was selected
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

                int posicao = info.position;

                AssinaturaMandado assinaturaMandado = new AssinaturaMandado();
                assinaturaMandado = listaDeAssinaturas.get(info.position);


                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_clica_item_assintura_mandado, menu);


                LayoutInflater layoutInflater = getLayoutInflater();
                View view = (View) layoutInflater.inflate(
                        R.layout.menu_titulo_mandado_clicado, null);
                menu.setHeaderView(view);

                TextView tituloMenu2 = (TextView) view.findViewById(R.id.tituloMenuMandadoClicado);
                tituloMenu2.setText("Assinatura de :"+assinaturaMandado.getNome());

            }

        });

        mainListViewAssinatura.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                return false;
            }
        });


    }*/


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

            case "Abrir":
                //this.abrirMandado();
                return true; // consume the menu event

            case "Excluir Assinatura":
                //Intent intent = new Intent(ListaMandadoActivity.this, AgendamentoMandadoLista.class);
                //startActivity(intent);
                //Toast.makeText(ListaAssinaturasMandadoActivity.this, "Excluir: " + assinaturaMandadoEscolhido.getNome(), Toast.LENGTH_SHORT).show();

                try {
                    excluirAssinaturaMandado(assinaturaMandadoEscolhido);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return true; // consume the menu event

            case "Localizar":
                //this.abrirRota(mandadoEscolhido);
                //Toast.makeText(ListaMandadoActivity.this, "Localizacao: " + mandadoEscolhido.getMAN_Numero_Processo(), Toast.LENGTH_SHORT).show();
                return true; // consume the menu event

            /*case "Mapa":
                this.abrirMapa(mandadoEscolhido);
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


    public static void addAssinatura(AssinaturaMandado assinatura){
        listaDeAssinaturas.add(assinatura);
        //listarAssinaturas();
    }

    @Override
    public void onStop() {
        super.onStop();
        //if (VERBOSE) Log.v(TAG, "-- ON STOP --");
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){

            SharedPreferences pref1 = getSharedPreferences("PreferenciaJudix", 0);
            Gson gsonMandados = new Gson();
            String jsonMandado = pref1.getString("objectMandado", "");
            assinaturaOficial = pref1.getString("USU_Assinatura", "");
            nomeOficial = pref1.getString("USU_Nome", "");
            CPFOficial = pref1.getString("USU_CPF", "");
            this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);

            listarAssinaturas();

        }


    }


    public void excluirAssinaturaMandado(AssinaturaMandado obj) throws JSONException {

        //Toast.makeText(ListaAssinaturasMandadoActivity.this, "excluindo assinatura... ", Toast.LENGTH_SHORT).show();return;

        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();

        final ProgressDialog progressDialog = ProgressDialog.show(ListaAssinaturasMandadoActivity.this, "", "Excluindo assinatura, aguarde...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        //final int MAN_ID, final int CEM_ID, final String MAN_CertidaoTexto


        String URL = "https://www.judix.com.br/modulos/ws/index.php";


        try {
            objEnviar.put("ASM_ID", obj.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONArray arrayDados = new JSONArray();
        arrayDados.put(0, objEnviar);


        jsonEnviar.put("mensagem","excluirAssinaturaMandadoAndroid");
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

                            Toast.makeText(ListaAssinaturasMandadoActivity.this, msgm, Toast.LENGTH_SHORT).show();
                            if(sucesso.equals("true")){

                                listarAssinaturas();

                            }

                        }catch (Exception e){
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(ListaAssinaturasMandadoActivity.this, "Erro excluir assinatura: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListaAssinaturasMandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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


    private void inicializarLista(){
        //preenche a primeira assinatura da lista..
        listaDeAssinaturas.clear();
        buttonAdicionarAssinaturaDestinatario.setVisibility(View.INVISIBLE);

        AssinaturaMandado assinaturaOficialLogado = new AssinaturaMandado();
        assinaturaOficialLogado.setId("-1");
        assinaturaOficialLogado.setAgente("Oficial de Justiça");
        assinaturaOficialLogado.setAssinatura(assinaturaOficial);
        assinaturaOficialLogado.setNome(nomeOficial);
        assinaturaOficialLogado.setNumeroDocumento(CPFOficial);
        assinaturaOficialLogado.setTipoDocumento("CPF");
        listaDeAssinaturas.add(assinaturaOficialLogado);

        if (!this.mandadoEscolhido.getMAN_DestinatarioDoc().equals("null")) {
            AssinaturaMandado assinaturaDestiantarioMandado = new AssinaturaMandado();
            assinaturaDestiantarioMandado.setId("-2");
            assinaturaDestiantarioMandado.setAgente("Destinatário");
            assinaturaDestiantarioMandado.setAssinatura("");
            assinaturaDestiantarioMandado.setNome(this.mandadoEscolhido.getMAN_OutraParte());
            assinaturaDestiantarioMandado.setTipoDocumento(this.mandadoEscolhido.getMAN_DestinatarioTipoDoc());
            assinaturaDestiantarioMandado.setNumeroDocumento(this.mandadoEscolhido.getMAN_DestinatarioDoc());
            listaDeAssinaturas.add(assinaturaDestiantarioMandado);
        }else{
            buttonAdicionarAssinaturaDestinatario.setVisibility(View.VISIBLE);

        }


    }
}
























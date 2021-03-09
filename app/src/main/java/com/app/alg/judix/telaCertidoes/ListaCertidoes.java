package com.app.alg.judix.telaCertidoes;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.alg.judix.R;
import com.app.alg.judix.model.Certidao;
import com.app.alg.judix.util.Funcoes;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListaCertidoes extends AppCompatActivity {

    public static Activity activityListaCertidoes;

    //TextView txtTotalCertidoes;
    ProgressDialog progressDialog;

    Spinner spinnerTipoPesquisa;

    private final ArrayList<Certidao> listaDeCertidoes = new ArrayList<Certidao>();
    ListView mainListView = null;
    String tipoConsulta;

    ///para o spin
    private List<String> nomesLista = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityListaCertidoes = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_certidoes);

        //txtTotalCertidoes = (TextView) findViewById(R.id.txtTotalCertidoes);

        try{



            nomesLista.add("POSITIVA");
            nomesLista.add("NEGATIVA");

            //Identifica o Spinner no layout
            spinnerTipoPesquisa = (Spinner) findViewById(R.id.spinnerTipoCertidaoPesquisa);
            //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nomesLista);

            ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipoPesquisa.setAdapter(spinnerArrayAdapter);


            spinnerTipoPesquisa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {

                    Funcoes func = new Funcoes();
                    if(func.checkConexao(ListaCertidoes.this)){
                        switch (parent.getItemAtPosition(posicao).toString()) {
                            case "POSITIVA":  tipoConsulta = "P";
                                listaCertidoes();
                                break;
                            case "NEGATIVA":  tipoConsulta = "N";
                                listaCertidoes();
                                break;
                            default: tipoConsulta = "";
                                listaCertidoes();
                                break;
                        }
                    }else{
                        Toast toast = Toast.makeText(ListaCertidoes.this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                        toast.show();
                    }

                    //pega nome pela posição
                    //nome = parent.getItemAtPosition(posicao).toString();




                    //imprime um Toast na tela com o nome que foi selecionado
                    //Toast.makeText(ListaMandadoActivity.this, "Nome Selecionado: " + nome, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            /*Funcoes func = new Funcoes();
            if(func.checkConexao(ListaCertidoes.this)){
                listaCertidoes();
            }else{
                Toast toast = Toast.makeText(this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                toast.show();
            }*/

        }catch (Exception e){
            Toast toast = Toast.makeText(this,e.getMessage().toString(), Toast.LENGTH_LONG);
            toast.show();
        }


    }





    public void listaCertidoes() {

        progressDialog = ProgressDialog.show(ListaCertidoes.this, "", "Carregando...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados


        Funcoes func = new Funcoes();
        JSONObject objEnviar = new JSONObject();
        JSONObject jsonEnviar = new JSONObject();

        try {
            objEnviar.put("USU_ID", func.getUsuarioId(ListaCertidoes.this));

            objEnviar.put("CEM_Tipo", tipoConsulta); //pode ser n negativo, p positivo e "" pra todos

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            jsonEnviar.put("mensagem","consultarTipoCertidaoAndroid");
            jsonEnviar.put("sucesso", "true");
            jsonEnviar.put("dados", arrayDados);

        } catch (JSONException e) {
            Toast.makeText(ListaCertidoes.this, "Erro gerar consulta certidões."+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

                                    JSONArray dados2 = dados;

                                    //dados.length() faz um for e preenceh os itens da exibição
                                    int tot = dados.length();

                                    setTitle("Certidões: "+tot);

                                    //txtTotalCertidoes.setText("Certidões: "+tot);

                                    //limpa pra sempre trazer uma nova
                                    listaDeCertidoes.clear();
                                    for(int i=0;  i<tot; i++){
                                        JSONObject itemDados = (JSONObject) dados.get(i);
                                        Certidao certidao = new Certidao();

                                        certidao.setId(itemDados.getString("CEM_ID"));
                                        certidao.setNome(itemDados.getString("CEM_Nome"));
                                        certidao.setCabecalho(itemDados.getString("CEM_Cabecalho"));
                                        certidao.setTexto(itemDados.getString("CEM_Texto"));
                                        certidao.setRodape(itemDados.getString("CEM_Rodape"));
                                        certidao.setTipo(itemDados.getString("CEM_Tipo"));

                                        listaDeCertidoes.add(certidao);

                                    }


                                    mainListView = (ListView) findViewById(R.id.listViewCertidoes);
                                    ItemCertidaoAdapter adapterCertidao = new ItemCertidaoAdapter(ListaCertidoes.this, listaDeCertidoes);
                                    mainListView.setAdapter(adapterCertidao);
                                    adapterCertidao.notifyDataSetChanged();

                                    mainListView.setClickable(true);
                                    mainListView.setLongClickable(true);

                                    mainListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                                        @Override
                                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                                            // Get the info on which item was selected
                                            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                                            int posicao = info.position;

                                            Certidao cert = new Certidao();
                                            cert = listaDeCertidoes.get(info.position);



                                            //carregar a sessao com a certidao escolhida
                                            //pra passar o objeto para o SharedPreferences transforma e m json
                                            SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();


                                            Gson gson = new Gson();
                                            String gsonCert = gson.toJson(cert);
                                            editor.putString("objectCertidao", String.valueOf(gsonCert));
                                            editor.apply();


                                            Toast.makeText(ListaCertidoes.this, "Certidao escolhida: "+cert.getNome(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ListaCertidoes.this, CertidaoDoMandado.class);
                                            startActivityForResult(intent,1);
                                            //finish();

                                            /*mandadoEscolhido = mandado;

                                            MenuInflater inflater = getMenuInflater();
                                            inflater.inflate(R.menu.menu_clica_item_mandado, menu);
                                            menu.setHeaderTitle("Mandado: " + mandado.getMAN_Numero_Processo());*/
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


                                }else {
                                    Toast.makeText(ListaCertidoes.this, "Nenhum dado retornado, contate o adminstrador do sistema.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ListaCertidoes.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();

                                //txtTotalCertidoes.setText("Certidões: 0");
                                setTitle("Certidões: 0");

                                listaDeCertidoes.clear();
                                ItemCertidaoAdapter adapterCertidao = new ItemCertidaoAdapter(ListaCertidoes.this, listaDeCertidoes);
                                mainListView = (ListView) findViewById(R.id.listViewCertidoes);
                                mainListView.setAdapter(adapterCertidao);

                            }

                        }catch (Exception e){
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(ListaCertidoes.this, "Erro converssao dados tela login: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListaCertidoes.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
        queue.start();
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

    public void onBackPressed() {

        Intent intentDeRetorno = new Intent();
        intentDeRetorno.putExtra("retorno", 1);
        int resultCode = RESULT_OK;
        setResult (resultCode, intentDeRetorno);
        finish();

        //return;
    }


}



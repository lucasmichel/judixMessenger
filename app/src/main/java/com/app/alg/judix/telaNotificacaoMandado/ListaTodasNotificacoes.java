package com.app.alg.judix.telaNotificacaoMandado;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
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
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.model.Notificacao;
import com.app.alg.judix.util.Funcoes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ListaTodasNotificacoes extends Activity {


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
        setContentView(R.layout.activity_lista_todas_notificacoes);

        mainListViewNotificacao = (ListView) findViewById(R.id.listViewTodasNotificacoes);
        setTitle("Notificações Existentes");
        try{
            Funcoes func = new Funcoes();

            if(func.checkConexao(ListaTodasNotificacoes.this)){
                this.listarTodasNotificacoes();
            }else{
                Toast toast = Toast.makeText(this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                toast.show();
            }
        }catch (Exception e){
            Toast toast = Toast.makeText(this,e.getMessage().toString(), Toast.LENGTH_LONG);
            toast.show();
        }


    }


    private void listarTodasNotificacoes() {
        progressDialog = ProgressDialog.show(ListaTodasNotificacoes.this, "", "Carregando notificações...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        JSONObject objEnviar = new JSONObject();
        JSONObject jsonEnviar = new JSONObject();

        try {

            Funcoes func = new Funcoes();
            objEnviar.put("USU_CPF", func.getUsuarioCPF(ListaTodasNotificacoes.this));

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            jsonEnviar.put("mensagem","listarNotificacoesPorOficial");
            jsonEnviar.put("sucesso", "true");
            jsonEnviar.put("dados", arrayDados);

        } catch (JSONException e) {
            Toast.makeText(ListaTodasNotificacoes.this, "Erro ao gerar a consulta de todas as notificações ", Toast.LENGTH_SHORT).show();
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
                                        notificacao.setMAN_ID(itemDados.getString("MAN_ID"));
                                        notificacao.setMAN_Numero_Processo(itemDados.getString("MAN_Numero_Processo"));

                                        listaDeNotificacoes.add(notificacao);

                                    }

                                }else {
                                    Toast.makeText(ListaTodasNotificacoes.this, "Nenhum dado retornado, contate o adminstrador do sistema.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ListaTodasNotificacoes.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                            }



                            final ItemTodasNotificacaoAdapter adapterNot = new ItemTodasNotificacaoAdapter(ListaTodasNotificacoes.this, listaDeNotificacoes);
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
                                    inflater.inflate(R.menu.menu_clica_item_todas_notificacao , menu);


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
                            Toast.makeText(ListaTodasNotificacoes.this, "Erro converssao dados tela login: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListaTodasNotificacoes.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

package com.app.alg.judix.telaMandado;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.alg.judix.R;
import com.app.alg.judix.model.Mandado;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AgendamentoMandadoLista extends AppCompatActivity implements View.OnClickListener {

    Mandado mandadoEscolhido;
    Button btnAgendar;
    TextView textListaAgendamento, textListaAgendamentoTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento_lista);

        //identifica o mandado
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref.getString("objectMandado", "");
        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);

        String mandado = "Agendamentos";

        textListaAgendamento = (TextView) findViewById(R.id.textListaAgendamento);
        textListaAgendamentoTitulo = (TextView) findViewById(R.id.textListaAgendamentoTitulo);

        textListaAgendamentoTitulo.setText("ID: "+this.mandadoEscolhido.getMAN_ID().toString()+"\nProc.: "+this.mandadoEscolhido.getMAN_Numero_Processo().toString());

        setTitle(mandado.toString());
        this.execListarAgendamento();

        btnAgendar = (Button) findViewById(R.id.buttonAgendar);
        btnAgendar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAgendar:
                finish();
                Intent intent = new Intent(AgendamentoMandadoLista.this, AgendamentoMandado.class);
                startActivity(intent);
                /*int day = componenteDate.getDayOfMonth();
                int month = componenteDate.getMonth();
                int year =  componenteDate.getYear();

                String dataGravar = day+"/"+month+"/"+year;

                this.execSalvarAgendamento(dataGravar);*/

                break;
            default:
                break;
        }
    }

    public void execListarAgendamento(){

        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();

        final ProgressDialog progressDialog = ProgressDialog.show(AgendamentoMandadoLista.this, "", "Carregando...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);


        String URL = "https://www.judix.com.br/modulos/ws/index.php";
        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados

        try {

            objEnviar.put("MAN_ID", mandadoEscolhido.getMAN_ID().toString());

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);


            jsonEnviar.put("mensagem","listarAgendamentoMandadoAndroid");
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

                                //Toast.makeText(AgendamentoMandadoLista.this, msgm, Toast.LENGTH_SHORT).show();
                            }else{
                                //Toast.makeText(AgendamentoMandadoLista.this, msgm, Toast.LENGTH_SHORT).show();
                            }
                            textListaAgendamento.setText(msgm);

                        }catch (Exception e){
                            progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(AgendamentoMandadoLista.this, "Erro converssao dados tela login: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(AgendamentoMandadoLista.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

}

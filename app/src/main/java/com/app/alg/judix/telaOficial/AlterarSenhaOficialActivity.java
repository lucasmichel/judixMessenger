package com.app.alg.judix.telaOficial;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.alg.judix.R;
import com.app.alg.judix.util.Funcoes;

import org.json.JSONArray;
import org.json.JSONObject;

public class AlterarSenhaOficialActivity extends AppCompatActivity implements OnClickListener {

    private EditText edtSenhaAtual, edtNovaSenha;
    private int USU_ID;
    private String USU_CPF;

    private Button btnSalvarSenhaOficial;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_senha_oficial);

        edtSenhaAtual = (EditText) findViewById(R.id.edtSenhaAtual);
        edtNovaSenha = (EditText) findViewById(R.id.edtNovaSenha);

        btnSalvarSenhaOficial = (Button) findViewById(R.id.btnSalvarSenhaOficial);
        btnSalvarSenhaOficial.setOnClickListener(this);

        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        USU_ID = Integer.parseInt(pref.getString("USU_ID", null));
        USU_CPF = pref.getString("USU_CPF", null);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSalvarSenhaOficial:
                try{

                    Funcoes func = new Funcoes();

                    if(func.checkConexao(AlterarSenhaOficialActivity.this)){
                        this.validarDados();
                    }else{
                        Toast toast = Toast.makeText(this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }catch (Exception e){
                    Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }

                break;
            default:
                break;
        }


    }

    private void validarDados(){


        String USU_Senha = edtSenhaAtual.getText().toString();
        String USU_Nova_Senha = edtNovaSenha.getText().toString();

        if(USU_Senha.equals("")){
            Toast.makeText(AlterarSenhaOficialActivity.this, "Informe a senha antiga!", Toast.LENGTH_SHORT).show();
        }else{
            if(USU_Nova_Senha.equals("")){
                Toast.makeText(AlterarSenhaOficialActivity.this, "Informe a senha nova!", Toast.LENGTH_SHORT).show();
            }else{
                this.salvarDados(USU_Senha, USU_Nova_Senha);
            }
        }

    }


    private void salvarDados( final String USU_Senha, final String USU_Nova_Senha){
        progressDialog = ProgressDialog.show(AlterarSenhaOficialActivity.this, "", "Salvando...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        try{

            JSONObject objEnviar = new JSONObject();
            objEnviar.put("USU_Senha", USU_Senha);
            objEnviar.put("USU_Nova_Senha", USU_Nova_Senha);
            objEnviar.put("USU_CPF", USU_CPF);

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);


            JSONObject jsonEnviar = new JSONObject();
            jsonEnviar.put("mensagem","alterarSenhaOficialAndroid");
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

                                Toast.makeText(AlterarSenhaOficialActivity.this, msgm, Toast.LENGTH_SHORT).show();
                                if(sucesso.equals("true")){

                                    /*SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("USU_Telefone", USU_Telefone);
                                    editor.putString("USU_Email", USU_Email);
                                    editor.putString("USU_Transmissao", USU_Transmissao);
                                    editor.apply();*/

                                    Toast.makeText(AlterarSenhaOficialActivity.this, "Senha alterada com sucesos!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }



                            }catch (Exception e){
                                progressDialog.dismiss();
                                //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                                Toast.makeText(AlterarSenhaOficialActivity.this, "Erro converssao dados tela login: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(AlterarSenhaOficialActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

        }catch (Exception e ){
            progressDialog.dismiss();
            Toast.makeText(AlterarSenhaOficialActivity.this, "Erro ao salvar: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

}

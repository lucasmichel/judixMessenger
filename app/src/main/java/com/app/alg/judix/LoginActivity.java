package com.app.alg.judix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.app.alg.judix.util.Funcoes;
import com.app.alg.judix.util.Mask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String NOME_PREFERENCIA = "PreferenciaJudix";

    private EditText cpf, senha;
    private Button btnLogar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cpf = (EditText) findViewById(R.id.editTextCpf);
        cpf.addTextChangedListener(Mask.insert(Mask.CPF_MASK, cpf));
        senha = (EditText) findViewById(R.id.editTextSenha);
        btnLogar = (Button) findViewById(R.id.buttonLogin);

        btnLogar.setOnClickListener(this);
        btnLogar.requestFocus();
        btnLogar.setFocusableInTouchMode(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                try{

                    Funcoes func = new Funcoes();

                    if(func.checkConexao(LoginActivity.this)){
                        this.login(cpf.getText().toString(), senha.getText().toString());
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



    private void login(final String cpf, final String senha) throws JSONException {
        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Carregando...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        String URL = "https://www.judix.com.br/modulos/ws/index.php";
        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados
        JSONObject objEnviar = new JSONObject();
        objEnviar.put("USU_Login", cpf);
        objEnviar.put("USU_Senha", senha);

        JSONArray arrayDados = new JSONArray();
        arrayDados.put(0, objEnviar);

        JSONObject jsonEnviar = new JSONObject();
        jsonEnviar.put("mensagem","logarAndroid");
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

                            if(sucesso.equals("true")){
                                if(response.getJSONArray("dados").length() > 0){
                                    JSONArray dados = response.getJSONArray("dados");

                                    JSONArray dados2 = dados;
                                    JSONObject itemDados = (JSONObject) dados.get(0);
                                    int IdUsuario = itemDados.getInt("USU_ID");

                                    SharedPreferences sharedPreferences = getSharedPreferences(NOME_PREFERENCIA, 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("USU_ID", String.valueOf(IdUsuario));
                                    editor.putString("USU_Nome", itemDados.getString("USU_Nome"));
                                    editor.putString("USU_DataHoraUltimoAcesso", itemDados.getString("USU_DataHoraUltimoAcesso"));
                                    editor.putString("USU_Assinatura", itemDados.getString("USU_Assinatura"));
                                    editor.putString("USU_Transmissao", itemDados.getString("USU_Transmissao"));
                                    editor.putString("USU_Email", itemDados.getString("USU_Email"));
                                    editor.putString("USU_Telefone", itemDados.getString("USU_Telefone"));
                                    editor.putString("USU_EnderecoContato", itemDados.getString("USU_EnderecoContato"));
                                    editor.putString("USU_CPF", itemDados.getString("USU_CPF"));
                                    editor.apply();

                                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                    startActivity(intent);
                                    LoginActivity.this.onBackPressed();

                                }else {
                                    Toast.makeText(LoginActivity.this, "Nenhum dado retornado, contate o adminstrador do sistema.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Log.d("JUDIX", "Error logarAndroid: " + "Login ou senha incorretos");
                                Toast.makeText(getBaseContext(), "Login ou senha incorretos.", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(LoginActivity.this, "Erro converssao dados tela login: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
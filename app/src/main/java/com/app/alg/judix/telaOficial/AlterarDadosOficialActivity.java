package com.app.alg.judix.telaOficial;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.app.alg.judix.util.Mask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlterarDadosOficialActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioButton rbSim, rbNao;
    private RadioGroup rbGrgupo;

    private EditText email, fone, textEnderecoContato;


    private int USU_ID;
    private String USU_CPF;

    private Button btnSalvarDadosOficial;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_dados_oficial);

        email = (EditText) findViewById(R.id.edtEmailOficial);
        fone = (EditText) findViewById(R.id.edtFoneOficial);
        fone.addTextChangedListener(Mask.insert(Mask.CELULAR_MASK, fone));

        textEnderecoContato = (EditText) findViewById(R.id.textEnderecoContato);

        rbSim = (RadioButton) findViewById(R.id.dbTrasnmissaoSim);
        rbNao = (RadioButton) findViewById(R.id.dbTrasnmissaoNao);
        rbGrgupo = (RadioGroup) findViewById(R.id.rbGroupTransmissao);

        btnSalvarDadosOficial = (Button) findViewById(R.id.btnSalvarDadosOficial);
        btnSalvarDadosOficial.setOnClickListener(this);

        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        //USU_ID = Integer.parseInt(pref.getString("USU_ID", null));
        USU_CPF = pref.getString("USU_CPF", null);

        if( pref.getString("USU_Email", null).length() > 0 ) {
            email.setText(pref.getString("USU_Email", null));
        }

        if( pref.getString("USU_Telefone", null).length() > 0 ) {
            fone.setText(pref.getString("USU_Telefone", null));
        }

        if(pref.getString("USU_Transmissao", null).equals("N")){
            rbNao.setChecked(true);
            rbSim.setChecked(false);
        }else{
            rbNao.setChecked(false);
            rbSim.setChecked(true);
        }

        if( pref.getString("USU_EnderecoContato", null).length() > 0 ) {
            textEnderecoContato.setText(pref.getString("USU_EnderecoContato", null));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSalvarDadosOficial:
                try{

                    Funcoes func = new Funcoes();

                    if(func.checkConexao(AlterarDadosOficialActivity.this)){
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

        String USU_Transmissao = "";
        String USU_Telefone = fone.getText().toString();
        String USU_Email = email.getText().toString();
        String USU_EnderecoContato = textEnderecoContato.getText().toString();

        int id = rbGrgupo.getCheckedRadioButtonId();
        if (id == -1){
            //no item selected
            Toast.makeText(AlterarDadosOficialActivity.this, "Escolha o tipo da transmissão!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (id == R.id.dbTrasnmissaoSim) {
                //Do something with the button
                USU_Transmissao = "S";
                //Toast.makeText(AlterarDadosOficialActivity.this, "Transmissao sim", Toast.LENGTH_SHORT).show();
            } else {
                USU_Transmissao = "N";
                //Toast.makeText(AlterarDadosOficialActivity.this, "Transmissao nao", Toast.LENGTH_SHORT).show();
            }
            if(!this.isValidMail(USU_Email)){
                Toast.makeText(AlterarDadosOficialActivity.this, "Informe um e-mail válido!", Toast.LENGTH_SHORT).show();
            }else{
                this.salvarDados(USU_Transmissao, USU_Telefone, USU_Email, USU_EnderecoContato );
            }

        }

    }

    private boolean isValidMail(String email2)
    {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email2);
        check = m.matches();

        return check;
    }

    private void salvarDados( final String USU_Transmissao, final String USU_Telefone, final String USU_Email, final String USU_EnderecoContato){
        progressDialog = ProgressDialog.show(AlterarDadosOficialActivity.this, "", "Salvando...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        try{

            JSONObject objEnviar = new JSONObject();
            objEnviar.put("USU_Telefone", USU_Telefone);
            objEnviar.put("USU_Email", USU_Email);
            objEnviar.put("USU_Transmissao", USU_Transmissao);
            objEnviar.put("USU_EnderecoContato", USU_EnderecoContato);

            objEnviar.put("USU_CPF", USU_CPF);

            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);


            JSONObject jsonEnviar = new JSONObject();
            jsonEnviar.put("mensagem","alterarDadosOficialAndroid");
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

                                Toast.makeText(AlterarDadosOficialActivity.this, msgm, Toast.LENGTH_SHORT).show();
                                if(sucesso.equals("true")){

                                    SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("USU_Telefone", USU_Telefone);
                                    editor.putString("USU_Email", USU_Email);
                                    editor.putString("USU_Transmissao", USU_Transmissao);
                                    editor.putString("USU_EnderecoContato", USU_EnderecoContato);
                                    editor.apply();

                                    //Toast.makeText(AlterarDadosOficialActivity.this, "Dados alterados com sucesso.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }catch (Exception e){
                                progressDialog.dismiss();
                                //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                                Toast.makeText(AlterarDadosOficialActivity.this, "Erro converssao dados tela login: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(AlterarDadosOficialActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(AlterarDadosOficialActivity.this, "Erro ao salvar: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }
}



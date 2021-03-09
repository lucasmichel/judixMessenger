package com.app.alg.judix.telaNotificacaoMandado;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.app.alg.judix.model.Certidao;
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.model.Notificacao;
import com.app.alg.judix.telaOficial.AlterarDadosOficialActivity;
import com.app.alg.judix.util.Funcoes;
import com.app.alg.judix.util.Mask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotificacaoMandado extends AppCompatActivity implements View.OnClickListener {

    RadioButton radioButonTelefone;
    RadioButton radioButonEndereco;
    RadioButton radioButonAmbos;

    Notificacao notificacaoParaAlteracao;

    TextView textViewTelefone;
    EditText editTextTelefone;

    TextView textViewEndereco;
    EditText editTextEndereco;

    TextView textViewData;
    EditText editTextData;

    TextView textViewHora;
    EditText editTextHora;

    LinearLayout linearLayoutEndereco;
    LinearLayout linearLayoutTelefone;

    Button btnSalvarNotificacao;
    Mandado mandadoEscolhido;

    ProgressDialog progressDialog;
    String USU_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacao_mandado);


        editTextTelefone = (EditText) findViewById(R.id.editTextTelefone);
        editTextTelefone.addTextChangedListener(Mask.insert(Mask.CELULAR_MASK, editTextTelefone));

        editTextHora = (EditText) findViewById(R.id.editTextHora);
        editTextHora.addTextChangedListener(Mask.insert(Mask.HORA_MASK, editTextHora));

        editTextData = (EditText) findViewById(R.id.editTextData);
        editTextData.addTextChangedListener(Mask.insert(Mask.DATA_MASK, editTextData));



        radioButonEndereco = (RadioButton) findViewById(R.id.radioButonEndereco);
        View.OnClickListener endereco_radio_listener = new View.OnClickListener(){
            public void onClick(View v) {
                //Your Implementaions...
                escolherTipoNotificacao();
            }
        };
        radioButonEndereco.setOnClickListener(endereco_radio_listener);


        radioButonTelefone = (RadioButton) findViewById(R.id.radioButonTelefone);
        View.OnClickListener telefone_radio_listener = new View.OnClickListener(){
            public void onClick(View v) {
                //Your Implementaions...
                escolherTipoNotificacao();

            }
        };
        radioButonTelefone.setOnClickListener(telefone_radio_listener);

        radioButonAmbos = (RadioButton) findViewById(R.id.radioButonAmbos);
        View.OnClickListener ambos_radio_listener = new View.OnClickListener(){
            public void onClick(View v) {
                //Your Implementaions...
                escolherTipoNotificacao();

            }
        };
        radioButonAmbos.setOnClickListener(ambos_radio_listener);


        textViewTelefone = (TextView) findViewById(R.id.textViewTelefone);
        editTextTelefone = (EditText) findViewById(R.id.editTextTelefone);

        textViewEndereco = (TextView) findViewById(R.id.textViewEndereco);
        editTextEndereco = (EditText) findViewById(R.id.editTextEndereco);

        textViewData = (TextView) findViewById(R.id.textViewData);
        editTextData = (EditText) findViewById(R.id.editTextData);

        textViewHora = (TextView) findViewById(R.id.textViewHora);
        editTextHora = (EditText) findViewById(R.id.editTextHora);



        linearLayoutTelefone = (LinearLayout) findViewById(R.id.linearLayoutTelefone);
        linearLayoutEndereco = (LinearLayout) findViewById(R.id.linearLayoutEndereco);

        btnSalvarNotificacao = (Button) findViewById(R.id.btnSalvarNotificacao);
        btnSalvarNotificacao.setOnClickListener(this);

        linearLayoutTelefone.setVisibility(View.INVISIBLE);
        linearLayoutEndereco.setVisibility(View.INVISIBLE);



        //identifica o mandado
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);

        if( pref.getString("USU_Telefone", null).length() > 0 ) {
            editTextTelefone.setText(pref.getString("USU_Telefone", null));
        }

        if( pref.getString("USU_EnderecoContato", null).length() > 0 ) {
            editTextEndereco.setText(pref.getString("USU_EnderecoContato", null));
        }



        Gson gsonMandados = new Gson();
        String jsonMandado = pref.getString("objectMandado", "");
        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);


        String jsonNot = pref.getString("objectNotificacao", "");
        this.notificacaoParaAlteracao = gsonMandados.fromJson(jsonNot, Notificacao.class);



        if(this.notificacaoParaAlteracao.getNOT_ID() == null){
            /*Toast toast = Toast.makeText(this,"notificacao SALVAR: "+this.notificacaoParaAlteracao.getNOT_ID(), Toast.LENGTH_LONG);
            toast.show();*/

            //limpa os campos
            radioButonAmbos.setChecked(true);
            escolherTipoNotificacao();

        }else{
            //Toast toast = Toast.makeText(this,"notificacao EDITAR: "+this.notificacaoParaAlteracao.getNOT_ID(), Toast.LENGTH_LONG);
            //toast.show();

            //preenche os campos
            this.prencherCamposEdicao();

        }






        USU_ID = pref.getString("USU_ID", "");


    }

    private void escolherTipoNotificacao(){
        if(radioButonEndereco.isChecked()){
            if(editTextTelefone.getText().length()>0){
                //editTextTelefone.setText("");
            }
            linearLayoutTelefone.setVisibility(View.INVISIBLE);
            linearLayoutEndereco.setVisibility(View.VISIBLE);

        }else if(radioButonTelefone.isChecked()){

            linearLayoutTelefone.setVisibility(View.VISIBLE);
            linearLayoutEndereco.setVisibility(View.INVISIBLE);


        }else{

            linearLayoutTelefone.setVisibility(View.VISIBLE);
            linearLayoutEndereco.setVisibility(View.VISIBLE);

            /*if(radioButonTelefone.isChecked()) {
                if (editTextData.getText().length() > 0){
                    //editTextData.setText("");
                }
                if (editTextEndereco.getText().length() > 0){
                    //editTextEndereco.setText("");
                }
                if (editTextHora.getText().length() > 0){
                    //editTextHora.setText("");
                }



                linearLayoutEndereco.setVisibility(View.INVISIBLE);
                linearLayoutTelefone.setVisibility(View.VISIBLE);
            }else{
                if(editTextTelefone.getText().length()>0){
                    //editTextTelefone.setText("");
                }
                linearLayoutTelefone.setVisibility(View.INVISIBLE);
                linearLayoutEndereco.setVisibility(View.INVISIBLE);
            }*/
        }

    }

    private boolean validarSalvar(){
        if(!radioButonEndereco.isChecked() && !radioButonTelefone.isChecked() && !radioButonAmbos.isChecked()){
            Toast.makeText(NotificacaoMandado.this, "Escolha o tipo da notificação!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(radioButonTelefone.isChecked()){
            if(editTextTelefone.getText().toString().equals("")){
                Toast.makeText(NotificacaoMandado.this, "Informe o telefone!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(radioButonEndereco.isChecked()){
            if(editTextEndereco.getText().toString().equals("")){
                Toast.makeText(NotificacaoMandado.this, "Informe o endereço!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if(radioButonAmbos.isChecked()){
            if(editTextTelefone.getText().toString().equals("")){
                Toast.makeText(NotificacaoMandado.this, "Informe o telefone!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(editTextEndereco.getText().toString().equals("")){
                Toast.makeText(NotificacaoMandado.this, "Informe o endereço!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }


        if(editTextData.getText().toString().equals("")){
            Toast.makeText(NotificacaoMandado.this, "Informe a data!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(editTextHora.getText().toString().equals("")){
            Toast.makeText(NotificacaoMandado.this, "Informe a hora!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSalvarNotificacao:
                try{
                    Funcoes func = new Funcoes();
                    if(func.checkConexao(NotificacaoMandado.this)){
                        if(this.validarSalvar()){

                            String NOT_ID;
                            if(notificacaoParaAlteracao.getNOT_ID()==null){
                                NOT_ID = "";
                            }else{
                                NOT_ID = notificacaoParaAlteracao.getNOT_ID().toString();
                            }


                            String NOT_Telefone = editTextTelefone.getText().toString();
                            String NOT_Endereco = editTextEndereco.getText().toString();
                            String NOT_DataEncontro = editTextData.getText().toString();
                            String NOT_HoraEncontro = editTextHora.getText().toString();
                            String USU_Cadastro_ID = USU_ID.toString();
                            String MAN_ID = mandadoEscolhido.getMAN_ID().toString();
                            String USU_Alteracao_ID = USU_ID.toString();

                            //NOT_ID editTextTelefone editTextEndereco editTextData editTextHora
                            // USU_Cadastro_ID MAN_ID USU_Alteracao_ID
                            this.salvarDados(NOT_ID, NOT_Telefone, NOT_Endereco, NOT_DataEncontro, NOT_HoraEncontro, USU_Cadastro_ID, MAN_ID, USU_Alteracao_ID);
                        }
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



    private void salvarDados(final String NOT_ID, final String NOT_Telefone, final String NOT_Endereco, final String NOT_DataEncontro, final String NOT_HoraEncontro, final String USU_Cadastro_ID, final String MAN_ID, final String USU_Alteracao_ID){
        progressDialog = ProgressDialog.show(NotificacaoMandado.this, "", "Salvando notificação...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        try{

            JSONObject objEnviar = new JSONObject();
            objEnviar.put("NOT_ID", NOT_ID);
            if(NOT_Telefone.isEmpty()){
                objEnviar.put("NOT_Telefone", "");
            }else{
                objEnviar.put("NOT_Telefone", NOT_Telefone);
            }
            objEnviar.put("NOT_Endereco", NOT_Endereco);
            objEnviar.put("NOT_DataEncontro", NOT_DataEncontro);
            objEnviar.put("NOT_HoraEncontro", NOT_HoraEncontro);
            objEnviar.put("USU_Cadastro_ID", USU_Cadastro_ID);
            objEnviar.put("MAN_ID", MAN_ID);
            if(NOT_ID.isEmpty()){
                objEnviar.put("USU_Alteracao_ID", null);
            }else{
                objEnviar.put("USU_Alteracao_ID", USU_Alteracao_ID);
            }


            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            JSONObject jsonEnviar = new JSONObject();
            jsonEnviar.put("mensagem","registrarNotificacaoMandadoAndroid");
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

                                Toast.makeText(NotificacaoMandado.this, msgm, Toast.LENGTH_SHORT).show();
                                if(sucesso.equals("true")){

                                    /*SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("USU_Telefone", USU_Telefone);
                                    editor.putString("USU_Email", USU_Email);
                                    editor.putString("USU_Transmissao", USU_Transmissao);
                                    editor.putString("USU_EnderecoContato", USU_EnderecoContato);
                                    editor.apply();*/

                                    //Toast.makeText(AlterarDadosOficialActivity.this, "Dados alterados com sucesso.", Toast.LENGTH_SHORT).show();

                                    Intent intentDeRetorno = new Intent();
                                    intentDeRetorno.putExtra("retorno", 1);
                                    int resultCode = RESULT_OK;
                                    setResult (resultCode, intentDeRetorno);

                                    finish();
                                }
                            }catch (Exception e){
                                progressDialog.dismiss();
                                //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                                Toast.makeText(NotificacaoMandado.this, "Erro converssao dados tela notificacao: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(NotificacaoMandado.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(NotificacaoMandado.this, "Erro ao salvar a notificacao: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }



    private void prencherCamposEdicao(){


        editTextEndereco.setText(this.notificacaoParaAlteracao.getNOT_Endereco());
        editTextTelefone.setText(this.notificacaoParaAlteracao.getNOT_Telefone());

        editTextData.setText(this.notificacaoParaAlteracao.getNOT_DataEncontro());
        editTextHora.setText(this.notificacaoParaAlteracao.getNOT_HoraEncontro());

        if ((this.notificacaoParaAlteracao.getNOT_Telefone().length()>0) && (this.notificacaoParaAlteracao.getNOT_Endereco().length()>0)){

            radioButonEndereco.setChecked(false);
            radioButonTelefone.setChecked(false);
            radioButonAmbos.setChecked(true);

            linearLayoutTelefone.setVisibility(View.VISIBLE);
            linearLayoutEndereco.setVisibility(View.VISIBLE);

            //preenche ambos
        }else{
            if(this.notificacaoParaAlteracao.getNOT_Telefone().length()>0){

                radioButonEndereco.setChecked(false);
                radioButonTelefone.setChecked(true);
                radioButonAmbos.setChecked(false);

                linearLayoutTelefone.setVisibility(View.VISIBLE);
                linearLayoutEndereco.setVisibility(View.INVISIBLE);


              //so telefone
            }else if(this.notificacaoParaAlteracao.getNOT_Endereco().length()>0){
                //so endereco

                radioButonEndereco.setChecked(true);
                radioButonTelefone.setChecked(false);
                radioButonAmbos.setChecked(false);
                linearLayoutTelefone.setVisibility(View.INVISIBLE);
                linearLayoutEndereco.setVisibility(View.VISIBLE);

            }
        }




    }
}

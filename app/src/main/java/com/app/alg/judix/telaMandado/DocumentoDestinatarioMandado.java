package com.app.alg.judix.telaMandado;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.app.alg.judix.model.AssinaturaMandado;
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.telaAssinatura.Tela;
import com.app.alg.judix.telaAssinaturasMandado.ListaAssinaturasMandadoActivity;
import com.app.alg.judix.util.Funcoes;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DocumentoDestinatarioMandado extends AppCompatActivity implements View.OnClickListener {

    Mandado mandadoEscolhido;
    CheckBox recusoAssinar;

    EditText editTextNumeroDocumento;
    TextView textViewTituloDocumentoDestinatario;
    EditText editTextNomeDestinatario;

    Spinner spinnerTipoDocumento;

    /*RadioButton  radioButtonRg;
    RadioButton  radioButtonCpf;
    RadioButton  radioButtonPasport;*/


    Button buttonSalvarDocumentoDestinatario;
    String tipoDocEscolhido;

    ///para o spin
    private List<String> listaNomesSpin = new ArrayList<String>();
    String nomeTipoDocumento = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documento_destinatario_mandado);

        setTitle("Ass. do Destinatário");

        //identifica o mandado
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref.getString("objectMandado", "");
        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);

        //String mandado = "Doc. dest. do Mandado: "+mandadoEscolhido.getMAN_Numero_Processo();
        String mandado = "Doc. Receptor";

        tipoDocEscolhido = mandadoEscolhido.getMAN_DestinatarioTipoDoc();

        listaNomesSpin.add("Selecionar");
        listaNomesSpin.add("CNPJ");
        listaNomesSpin.add("Matrícula");
        listaNomesSpin.add("OAB");
        listaNomesSpin.add("Registro Carcerário");
        listaNomesSpin.add("RG");
        listaNomesSpin.add("CPF");
        listaNomesSpin.add("Passaport");

        //Identifica o Spinner no layout
        spinnerTipoDocumento = (Spinner) findViewById(R.id.spinnerTipoDocumento);
        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listaNomesSpin);

        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoDocumento.setAdapter(spinnerArrayAdapter);


        //Método do Spinner para capturar o item selecionado
        spinnerTipoDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });






        /*radioButtonRg = (RadioButton) findViewById(R.id.radioButtonRg);
        radioButtonCpf = (RadioButton) findViewById(R.id.radioButtonCpf);
        radioButtonPasport = (RadioButton) findViewById(R.id.radioButtonPasport);*/
        editTextNumeroDocumento = (EditText) findViewById(R.id.editTextNumeroDocumento);
        textViewTituloDocumentoDestinatario = (TextView) findViewById(R.id.textViewTituloDocumentoDestinatario);


        recusoAssinar = (CheckBox) findViewById(R.id.checkBoxRecAssDocDest);
        recusoAssinar.setOnClickListener(this);

        editTextNomeDestinatario = (EditText) findViewById(R.id.editTextNomeDestinatario);
        editTextNomeDestinatario.setText(mandadoEscolhido.getMAN_OutraParte());

        textViewTituloDocumentoDestinatario.setText("ID: "+this.mandadoEscolhido.getMAN_ID().toString()+"\nProc.:"+this.mandadoEscolhido.getMAN_Numero_Processo().toString());

        String dosumentoDestinatario = this.mandadoEscolhido.getMAN_DestinatarioDoc().toString();

        if(!dosumentoDestinatario.equals("null")){
            if(!dosumentoDestinatario.equals("NULL")){
                if(!dosumentoDestinatario.equals("")){
                   if(dosumentoDestinatario.length() > 0){
                       if(!dosumentoDestinatario.isEmpty()){
                           editTextNumeroDocumento.setText(dosumentoDestinatario);
                       }
                   }

                }
            }
        }


        buttonSalvarDocumentoDestinatario = (Button) findViewById(R.id.buttonSalvarDocumentoDestinatario);
        buttonSalvarDocumentoDestinatario.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        if(recusoAssinar.isChecked()){
            buttonSalvarDocumentoDestinatario.setText("Salvar");
            //Toast.makeText(DadosAssinaturaMandado.this, "oiiiii", Toast.LENGTH_SHORT).show();
        }else{
            buttonSalvarDocumentoDestinatario.setText("Assinar");
        }

        switch (v.getId()) {
            case R.id.buttonSalvarDocumentoDestinatario:
                try{
                    if(recusoAssinar.isChecked()){
                        if(validarSalvar()){
                            final String numDoc = this.editTextNumeroDocumento.getText().toString();
                            final String tipoDocFim = this.spinnerTipoDocumento.getSelectedItem().toString();
                            final String outraParte = this.editTextNomeDestinatario.getText().toString();

                            this.execSalvarDocumentacao(numDoc, tipoDocFim, outraParte);
                        }


                    }else{


                        Funcoes func = new Funcoes();

                        if(func.checkConexao(DocumentoDestinatarioMandado.this)){

                            if(this.validarSalvar()){


                                SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("nomeFuncaoWebservice", "registrarAssinaturaMandadoAndroid");
                                editor.putString("nomeCampoWebservice", "MAN_Imagem_Receptor");
                                editor.putString("idCampoWebservice", "MAN_ID");
                                editor.apply();


                                Intent intent = new Intent(DocumentoDestinatarioMandado.this, Tela.class);
                                intent.putExtra("telaEnviada", "DocumentoDestinatarioMandado");
                                startActivityForResult(intent, 1);

                            }
                            //this.login(cpf.getText().toString(), senha.getText().toString());
                        }else{
                            Toast toast = Toast.makeText(this,"Confira sua conexão com a internet", Toast.LENGTH_LONG);
                            toast.show();
                        }

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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){

            final String numDoc = this.editTextNumeroDocumento.getText().toString();
            final String tipoDocFim = this.spinnerTipoDocumento.getSelectedItem().toString();
            final String outraParte = this.editTextNomeDestinatario.getText().toString();

            this.execSalvarDocumentacao(numDoc, tipoDocFim, outraParte);
        }


        /*switch (requestCode) {
            case 1:

            break;
        }*/
        super.onActivityResult(requestCode, resultCode, data);

    }






    private boolean validarSalvar() throws Exception {
        boolean retorno = true;

        /*if(!this.radioButtonRg.isChecked()){
            if(!this.radioButtonCpf.isChecked()){
                if(!this.radioButtonPasport.isChecked()){
                    throw new Exception("É necessário escolher ao menos um tipo de documento");
                }
            }
        }*/
        if(this.editTextNomeDestinatario.length()==0 ){
            throw new Exception("É necessário informar o Destinatário");
        }

        if(this.editTextNumeroDocumento.length()==0 ){
            throw new Exception("É necessário informar o Número do Documento");
        }


        return retorno;
    }

    private void execSalvarDocumentacao(final String numDoc, final String tipoDoc, final String outraParte){

        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();



        final ProgressDialog progressDialog = ProgressDialog.show(DocumentoDestinatarioMandado.this, "", "Salvando...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);


        String URL = "https://www.judix.com.br/modulos/ws/index.php";
        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados

        try {

            objEnviar.put("MAN_OutraParte", outraParte.toString());
            objEnviar.put("MAN_DestinatarioDoc", numDoc.toString());
            objEnviar.put("MAN_DestinatarioTipoDoc", tipoDoc.toString());
            objEnviar.put("MAN_ID", mandadoEscolhido.getMAN_ID().toString());



            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);


            jsonEnviar.put("mensagem","registrarDocumentacaoDestinatario");
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

                                //Toast.makeText(DocumentoDestinatarioMandado.this, msgm, Toast.LENGTH_SHORT).show();




                                SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                //recupera o mandado
                                Gson gsonMandados = new Gson();
                                String jsonMandado = sharedPreferences.getString("objectMandado", "");
                                Mandado mandado = new Mandado();
                                mandado = gsonMandados.fromJson(jsonMandado, Mandado.class);


                                mandado.setMAN_OutraParte(outraParte.toString());
                                mandado.setMAN_DestinatarioDoc(numDoc.toString());
                                mandado.setMAN_DestinatarioTipoDoc(tipoDoc.toString());

                                //repassa o objeto pra sessao
                                Gson gson = new Gson();
                                String gsonMandado = gson.toJson(mandado);
                                editor.putString("objectMandado", String.valueOf(gsonMandado));
                                editor.apply();


                                AssinaturaMandado novaAssinatura = new AssinaturaMandado();

                                novaAssinatura.setNome(outraParte.toString());
                                novaAssinatura.setTipoDocumento(tipoDoc.toString());
                                novaAssinatura.setAssinatura("");
                                novaAssinatura.setAgente("Destinatário");
                                novaAssinatura.setNumeroDocumento(numDoc.toString());

                                //ListaAssinaturasMandadoActivity.addAssinatura(novaAssinatura);
                                Intent resultIntent = new Intent(DocumentoDestinatarioMandado.this, ListaAssinaturasMandadoActivity.class);
                                resultIntent.putExtra("OBJETO_ASSINATURA", novaAssinatura);
                                setResult(Activity.RESULT_OK, resultIntent);


                                //ListaAssinaturasMandadoActivity.activityListaAssinaturasMandadoActivity.finish();

                                finish();





                            }else{
                                Toast.makeText(DocumentoDestinatarioMandado.this, msgm, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(DocumentoDestinatarioMandado.this, "Erro converssao dados Documento Destinatario Mandado: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(DocumentoDestinatarioMandado.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

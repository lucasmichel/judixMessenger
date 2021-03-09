package com.app.alg.judix.telaAssinaturasMandado;

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
import android.widget.RadioButton;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DadosAssinaturaMandadoPartesRelacionadas extends AppCompatActivity implements View.OnClickListener {

    public static Activity activityDadosAssinaturaMandado;

    Mandado mandadoEscolhido;
    CheckBox recusoAssinar;
    EditText editTextNumeroDocumentoAssinatura;
    EditText editTextNomeDadoAssinatura;
    TextView textViewTituloDocumentoDestinatario;
    RadioButton  radioButtonRg;
    RadioButton  radioButtonCpf;
    RadioButton  radioButtonPasport;
    Button buttonAdicionarAssinatura;
    String tipoDocEscolhido;

    Spinner spinnerAgentes;
    Spinner spinnerTipoDocumento;
    String USU_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityDadosAssinaturaMandado = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_assinatura_do_mandado);

        setTitle("Ass. da Parte Relacionada");

        /// AGENTE Policial, Conselho Tutelar, Testemunha, Responsável pela Obra, Parte / Réu, Parte / Autor
        //spinnerAgentes

        List<String> listAgentes = new ArrayList<String>();
        String nomeAgente = "";
        listAgentes.add("Selecionar");
        listAgentes.add("Policial");
        listAgentes.add("Receptor");
        listAgentes.add("Responsável pela Obra");
        listAgentes.add("Testemunha");
        listAgentes.add("Fiel Depositário");
        listAgentes.add("Outro");

        //Identifica o Spinner no layout
        spinnerAgentes = (Spinner) findViewById(R.id.spinnerAgentes);
        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapterAgente = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listAgentes);

        ArrayAdapter<String> spinnerArrayAdapterAgente = arrayAdapterAgente;
        spinnerArrayAdapterAgente.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgentes.setAdapter(spinnerArrayAdapterAgente);

        //Método do Spinner para capturar o item selecionado
        spinnerAgentes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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

        /// TIPO DOC OAB, Agente Carcerário, RG, CPF, Passaporte
        List<String> listTipoDoc = new ArrayList<String>();

        listTipoDoc.add("Selecionar");
        listTipoDoc.add("CNPJ");
        listTipoDoc.add("Matrícula");
        listTipoDoc.add("OAB");
        listTipoDoc.add("Registro Carcerário");
        listTipoDoc.add("RG");
        listTipoDoc.add("CPF");
        listTipoDoc.add("Passaport");


        String nomeTipoDoc = "";

        //Identifica o Spinner no layout
        spinnerTipoDocumento = (Spinner) findViewById(R.id.spinnerTipoDocumento);
        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapterTipoDoc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listTipoDoc);

        ArrayAdapter<String> spinnerArrayAdapterTipoDoc = arrayAdapterTipoDoc;
        spinnerArrayAdapterTipoDoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoDocumento.setAdapter(spinnerArrayAdapterTipoDoc);


        spinnerTipoDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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


        //identifica o mandado
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref.getString("objectMandado", "");
        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);
        USU_ID = pref.getString("USU_ID", "");

        /*
        //String mandado = "Doc. dest. do Mandado: "+mandadoEscolhido.getMAN_Numero_Processo();
        String mandado = "Doc. Receptor";

        tipoDocEscolhido = mandadoEscolhido.getMAN_DestinatarioTipoDoc();



        //continhuar aqui
        setTitle(mandado.toString());

        radioButtonRg = (RadioButton) findViewById(R.id.radioButtonRg);
        radioButtonCpf = (RadioButton) findViewById(R.id.radioButtonCpf);
        radioButtonPasport = (RadioButton) findViewById(R.id.radioButtonPasport);
        editTextNumeroDocumento = (EditText) findViewById(R.id.editTextNumeroDocumento);
        textViewTituloDocumentoDestinatario = (TextView) findViewById(R.id.textViewTituloDocumentoDestinatario);


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
        */


        recusoAssinar = (CheckBox) findViewById(R.id.checkBoxRecAssMan);
        recusoAssinar.setOnClickListener(this);


        buttonAdicionarAssinatura = (Button) findViewById(R.id.buttonAdicionarAssinatura);
        buttonAdicionarAssinatura.setOnClickListener(this);


        editTextNomeDadoAssinatura = (EditText) findViewById(R.id.editTextNomeDadoAssinatura);
        editTextNumeroDocumentoAssinatura = (EditText) findViewById(R.id.editTextNumeroDocumentoAssinatura);
        //this.marcaRadioButon();

    }


    private void marcaRadioButon(){
        if(tipoDocEscolhido.equals("RG")){
            radioButtonRg.setChecked(true);
            radioButtonCpf.setChecked(false);
            radioButtonPasport.setChecked(false);



        }else if(tipoDocEscolhido.equals("CPF")) {
            radioButtonRg.setChecked(false);
            radioButtonCpf.setChecked(true);
            radioButtonPasport.setChecked(false);

        }
        else if(tipoDocEscolhido.equals("PASSPORT")){
            radioButtonRg.setChecked(false);
            radioButtonCpf.setChecked(false);
            radioButtonPasport.setChecked(true);
        }else{
            radioButtonRg.setChecked(false);
            radioButtonCpf.setChecked(false);
            radioButtonPasport.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {

        if(recusoAssinar.isChecked()){
            buttonAdicionarAssinatura.setText("Salvar");
            //Toast.makeText(DadosAssinaturaMandado.this, "oiiiii", Toast.LENGTH_SHORT).show();
        }else{
            buttonAdicionarAssinatura.setText("Assinar");
        }



        switch (v.getId()) {
            case R.id.buttonAdicionarAssinatura:



                if(recusoAssinar.isChecked()){

                    try {
                        if(this.validarSalvar() ){


                            final AssinaturaMandado novaAssinatura = new AssinaturaMandado();

                            novaAssinatura.setNome(editTextNomeDadoAssinatura.getText().toString());
                            novaAssinatura.setTipoDocumento(spinnerTipoDocumento.getSelectedItem().toString());
                            novaAssinatura.setAssinatura("");
                            novaAssinatura.setAgente(spinnerAgentes.getSelectedItem().toString());
                            novaAssinatura.setNumeroDocumento(editTextNumeroDocumentoAssinatura.getText().toString());

                            this.execSalvarAssinaturasMandado(novaAssinatura, mandadoEscolhido);

                        }
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }


                }else{


                    try {
                        if(this.validarSalvar() ){
                            try{

                                //passa os dados da tela pra sessao pra ja mandar salvar pela tela de assinatura


                                /*SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("ASM_Agente", spinnerAgentes.getSelectedItem().toString());
                                editor.putString("ASM_TipoDocumento", spinnerTipoDocumento.getSelectedItem().toString());
                                editor.putString("ASM_NumeroDocumento", editTextNumeroDocumentoAssinatura.getText().toString());
                                editor.putString("MAN_ID", mandadoEscolhido.getMAN_ID());
                                editor.putString("USUARIO_ID", USU_ID.toString());
                                editor.putString("ASM_Nome", editTextNomeDadoAssinatura.getText().toString());


                                editor.apply();*/



                                Intent intent = new Intent(DadosAssinaturaMandadoPartesRelacionadas.this, Tela.class);
                                intent.putExtra("telaEnviada", "DadosAssinaturaMandado");
                                startActivityForResult(intent, 1);
                            }catch (Exception e){
                                Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }


                }



                break;
            default:
                break;
        }
    }

    private boolean validarSalvar() throws Exception {

        boolean retorno = true;

        String agente = spinnerAgentes.getSelectedItem().toString();
        if(agente.equals("Selecionar") ) {
            throw new Exception("É necessário informar a Parte Relacionada.");
        }
        if(editTextNomeDadoAssinatura.getText().length() == 0 ) {
            throw new Exception("É necessário informar Nome.");
        }

        String tipoDocumento = spinnerTipoDocumento.getSelectedItem().toString();
        if(tipoDocumento.equals("Selecionar")){
            throw new Exception("É necessário informar o Tipo do Documento.");
        }

        if(this.editTextNomeDadoAssinatura.length()==0 ){
            throw new Exception("É necessário informar o Número do Documento");
        }

        /*if(!this.radioButtonRg.isChecked()){
            if(!this.radioButtonCpf.isChecked()){
                if(!this.radioButtonPasport.isChecked()){
                    throw new Exception("É necessário escolher ao menos um tipo de documento");
                }
            }
        }*/

        /*if(this.editTextNomeDadoAssinatura.length()==0 ){
            throw new Exception("É necessário informar o numero do documento");
        }*/

        return retorno;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            String assinatura = data.getExtras().getString("assianturaCapturada");
            String assinaturab = assinatura;

            final AssinaturaMandado novaAssinatura = new AssinaturaMandado();

            String agente = spinnerAgentes.getSelectedItem().toString();
            String tipoDoc = spinnerTipoDocumento.getSelectedItem().toString();

            editTextNomeDadoAssinatura = (EditText) findViewById(R.id.editTextNomeDadoAssinatura);
            editTextNumeroDocumentoAssinatura = (EditText) findViewById(R.id.editTextNumeroDocumentoAssinatura);

            novaAssinatura.setNome(editTextNomeDadoAssinatura.getText().toString());
            novaAssinatura.setTipoDocumento(tipoDoc.toString());
            novaAssinatura.setAssinatura(assinatura);
            novaAssinatura.setAgente(agente.toString());
            novaAssinatura.setNumeroDocumento(editTextNumeroDocumentoAssinatura.getText().toString());

            this.execSalvarAssinaturasMandado(novaAssinatura, mandadoEscolhido);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }




    private void execSalvarAssinaturasMandado(final AssinaturaMandado novaAssinatura, Mandado manEscolhido){

        final JSONObject jsonEnviar = new JSONObject();
        final JSONObject objEnviar = new JSONObject();

        final String MAN_ID = manEscolhido.getMAN_ID();

        final ProgressDialog progressDialog = ProgressDialog.show(DadosAssinaturaMandadoPartesRelacionadas.this, "", "Gravando a assinatura, aguarde...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        //final int MAN_ID, final int CEM_ID, final String MAN_CertidaoTexto


        String URL = "https://www.judix.com.br/modulos/ws/index.php";
        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados

        try {



            JSONArray arrayDados = new JSONArray();



            JSONObject objEnviarAssinatura = new JSONObject();

            objEnviarAssinatura.put("ASM_Assintura", novaAssinatura.getAssinatura().toString());
            objEnviarAssinatura.put("ASM_Agente", novaAssinatura.getAgente().toString());
            objEnviarAssinatura.put("ASM_Nome", novaAssinatura.getNome().toString());
            objEnviarAssinatura.put("ASM_TipoDocumento", novaAssinatura.getTipoDocumento().toString());
            objEnviarAssinatura.put("ASM_NumeroDocumento", novaAssinatura.getNumeroDocumento().toString());
            objEnviarAssinatura.put("MAN_ID", MAN_ID);
            objEnviarAssinatura.put("USUARIO_ID", USU_ID.toString() );

            arrayDados.put(0, objEnviarAssinatura);

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

                                //finish();

                                //Toast.makeText(DadosAssinaturaMandado.this, msgm, Toast.LENGTH_SHORT).show();


                                //ListaAssinaturasMandadoActivity.addAssinatura(novaAssinatura);
                                Intent resultIntent = new Intent(DadosAssinaturaMandadoPartesRelacionadas.this, ListaAssinaturasMandadoActivity.class);
                                //resultIntent.putExtra("OBJETO_ASSINATURA", novaAssinatura);
                                setResult(Activity.RESULT_OK, resultIntent);

                                finish();
                            }else{
                                Toast.makeText(DadosAssinaturaMandadoPartesRelacionadas.this, msgm, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(DadosAssinaturaMandadoPartesRelacionadas.this, "Erro converssao dados tela de dados assinatura mandado: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(DadosAssinaturaMandadoPartesRelacionadas.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
        //queue.start();*/
    }






}




package com.app.alg.judix.telaCertidoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.alg.judix.R;

public class dialog_salvar_alteracao_certidao extends AppCompatActivity implements View.OnClickListener {

    EditText textCampo;
    Button buttonAlterar;
    TextView editTextTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_salvar_alteracao_certidao);


        textCampo = (EditText) findViewById(R.id.editTextTextoAlteracoCertidao);



        Bundle extras = getIntent().getExtras();
        String texto = extras.getString("textoCertidaoEscolhida");

        textCampo.setText(texto);


        buttonAlterar = (Button) findViewById(R.id.buttonSalvarAlteracaoCertidao);
        buttonAlterar.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonSalvarAlteracaoCertidao:

                /*SharedPreferences sharedPreferences = getSharedPreferences("PreferenciaJudix", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("textoAlteracaoCertidao", textCampo.getText().toString());*/

                Intent it = new Intent();
                it.putExtra("textoAlteracaoCertidao", textCampo.getText().toString());
                setResult(1, it);


                finish();
                break;

            default:
                break;
        }

    }
}

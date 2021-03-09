package com.app.alg.judix.telaOficial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.alg.judix.R;
import com.app.alg.judix.util.Funcoes;

public class ListaRelatorios extends AppCompatActivity implements View.OnClickListener {

    private Button btnProdutividade, btnPeriodo;
    ProgressDialog prDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Relatórios Disponíveis");
        setContentView(R.layout.activity_lista_relatorios);

        btnProdutividade = (Button) findViewById(R.id.relatorioProdutividade);
        btnPeriodo = (Button) findViewById(R.id.relatorioMandadoPorPeriodo);

        btnProdutividade.setOnClickListener(this);
        btnPeriodo.setOnClickListener(this);

        /*
        *
        * try{
                Intent intent = new Intent(MenuActivity.this, RelatorioProdutividadeActivity.class);
                startActivity(intent);
            }catch (Exception e){
                Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        *
        *
        * */

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relatorioProdutividade:
                try{
                    this.openRelatorioProdutividade();

                }catch (Exception e){
                    //Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                    //toast.show();
                }

                break;

            case R.id.relatorioMandadoPorPeriodo:
                try{
                    this.openRelatorioMandadoPorPeriodo();
                }catch (Exception e){
                    //Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                    //toast.show();
                }

            default:
                break;
        }
    }

    private void openRelatorioProdutividade(){
        try{

            prDialog = new ProgressDialog(ListaRelatorios .this);
            prDialog.setMessage("Aguarde, carregando...");
            prDialog.show();

            Intent intent = new Intent(ListaRelatorios.this, RelatorioProdutividadeActivity.class);
            startActivity(intent);
        }catch (Exception e){
            Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        if(prDialog!=null){
            prDialog.dismiss();
        }

    }
    private void openRelatorioMandadoPorPeriodo(){
        //
        try{


            prDialog = new ProgressDialog(ListaRelatorios .this);
            prDialog.setMessage("Aguarde, carregando...");
            prDialog.show();


            Intent intent = new Intent(ListaRelatorios.this, ReltorioMandadoPorPeriodo.class);
            startActivity(intent);
        }catch (Exception e){
            Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        if(prDialog!=null){
            prDialog.dismiss();
        }

    }




}

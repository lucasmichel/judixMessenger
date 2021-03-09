package com.app.alg.judix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alg.judix.telaMandado.ListaMandadoActivity;
import com.app.alg.judix.telaNotificacaoMandado.ListaTodasNotificacoes;
import com.app.alg.judix.telaOficial.ListaRelatorios;
import com.app.alg.judix.telaOficial.OficialActivity;
import com.app.alg.judix.telaOficial.RelatorioProdutividadeActivity;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);//para os icones coloridos
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        /*btnConfiguracao = (Button) findViewById(R.id.btnConfiguracao);
        btnConfiguracao.setOnClickListener(MenuActivity.this);

        btnMandado = (Button) findViewById(R.id.btnMandado);
        btnMandado.setOnClickListener(MenuActivity.this);*/


        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);



        // recupera (ou cria) uma instância do arquivo de preferencia do Android,
        // pelo seu nome/chave (no caso "pref")
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);

        // recupera a propriedade com a chave 'a' e do tipo 'String',
        // passando um valor default como segundo parâmetro,
        // no caso de não encontrar um propriedade com essa chave para recuperar
        String nomeUsuario = "Oficial: "+pref.getString("USU_Nome", null);
        String ultimoAcessoUsuario = "Acesso: "+ pref.getString("USU_DataHoraUltimoAcesso", null);

        // recupera a propriedade com a chave 'b' e do tipo 'int',
        // passando um valor default como segundo parâmetro,
        // no caso de não encontrar um propriedade com essa chave para recuperar
        //int b = pref.getInt("b", 0);

        // recupera a propriedade com a chave 'c' e do tipo 'boolean',
        // passando um valor default como segundo parâmetro, no caso de não encontrar um
        // propriedade com essa chave para recuperar
        //boolean c = pref.getBoolean("c", true);

        TextView textViewTituloMenu = (TextView) findViewById(R.id.textViewTituloMenu);
        textViewTituloMenu.setText("Principal");


        TextView textNomeUsuario = (TextView) findViewById(R.id.textViewNomeUsuario);
        textNomeUsuario.setText(nomeUsuario.toString());

        TextView textUltimoAcesso = (TextView) findViewById(R.id.textUltimioAcesso);
        textUltimoAcesso.setText(ultimoAcessoUsuario.toString());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.exibirMandados) {

            try{

                SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("exibirMandado", "todos");
                editor.apply();


                Intent intent = new Intent(MenuActivity.this, ListaMandadoActivity.class);
                startActivity(intent);
            }catch (Exception e){
                Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }


        if (id == R.id.exibirNotificacoes) {

            try{

                /*SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("exibirMandado", "todos");
                editor.apply();*/

                Intent intent = new Intent(MenuActivity.this, ListaTodasNotificacoes.class);
                startActivity(intent);
            }catch (Exception e){
                Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }



        else if (id == R.id.exibirMandadoPendente) {

            try{
                SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("exibirMandado", "pendente");
                editor.apply();

                Intent intent = new Intent(MenuActivity.this, ListaMandadoActivity.class);
                startActivity(intent);
            }catch (Exception e){
                Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        else if (id == R.id.exibirMandadoConcluido) {

            try{
                SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("exibirMandado", "concluido");
                editor.apply();

                Intent intent = new Intent(MenuActivity.this, ListaMandadoActivity.class);
                startActivity(intent);
            }catch (Exception e){
                Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }



        else if (id == R.id.configuracaoAplicativo) {

            try{
                Intent intent = new Intent(MenuActivity.this, OficialActivity.class);
                startActivity(intent);
            }catch (Exception e){
                Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }

        }else if (id == R.id.sair) {

            finish();

        }



        else if (id == R.id.relatorios) {

            try{
                Intent intent = new Intent(MenuActivity.this, ListaRelatorios.class);
                startActivity(intent);
            }catch (Exception e){
                Toast toast = Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }

        }else if (id == R.id.sair) {

            finish();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

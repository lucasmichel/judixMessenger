package com.app.alg.judix.telaMandado;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.app.alg.judix.model.Endereco;
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.util.Funcoes;
import com.app.alg.judix.util.GPSHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

//import android.support.v4.app.FragmentActivity;

//AppCompatActivity

//FragmentActivity

public class MapaMandadoActivity extends AppCompatActivity implements OnMapReadyCallback{
    Mandado mandadoEscolhido;
    Endereco mandadoEndereco;
    ProgressDialog progressDialog;
    List<LatLng> polyz;


    GoogleMap mMap;
    private MarkerOptions mp1;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_mandado);



        //identifica o mandado
        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref.getString("objectMandado", "");
        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);


        this.buscaEnderecoMandado();//preenche mandadoEndereco

        if (this.mandadoEndereco.getEND_ID().length()> 0){
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }


    }



    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=SERVER-KEY");
        return urlString.toString();
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        final JSONObject[] locationEnderecoMandado = {new JSONObject()};
        final GoogleMap googleMap2 = googleMap;





        final ProgressDialog progressDialog = ProgressDialog.show(MapaMandadoActivity.this, "", "Carregando...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);


        //String origem = "Rua André Vidal de Negreiros, 06, Moreno  Pernambuco";
        //String origem = "Rua Adalto Barbosa, 288, Moreno  Pernambuco";

        //String URL = "https://maps.googleapis.com/maps/api/directions/json?origin="+origem+"&destination="+destino+"&key=AIzaSyAPfPXkbxkllvk2ga2Q0o4dBWrAu_RI0U0";

        //String URLl = URL;

        String URL = null;
        try {



            //URL = this.getUrlLatLong("Av. Barbosa de Lima", "149", "recife", "pe");
            URL = this.getUrlLatLong(this.mandadoEndereco.getEND_Logradouro().toString(), this.mandadoEndereco.getEND_Numero().toString(), this.mandadoEndereco.getEND_Municipio().toString(), this.mandadoEndereco.getEND_UF().toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(MapaMandadoActivity.this, "Erro " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        //String UrlCity = "http://maps.googleapis.com/maps/api/geocode/json?address=" + NameString + "&sensor=false";

        JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject location;
                try {
                    // Get JSON Array called "results" and then get the 0th
                    // complete object as JSON
                    locationEnderecoMandado[0] = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    setarMapa(locationEnderecoMandado, googleMap2);
                    progressDialog.dismiss();

                    // Get the value of the attribute whose name is
                    // "formatted_string"
                    //stateLocation = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                    // System.out.println(stateLocation.toString());
                } catch (JSONException e1) {
                    progressDialog.dismiss();
                    Toast.makeText(MapaMandadoActivity.this, "Erro1 " + e1.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MapaMandadoActivity.this, "Erro2 " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stateReq);
        queue.start();






















    }

    private void setarMapa(JSONObject[] enderecoMandado, GoogleMap googleMap){


        //DirectionsService
        GPSHelper gps = new GPSHelper(this);
        gps.getMyLocation();


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setTrafficEnabled(true);


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng( -34, 151);
        double la = 0;
        double lo = 0;
        try {
            la = (double) enderecoMandado[0].get("lat");
            lo = (double) enderecoMandado[0].get("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng posicaoAtual = new LatLng(gps.getLatitude(), gps.getLongitude());

        mMap.addMarker(new MarkerOptions().position(posicaoAtual).title("Local atual"));


        LatLng posicaoNova = new LatLng( la, lo);
        mMap.addMarker(new MarkerOptions().position(posicaoNova).title("Local destino"));

        /*ATENÇÂO SE PRECISAR DE MAIS INFORMAÇÔE SOBRE A ROTA OBSEVAR O LINK DE RETORNO DESTA FUNCAO*/
        String NovaUrl = this.criaURL( gps.getLatitude(), gps.getLongitude(), la, lo);


        this.imprimirCaminhoEntrePontos(NovaUrl, googleMap, posicaoAtual);

        /*Polyline line = mMap.addPolyline(new PolylineOptions().add(
                posicaoAtual, posicaoNova)
                .width(2)
                .color(Color.BLUE));*/





    }


    public String getUrlLatLong ( String logaradouro, String numero, String cidade, String uf ) throws UnsupportedEncodingException {

        //$strEndereco = trim($arrStrDadosEnd[0]["CONF_EnderecoLogradouro"]).", ".trim($arrStrDadosEnd[0]["CONF_EnderecoNumero"]).", ".trim($arrStrDadosEnd[0]["CONF_EnderecoCidade"])." ".trim($arrStrDadosEnd[0]["CONF_EnderecoUf"]);
        //http://maps.googleapis.com/maps/api/geocode/json?address=".str_replace(" ","+", urlencode($strEndereco))."&sensor=false

        String endereco = logaradouro+", "+numero+", "+cidade+" "+uf;
        /*StringBuilder enderecoString = new StringBuilder ( ) ;
        enderecoString.append(logaradouro.toString());
        enderecoString.append(",");
        enderecoString.append(numero.toString());
        enderecoString.append(",");
        enderecoString.append(cidade.toString());
        enderecoString.append(" ");
        enderecoString.append(uf.toString());*/



        StringBuilder urlString = new StringBuilder ( ) ;
        urlString . append ( "http://maps.googleapis.com/maps/api/geocode/json" ) ;
        urlString . append("?address=") ;
        urlString . append ( URLEncoder.encode(endereco, "utf8") ) ;
        urlString . append("&sensor=false") ;// + URLEncoder.encode(input, "utf8")
        return urlString . toString ( ) ;

    }


    public String criaURL ( double sourcelat , double sourcelog , double destlat , double destlog ) {
        StringBuilder urlString = new StringBuilder ( ) ;
        urlString . append ( "https://maps.googleapis.com/maps/api/directions/json" ) ;
        urlString . append ( "?origin=" ) ; // from
        urlString . append ( Double . toString ( sourcelat ) ) ;
        urlString . append ( "," ) ;
        urlString
                . append ( Double . toString ( sourcelog ) ) ;
        urlString . append ( "&destination=" ) ; // to
        urlString
                . append ( Double . toString ( destlat ) ) ;
        urlString . append ( "," ) ;
        urlString . append ( Double . toString ( destlog ) ) ;
        urlString . append ( "&sensor=false&mode=driving&alternatives=true" ) ;
        urlString . append ( "&key=AIzaSyAPfPXkbxkllvk2ga2Q0o4dBWrAu_RI0U0" ) ;
        return urlString . toString ( ) ;
    }



    private void imprimirCaminhoEntrePontos(String URL, GoogleMap googleMap2,  LatLng posicaoAtual){
        final GoogleMap googleMap =googleMap2;

        final ProgressDialog progressDialog2 = ProgressDialog.show(MapaMandadoActivity.this, "", "Carregando...");
        progressDialog2.setCancelable(true);
        progressDialog2.setCanceledOnTouchOutside(false);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {



                try{

                    // routesArray contains ALL routes
                    JSONArray routesArray = (JSONArray) response.getJSONArray("routes");
                    // Grab the first route
                    JSONObject route = (JSONObject) routesArray.getJSONObject(0);

                    JSONObject poly = (JSONObject) route.getJSONObject("overview_polyline");
                    String polyline = poly.getString("points");
                    polyz = decodePoly(polyline);

                    for (int i = 0; i < polyz.size() - 1; i++) {
                        LatLng src = polyz.get(i);
                        LatLng dest = polyz.get(i + 1);
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(src.latitude, src.longitude),new LatLng(dest.latitude,dest.longitude))
                                .width(3).color(Color.RED).geodesic(true));
                    }

                    progressDialog2.dismiss();

                }catch (Exception e){
                    Toast.makeText(MapaMandadoActivity.this, "Erro " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

                /*try {*/











                    //pega as rotas
                    /*JSONArray routes = (JSONArray) response.getJSONArray("routes");

                    int TotalRotas = routes.length();
                    for(int totRota =0; totRota<TotalRotas; totRota++){

                        //pega a overviewPolylines
                        JSONObject overviewPolylines = (JSONObject) routes.get(totRota);






                        //pega as legs
                        JSONArray legs = (JSONArray) overviewPolylines.getJSONArray("legs");

                        int TotalLeg = legs.length();

                        for(int totLeg =0; totLeg<TotalLeg; totLeg++){

                            JSONObject dadosLeg = (JSONObject) legs.get(totLeg);

                            //pega o ponto inicial e o final de cada leg
                            JSONObject start_location = (JSONObject) dadosLeg.get("start_location");
                            JSONObject end_location = (JSONObject) dadosLeg.get("end_location");

                            double lat_start = 0;
                            double lon_start = 0;
                            double lat_finish = 0;
                            double lon_finish = 0;


                                lat_start = (double) start_location.get("lat");
                                lon_start = (double) start_location.get("lng");

                                lat_finish  = (double) end_location.get("lat");
                                lon_finish = (double) end_location.get("lng");


                                LatLng posicaoInicial = new LatLng(lat_start, lon_start);
                                LatLng posicaoFinal = new LatLng(lat_finish, lon_finish);

                                PolylineOptions options = new PolylineOptions();
                                //options.width(6);
                                options.color(Color.RED);

                                options.add(posicaoInicial);
                                options.add(posicaoFinal);

                                mMap.addPolyline(options);






                        }*/

















            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog2.dismiss();
                Toast.makeText(MapaMandadoActivity.this, "Erro2 " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stateReq);
        queue.start();









        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicaoAtual));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);

    }




    /* Method to decode polyline points */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }









    private void buscaEnderecoMandado() {


        progressDialog = ProgressDialog.show(MapaMandadoActivity.this, "", "Carregando endereço, aguarde...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.judix.com.br/modulos/ws/index.php";

        //$arrDadosRecebidos["mensagem"] = "";// vem o nome da função pra ser executada
        //$arrDadosRecebidos["status"] = "";// vem 0 ok 1 erro
        //$arrDadosRecebidos["dados"] = "";// vem os dados


        Funcoes func = new Funcoes();
        JSONObject objEnviar = new JSONObject();
        JSONObject jsonEnviar = new JSONObject();

        try {

            objEnviar.put("END_ID", this.mandadoEscolhido.getEND_ENDERECO_ID());


            JSONArray arrayDados = new JSONArray();
            arrayDados.put(0, objEnviar);

            jsonEnviar.put("mensagem","buscarEnderecoMandadoAndroid");
            jsonEnviar.put("sucesso", "true");
            jsonEnviar.put("dados", arrayDados);

        } catch (JSONException e) {
            Toast.makeText(MapaMandadoActivity.this, "Erro gerar consulta do endereço do mandado: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }



        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                jsonEnviar,
                new Response.Listener<JSONObject>() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String msgm = response.getString("mensagem");
                            String sucesso = response.getString("sucesso");
                            progressDialog.dismiss();

                            //Toast.makeText(LoginActivity.this, msgm, Toast.LENGTH_SHORT).show();

                            if(sucesso.equals("true")){

                                if(response.getJSONArray("dados").length() > 0) {
                                    JSONArray dados = response.getJSONArray("dados");

                                    JSONArray dados2 = dados;

                                    //dados.length() faz um for e preenceh os itens da exibição

                                    JSONObject itemDados = (JSONObject) dados.get(0);

                                    mandadoEndereco.setEND_ID(itemDados.getString("END_ID"));
                                    mandadoEndereco.setEND_Cep(itemDados.getString("END_Cep"));
                                    mandadoEndereco.setEND_Logradouro(itemDados.getString("END_Logradouro"));
                                    mandadoEndereco.setEND_Complemento(itemDados.getString("END_Complemento"));
                                    mandadoEndereco.setEND_Municipio(itemDados.getString("END_Municipio"));
                                    mandadoEndereco.setEND_Numero(itemDados.getString("END_Numero"));
                                    mandadoEndereco.setEND_PontoReferencia(itemDados.getString("END_PontoReferencia"));
                                    mandadoEndereco.setEND_UF(itemDados.getString("END_UF"));


                                }else {
                                    Toast.makeText(MapaMandadoActivity.this, "Nenhum dado retornado, contate o adminstrador do sistema.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(MapaMandadoActivity.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                                SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
                            }

                        }catch (Exception e){
                            //Log.d("JUDIX", "Erro converssao dados tela login: " + e.toString());
                            Toast.makeText(MapaMandadoActivity.this, "Erro converssao dados tela mapamandadoactivity: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapaMandadoActivity.this, "Erro resposta do servidor: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

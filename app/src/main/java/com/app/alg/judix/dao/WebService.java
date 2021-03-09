package com.app.alg.judix.dao;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by lucas on 19/01/16.
 */
public class WebService extends AsyncTask<String, String, JSONArray> {


    public WebService() {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public JSONArray doInBackground(String... params) {


        android.os.Debug.waitForDebugger();



        JSONArray response = new JSONArray();

        String url = params[0];












        OutputStream os = null;
        InputStream is = null;

        URL object= null;
        try {
            object = new URL(url);
            HttpURLConnection con = (HttpURLConnection) object.openConnection();

            JSONObject cred   = new JSONObject();
            cred.put("ACO_Descricao", "ChecarAcesso");
            cred.put("USU_Login", "admin");
            cred.put("USU_Senha", "123");

            JSONArray ar   = new JSONArray();
            ar.put(cred);

            String message = ar.toString();


            /*con.setReadTimeout(10000 );//milliseconds
            con.setConnectTimeout(15000 );/ milliseconds
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");
            con.setFixedLengthStreamingMode(message.getBytes().length);*/


            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setFixedLengthStreamingMode(message.getBytes().length);

            //make some HTTP header nicety
            con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            con.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //open
            con.connect();



            //setup send
            os = new BufferedOutputStream(con.getOutputStream());
            os.write(message.getBytes());
            //clean up
            os.flush();

            //do somehting with response
            is = con.getInputStream();

            /*
            //display what returns the POST request
            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                br.close();

                System.out.println(""+sb.toString());

            }else{
                System.out.println(con.getResponseMessage());
            }
            */

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //String a = this.getDadosString(url);
        //JSONObject b = this.getDadosJson(url);

        /*
        try {

            URL urlCon = new URL(params[0]);
            HttpURLConnection conexao = (HttpURLConnection) urlCon.openConnection();
            conexao.setReadTimeout(15000);
            conexao.setConnectTimeout(15000);
            conexao.setRequestMethod("POST");
            conexao.setDoInput(true);

            conexao.connect();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                String responseString = readStream(urlConnection.getInputStream());
                Log.v("CatalogClient", responseString);
                response = new JSONArray(responseString);
            }else{
                Log.v("CatalogClient", "Response code:"+ responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
        */

        return response;
    }



    private JSONObject getDadosJson(String url){

        android.os.Debug.waitForDebugger();

        JSONObject retorno = null;
        HttpURLConnection client = null;

        try {
            URL urll = new URL(url);
            client = (HttpURLConnection) urll.openConnection();
            client.setDoOutput(true);
            client.setDoInput(true);
            client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            client.setRequestMethod("POST");
            //client.setFixedLengthStreamingMode(request.toString().getBytes("UTF-8").length);
            client.connect();


            JSONObject request = new JSONObject();
            request.put("ACO_Descricao","ChecarAcesso");
            request.put("USU_Login","admin");
            request.put("USU_Senha","123");


            OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream());
            String output = request.toString();
            writer.write(output);
            writer.flush();
            writer.close();

            InputStream input = client.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d("doInBackground(Resp)", result.toString());
            retorno = new JSONObject(result.toString());
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            client.disconnect();
        }

        return retorno;
    }



    private String getDadosString(String url){
        String retorno = null;
        try {
            HttpURLConnection conexao = this.abrirConexao(url, "GET", false);

            int responseCode = conexao.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                 /*
                 * HttpURLConnection
                 * <li>1xx: Informational</li>
                 * <li>2xx: Success</li>
                 * <li>3xx: Relocation/Redirection</li>
                 * <li>4xx: Client Error</li>
                 * <li>5xx: Server Error</li>
                 */

                InputStream is = conexao.getInputStream();
                retorno = streamToString(is);
                is.close();

                /*JSONObject json = new JSONObject(s);
                int idServidor = json.getInt("id");*/
            } else {
                throw new RuntimeException("Erro ao acessar servidor pelo getDadosString");
            }
            conexao.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retorno;
    }




    private HttpURLConnection abrirConexao(String url,
                                           String metodo, boolean doOutput) throws Exception{
        URL urlCon = new URL(url);
        HttpURLConnection conexao = (HttpURLConnection) urlCon.openConnection();
        conexao.setReadTimeout(15000);
        conexao.setConnectTimeout(15000);
        conexao.setRequestMethod(metodo);
        conexao.setDoInput(true);
        conexao.setDoOutput(doOutput);
        if (doOutput) {
            conexao.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conexao.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        }
        conexao.connect();
        return conexao;
    }

    private String streamToString(InputStream is) throws IOException {
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int lidos;
        while ((lidos = is.read(bytes)) > 0) {
            baos.write(bytes, 0, lidos);
        }
        return new String(baos.toByteArray());
    }




































    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }



}


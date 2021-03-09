package com.app.alg.judix.util;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 22/01/16.
 */
public class ModeloComunicacao {
    private String messagem;
    private String status;
    private String acao;
    private List<JSONObject> dados;

    public ModeloComunicacao() {}

    public String getMessagem() { return messagem; }
    public void setMessagem(String messagem) {
        this.messagem = messagem;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public List<JSONObject> getDados() {
        return dados;
    }
    public void setDados(List<JSONObject> dados) {
        this.dados = dados;
    }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public boolean receberDados(String retornoCriptografado) throws JSONException, UnsupportedEncodingException {
        String descriptografado = this.toBase64StringDecode(retornoCriptografado);
        JSONObject objResposta = new JSONObject(descriptografado);

        JSONObject jsonObject = objResposta.getJSONObject("retorno");
        this.setStatus(jsonObject.getString("status"));
        this.setMessagem(jsonObject.getString("mensagem"));
        JSONArray dados =(JSONArray) jsonObject.getJSONArray("dados");
        List<JSONObject> listaDadoRetorno = new ArrayList<JSONObject>();
        for (int i = 0; i < dados.length(); i++) {
            JSONObject jsonItem = dados.getJSONObject(i);
            listaDadoRetorno.add(jsonItem);
        }
        this.setDados(listaDadoRetorno);
        return true;
    }

    public JSONObject gerarSaida(String msgm,  String status, String acao, JSONArray dados) throws JSONException{
        JSONObject retorno = null;

        JSONObject princial = new JSONObject();
        princial.put("mensagem", msgm);
        princial.put("status", status);
        princial.put("acao", acao);
        princial.put("dados", dados);

        /*
        if(dados != null){
            for (int i = 0; i < dados.size(); i++) {
                JSONObject jsonItem =  dados.get(i);
                listaDadoRetorno.add(jsonItem);
            }_REQUES
        }*/

        /*
        JSONArray jArrayDados = new JSONArray();
        JSONObject js2 = new JSONObject();
        js2.put("meunome", "Junior");
        js2.put("minhaCasa", "Junior");
        jArrayDados.put(js2);

        JSONObject js3 = new JSONObject();
        js3.put("buuu", "ZZZZ");
        js3.put("baaaa", "ZZZZ");
        jArrayDados.put(js3);

        princial.put("dados", jArrayDados);
        */

        JSONObject js = new JSONObject();
        js.put("dados", this.toBase64StringEncode(princial.toString()));


        return js;
    }



    private String toBase64StringDecode(String text)
            throws UnsupportedEncodingException {
        byte bytes[] = text.getBytes();
        Base64.decode(bytes, Base64.DEFAULT);
        String valor = new String(Base64.decode(bytes, Base64.DEFAULT),"ISO-8859-1");
        return valor;
    }

    private String toBase64StringEncode(String text) {
        byte bytes[] = text.getBytes();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}

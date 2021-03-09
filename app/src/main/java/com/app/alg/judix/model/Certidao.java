package com.app.alg.judix.model;

/**
 * Created by lucas on 18/01/16.
 */
public class Certidao {

    private String id;
    private String nome;
    private String cabecalho;
    private String rodape;
    private String texto;
    private String tipo;

    public Certidao() {

    }

    public Certidao(String id, String nome, String cabecalho, String rodape, String texto, String tipo) {
        this.id = id;
        this.nome = nome;
        this.cabecalho = cabecalho;
        this.rodape = rodape;
        this.texto = texto;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCabecalho() {
        return cabecalho;
    }

    public void setCabecalho(String cabecalho) {
        this.cabecalho = cabecalho;
    }

    public String getRodape() {
        return rodape;
    }

    public void setRodape(String rodape) {
        this.rodape = rodape;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTipo() {return tipo;}

    public void setTipo(String tipo) {this.tipo = tipo;}
}

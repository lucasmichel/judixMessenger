package com.app.alg.judix.model;

/**
 * Created by lucas on 18/01/16.
 */
public class Oficial {

    private int id;
    private String login;
    private String senha;
    private String cpf;
    private String nome;
    private String assinatura;
    private char logado;

    public Oficial() {}

    public Oficial(int id, String login, String senha, String cpf, String nome, String assinatura, char logado) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.cpf = cpf;
        this.nome = nome;
        this.assinatura = assinatura;
        this.logado = logado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    public char getLogado() {
        return logado;
    }

    public void setLogado(char logado) {
        this.logado = logado;
    }

    public Boolean logar(){

        return false;
    }
}

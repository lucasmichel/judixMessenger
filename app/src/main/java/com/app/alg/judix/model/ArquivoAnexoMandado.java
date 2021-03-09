package com.app.alg.judix.model;

import java.io.Serializable;

/**
 * Created by lucas on 13/02/18.
 */

public class ArquivoAnexoMandado implements Serializable {

    private String id;
    private String endereco;
    private String nome;

    public ArquivoAnexoMandado() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}


package com.app.alg.judix.model;

/**
 * Created by lucas on 19/11/17.
 */

public class DispositivoBluetooth {

    private String id;
    private String nome;

    public DispositivoBluetooth(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }
    public DispositivoBluetooth() {
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
}

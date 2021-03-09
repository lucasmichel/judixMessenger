package com.app.alg.judix.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lucas on 18/01/16.
 */
public class Bd extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "dbJudix";
    private static final int VERSAO_DO_BANCO = 1;

    public Bd(Context context) {
        super(context, NOME_BANCO, null, VERSAO_DO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table oficial(_id integer primary key "+
                "autoincrement,   "+
                "login text not null, "+
                "senha text not null, "+
                "cpf text not null, "+
                "nome text not null, "+
                "assinatura text not null, "+
                "logado INTEGER DEFAULT 0, "+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

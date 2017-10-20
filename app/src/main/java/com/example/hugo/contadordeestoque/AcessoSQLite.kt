package com.example.hugo.contadordeestoque

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
* Created by hugo on 19/10/17.
*/
class AcessoSQLite(c: Context): ManagedSQLiteOpenHelper(c, "Estoque") {
    private val ID = "id"
    private val NOME = "nome"
    private val QUANTIDADE = "qtd"
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable("produtos")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(
                "produtos",
                true,
                ID to INTEGER,
                NOME to TEXT,
                QUANTIDADE to TEXT
        )
    }
}
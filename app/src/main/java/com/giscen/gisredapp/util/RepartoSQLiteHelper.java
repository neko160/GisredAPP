package com.giscen.gisredapp.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RepartoSQLiteHelper extends SQLiteOpenHelper {
    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate = "CREATE TABLE respartos ( id INTEGER PRIMARY KEY AUTOINCREMENT ,codigo TEXT, x NUMERIC, Y NUMERIC, fecha TEXT,tipo TEXT)";

    public RepartoSQLiteHelper(Context contexto, String nombre, SQLiteDatabase.CursorFactory factory, int version){
        super(contexto, nombre, factory, version);
    }
    public void onCreate(SQLiteDatabase db){
        db.execSQL( sqlCreate );
    }

    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva){
        db.execSQL( "DROP TABLE IF EXISTS repartos" );
        db.execSQL( sqlCreate );
    }
}

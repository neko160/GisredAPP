package com.giscen.gisredapp.entity;

public class MenuClass {
    private String titulo;
    private String descripcion;
    private boolean estado;
    private int res;

    public MenuClass(String tit, String desc, boolean est){
        titulo = tit;
        setDescripcion(desc);
        estado = est;
    }

    public MenuClass(String tit, boolean est){
        titulo = tit;
        estado = est;
    }

    public String getTitulo(){
        return titulo;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public boolean getEstado(){
        return estado;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

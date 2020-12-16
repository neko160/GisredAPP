package com.giscen.gisredapp.entity;

/**
 * Created by cramiret on 19-01-2020.
 */
public class RepartoClass {

    public static int length_code = 34;
    private static int length_req = 6;

    private int id;
    private int nis;
    private String codigo;
    private double x;
    private double y;
    private String fecha;
    private String tipo;

    public RepartoClass(int iId, String sCodigo, double dX, double dY, String dfecha, String dtipo){
        id = iId;
        nis = getNisByCode(sCodigo);
        codigo = sCodigo;
        x = dX;
        y = dY;
        fecha = dfecha;
       // tipo = getTipoByCode(dtipo);
        tipo = dtipo;
    }

    private static int getNisByCode(String cod) {
        if (cod.length() < length_code)
            return 0;
        else {
            int fx = cod.length() - length_code + length_req;
            String sNis = cod.substring(0, fx);

            if (Integer.valueOf(sNis) != null) {
                return Integer.valueOf(sNis);
            } else return 0;
        }
    }
    //Obtenemos el tipo de reparto
    //se deben agregar tantos case como codigos de reparto existan
    //esto valida la posicion correcta 11 +3
    private static String getTipoByCode(String cod) {

        switch (cod){
            case "MDV" :
                cod="CartaPoda";
                break;
            default:
                cod="Boleta";
                break;
        }

        return  cod ;
    }
    // validamos el codigo scaneando
    public static boolean valCode(String cod) {
      /*  if (cod.length() < length_code) {
            return false;
        }else if(oCaracter(cod) == true) {
            return false;
        }else if(oSpecialCharacter(cod)==true){
            return true;
        }else{
            int fx = cod.length() - length_code + length_req;
            String sNis = cod.substring(0, fx);

            return Integer.valueOf(sNis) != null && Integer.valueOf(sNis) > 0;
        }*/
      if(cod.length() != length_code){
            return false;
      }else if(oSpecialCharacter( cod ) == false){
          return false;
      }
      return true;
    }

    private static boolean oSpecialCharacter(String cod) {
        if(cod.substring(17,18).equals( "P" )){
            return true;
        }else{
            return false;
        }
    }

    // Comprobamos que los 6 primeros digitos no contengan letras
    public  static boolean oCaracter(String Cadena){

        String letras="abcdefghyjklmnñopqrstuvwxyz";
        Cadena = Cadena.substring(0,6);
        Cadena = Cadena.toLowerCase();
        for(int i=0 ;i < Cadena.length();i++) {
            if(letras.indexOf(Cadena.charAt(i))!=-1){
                return true;
            }
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public int getNis() {
        return nis;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    public String getFecha() {
        return  fecha;
    }
    public String getTipo(){
        return tipo;
    }

}

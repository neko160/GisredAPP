package com.giscen.gisredapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.security.AuthenticationChallengeResponse;
import com.esri.arcgisruntime.security.UserCredential;
import com.giscen.gisredapp.entity.customDialog;
import com.giscen.gisredapp.util.CheckNetwork;
import com.giscen.gisredapp.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Result;


public class SplashActivity extends AppCompatActivity {

    public String usuario, password, domain;
    private TextView welcome;
    private String sError;

    private String sNomEquipo;
    private String sImei;
    private String sFecha;

    UserCredential credenciales;
    Bundle bundle;
    ServiceFeatureTable accesFeatureTable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
     /*   Intent oIntent = new Intent( getApplicationContext(), LoginActivity.class );
        startActivity( oIntent );*/
        String FEATURE_URL = getResources().getString( R.string.srv_Access );
        accesFeatureTable = new ServiceFeatureTable( FEATURE_URL );
        domain = getResources().getString( R.string.domain );
        welcome  = findViewById( R.id.txtRecuperaPass );
        //Verifica permisos del telefono necesarios para la aplicacion
        verificaPermisos();

    }

    private void verificaPermisos() {
        if(ContextCompat.checkSelfPermission( SplashActivity.this, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale( SplashActivity.this,Manifest.permission.READ_PHONE_STATE )){
                customDialog dialog = new customDialog();
                dialog.showWarningDialog( "Error de Permisos", "GISRED necesita algunos permisos para poder funcionar correctamente","Autoriza?","No",1,SplashActivity.this );
            }else{
                ActivityCompat.requestPermissions( SplashActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, Util.REQUEST_READ_PHONE_STATE );
            }
        }else{

            CheckNetwork checkNetwork = new CheckNetwork( getApplicationContext() );

            boolean estado = checkNetwork.isConnectingToInternet();
            welcome.setText("Verificando conexión a internet");
            if(estado){

                VerificaLogin();
            }else{
                customDialog dialog = new customDialog();
                dialog.showErrorDialog( "Conexión","Verifique la red de datos del equipo \n sin conexión a internet","Salir",1,SplashActivity.this);
            }
        }

    }

    private void VerificaLogin() {

        SharedPreferences sharedPreferences = getSharedPreferences( "GISREDPrefe",Context.MODE_PRIVATE );
        if(sharedPreferences.contains( "username" )&& sharedPreferences.contains( "password" )){
            usuario = sharedPreferences.getString( "username","" );
            password = sharedPreferences.getString( "password","" );

            if(usuario.isEmpty()||password.isEmpty()){

            }else{
                setCredeciales(usuario,password);
                sNomEquipo = Util.getDeviceName();
                sImei = Util.getImei( getApplicationContext() );
                sFecha = DateFormat.format( "dd-MM-yyyy HH:mm:ss", new Date() ).toString();
                try{
                    ConsultaPErmisos(usuario);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            Intent oIntent = new Intent( getApplicationContext(),LoginActivity.class );
            startActivity( oIntent );
        }
    }

    private void ConsultaPErmisos(String usuario) {

        welcome.setText( "Verificando Credenciales" );
        new AuthenticationChallengeResponse( AuthenticationChallengeResponse.Action.CONTINUE_WITH_CREDENTIAL,credenciales );
        accesFeatureTable.setCredential( credenciales );


        String whereClause = "usuario = '" + domain + usuario + "' AND plataforma = 'MOVIL'";
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause( whereClause );
        queryParameters.setReturnGeometry( false );
        Log.d( "LOGIN:" , "HE ENTRADO EN ERROR DE LOGIN1" );
        final ListenableFuture<FeatureQueryResult> future = accesFeatureTable.queryFeaturesAsync( queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL );
        future.addDoneListener( () -> {
            ArrayList arrayModulos = new ArrayList();
            ArrayList arrayEmpresas = new ArrayList();
            ArrayList arrayWidgets = new ArrayList();

            try {
                FeatureQueryResult result = future.get();
                Iterator<Feature> iterator = result.iterator();
                Feature feature;

                if(result== null){
                    sError= "Usuario no tiene acceso a aplicacion Mòvil GISRED";
                }else{
                    String emp_module = "";
                    String modulo = "";
                    String userDominio = new String();
                    String widget = "";
                    String empresa = "";

                    while (iterator.hasNext()){
                        feature = iterator.next();
                        Map<String,Object> attr = feature.getAttributes();

                        empresa = (String) attr.get("empresa");
                        modulo = (String) attr.get("modulo");
                        widget = (String) attr.get("widget");
                        if(empresa !=null){
                            if(!arrayEmpresas.contains((empresa))){
                                arrayEmpresas.add(empresa);
                            }
                            emp_module = empresa.toString();
                        }
                        arrayModulos.add(emp_module + "@" + modulo);
                        userDominio = (String)  attr.get("usuario");
                        arrayWidgets.add(emp_module + "@" + widget);
                    }
                    CharSequence Csl = domain;
                    boolean retval =  userDominio.contains(Csl);
                    bundle = new Bundle();
                    if(retval) bundle.putString("usuarioLogin",domain+usuario);
                    else bundle.putString("usuarioLogin",usuario);

                    bundle.putString("passwordLogin", password);
                    bundle.putStringArrayList("modulos",arrayModulos);
                    bundle.putStringArrayList("empresas",arrayEmpresas);
                    bundle.putStringArrayList("widgets",arrayWidgets);
                    bundle.putString("imei",sImei);

                    Map<String, Object> attributes = new HashMap<>();

                    attributes.put("usuario", credenciales.getUsername());
                    attributes.put("fecha",sFecha);
                    attributes.put("pagina","Mobile");
                    attributes.put("modulo","GISRED 2.0" + Util.getVersionPackage());
                    attributes.put("nom_equipo",sNomEquipo);
                    attributes.put("ip",sImei);

                    Intent intent = new Intent(getApplicationContext(),EmpresaActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            }catch (Exception e){
                Intent oIntent = new Intent( getApplicationContext(),LoginActivity.class );
               startActivity( oIntent );

            }
        } );
    }

    private void setCredeciales(String usuario, String password) {
        credenciales = new UserCredential(domain + usuario,password);
        new AuthenticationChallengeResponse( AuthenticationChallengeResponse.Action.CONTINUE_WITH_CREDENTIAL,credenciales );
        accesFeatureTable.setCredential( credenciales );
    }
/*
    private class AsynQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            welcome.setVisibility( View.VISIBLE );
            welcome.setText( "Verificando Conexion a internet" );
        }

        @Override
        protected String doInBackground(String... params) {

          welcome.setText( "Verificando Credenciales" );
          new AuthenticationChallengeResponse( AuthenticationChallengeResponse.Action.CONTINUE_WITH_CREDENTIAL,credenciales );

          accesFeatureTable.setCredential( credenciales );


          String whereClause = "usuario = '" + domain + params[0] + "' AND plataforma = 'MOVIL'";
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setWhereClause( whereClause );
            queryParameters.setReturnGeometry( false );
            Log.d( "LOGIN:" , "HE ENTRADO EN ERROR DE LOGIN1" );
            final ListenableFuture<FeatureQueryResult> future = accesFeatureTable.queryFeaturesAsync( queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL );
            future.addDoneListener( () -> {
                ArrayList arrayModulos = new ArrayList();
                ArrayList arrayEmpresas = new ArrayList();
                ArrayList arrayWidgets = new ArrayList();

                try {
                    FeatureQueryResult result = future.get();
                    Iterator<Feature> iterator = result.iterator();
                    Feature feature;

                    if(result== null){
                        sError= "Usuario no tiene acceso a aplicacion Mòvil GISRED";
                    }else{
                        String emp_module = "";
                        String modulo = "";
                        String userDominio = new String();
                        String widget = "";
                        String empresa = "";

                        while (iterator.hasNext()){
                            feature = iterator.next();
                            Map<String,Object> attr = feature.getAttributes();

                            empresa = (String) attr.get("empresa");
                            modulo = (String) attr.get("modulo");
                            widget = (String) attr.get("widget");
                            if(empresa !=null){
                                if(!arrayEmpresas.contains((empresa))){
                                    arrayEmpresas.add(empresa);
                                }
                                emp_module = empresa.toString();
                            }
                            arrayModulos.add(emp_module + "@" + modulo);
                            userDominio = (String)  attr.get("usuario");
                            arrayWidgets.add(emp_module + "@" + widget);
                        }
                        CharSequence Csl = domain;
                        boolean retval =  userDominio.contains(Csl);
                        bundle = new Bundle();
                        if(retval) bundle.putString("usuarioLogin",domain+usuario);
                        else bundle.putString("usuarioLogin",usuario);

                        bundle.putString("passwordLogin", password);
                        bundle.putStringArrayList("modulos",arrayModulos);
                        bundle.putStringArrayList("empresas",arrayEmpresas);
                        bundle.putStringArrayList("widgets",arrayWidgets);
                        bundle.putString("imei",sImei);

                        Map<String, Object> attributes = new HashMap<>();

                        attributes.put("usuario", credenciales.getUsername());
                        attributes.put("fecha",sFecha);
                        attributes.put("pagina","Mobile");
                        attributes.put("modulo","GISRED 2.0" + Util.getVersionPackage());
                        attributes.put("nom_equipo",sNomEquipo);
                        attributes.put("ip",sImei);

                        Intent intent = new Intent(getApplicationContext(),EmpresaActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

                        final EditText txtUsuario = (EditText) findViewById(R.id.TxtUser);
                        final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

                        txtPassword.setText( "" );
                        txtPassword.getText().clear();
                        txtUsuario.setText( "" );
                        txtUsuario.getText().clear();
                    }
                }catch (Exception e){
                   // e.printStackTrace();
                    sError = "Credenciales Incorrectas";


                    //     customDialog myDialog = new customDialog();
                //    myDialog.showErrorDialog( "Login","Credenciales no son validas","Aceptar",1, SplashActivity.this);

                }
            } );
            Log.d( "MYLOGINaq:" , "es: " + sError);
          //  return sError;
            return sError;
        }

        @Override
        protected void onPostExecute(String s) {
         //   super.onPostExecute( s );
            Log.d( "MYLOGINa:" , "es: " + s );
            if (s != null){
                Toast.makeText( SplashActivity.this, s,Toast.LENGTH_SHORT ).show();

            }
        }
    }
*/
}
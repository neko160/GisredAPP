package com.giscen.gisredapp;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.security.AuthenticationChallengeResponse;
import com.esri.arcgisruntime.security.UserCredential;
import com.giscen.gisredapp.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public String usuario, password,domain;
    UserCredential credenciales;
    Bundle bundle;

    // progress
    private ProgressBar progressBar;
    //Layer definition
    ServiceFeatureTable accesFeatureTable;
    //Almacena error
    private String sError;
    private String sImei;
    private String sFecha;
    private String sNomEquipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        final Button btnIngresar = (Button) findViewById( R.id.btnLogin );
        final EditText txtUsuario = (EditText) findViewById( R.id.TxtUser );
        final EditText txtPassword = (EditText) findViewById( R.id.txtPassword );

        String FEATURE_URL = getResources().getString( R.string.srv_Access );
        accesFeatureTable = new ServiceFeatureTable( FEATURE_URL );



        domain = getResources().getString( R.string.domain );

        btnIngresar.setOnClickListener( view -> {
            try {
                if(txtUsuario != null){
                    usuario = txtUsuario.getText().toString();
                }
                if(txtPassword != null){
                    password = txtPassword.getText().toString();
                }
                if(usuario.isEmpty()){
                    Toast.makeText(LoginActivity.this ,"Ingrese nombre de usuario",Toast.LENGTH_SHORT).show();
                }else if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this ,"Ingrese password",Toast.LENGTH_SHORT).show();
                }else{
                    setCredenciales(usuario,password);
                    if(Online()){
                        AsynQueryTask queryTask = new AsynQueryTask();
                        queryTask.execute( usuario );

                    }else{
                        Toast.makeText(LoginActivity.this ,"Sin conexión a internet",Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } );
    }

    private boolean Online() {
        try {
          return true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void setCredenciales(String usuario, String password) {
        credenciales = new UserCredential( domain + usuario,password );
    }

    private class AsynQueryTask extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            new AuthenticationChallengeResponse( AuthenticationChallengeResponse.Action.CONTINUE_WITH_CREDENTIAL,credenciales );
            accesFeatureTable.setCredential( credenciales );

            String whereClause = "usuario ='" + domain + strings[0] + "' AND plataforma = 'MOVIL'";
            Log.d( "MI error:",whereClause );
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setWhereClause( whereClause );
            queryParameters.setReturnGeometry( false );


            final ListenableFuture<FeatureQueryResult> future = accesFeatureTable.queryFeaturesAsync( queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL );
            future.addDoneListener( () -> {
                ArrayList arrayModulos = new ArrayList(  );
                ArrayList arrayEmpresas = new ArrayList(  );
                ArrayList arrayWidgets = new ArrayList(  );

                try {
                        FeatureQueryResult result = future.get();
                        // create an Iterator
                        Iterator<Feature> iterator = result.iterator();
                        Feature feature;

                        if (result == null){
                            sError = "Usuario no tiene acceso a aplicacion Mòvil GISRED";
                        }else{
                            String emp_module = "";
                            String modulo = "";
                            String userDominio = new String();
                            String widget = "";
                            String empresa = "";

                            SharedPreferences prefs = getSharedPreferences("GISREDPrefe", Context.MODE_PRIVATE );
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString( "username" ,usuario);
                            editor.putString( "password",password );
                            editor.apply();

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
                            final EditText txtPass = (EditText) findViewById(R.id.txtPassword);

                            txtPass.setText( "" );
                            txtPass.getText().clear();
                            txtUsuario.setText( "" );
                            txtUsuario.getText().clear();
                   }

                }catch (Exception e){
                    e.printStackTrace();
                    sError = "Credenciales Incorrectas";
                }
            });
            return sError;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute( s );
            if (s != null){
                Toast.makeText( LoginActivity.this, s,Toast.LENGTH_SHORT ).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}
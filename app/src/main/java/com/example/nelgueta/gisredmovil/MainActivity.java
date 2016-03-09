package com.example.nelgueta.gisredmovil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;

import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.FeatureSet;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    public String usuario, password;
    UserCredentials credenciales;
    UserCredentials credenciales2;

    ProgressDialog progress;

    String usuario2 = "vialactea\\nelgueta";
    String password2 = "Feb.2016";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnIngresar = (Button) findViewById(R.id.btnLogin);
        final EditText txtUsuario = (EditText) findViewById(R.id.usuario);
        final EditText txtPassword = (EditText) findViewById(R.id.password);



        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                credenciales2 = new UserCredentials();
                credenciales2.setUserAccount(usuario2, password2);


                usuario = txtUsuario.getText().toString();
                password = txtPassword.getText().toString();
                //Set Credenciales
                setCredenciales(usuario, password);


                Consulta_permisos_usuario();



              /*  if(txtUsuario.getText().toString().equals("nelgueta") && txtPassword.getText().toString().equals("Feb.2016")){
                    usuario = txtUsuario.getText().toString();
                    password = txtPassword.getText().toString();

                    Bundle bundle = new Bundle();
                    bundle.putString("usuarioLogin", "vialactea\\" + usuario);
                    bundle.putString("passwordLogin", password);

                    Intent intent = new Intent(MainActivity.this,MenuActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }else{
                    Toast.makeText(MainActivity.this,"Login Incorrecto",Toast.LENGTH_SHORT).show();
                }*/

            }
        });
    }

    public void setCredenciales(String usuario , String password) {
        credenciales = new UserCredentials();
        credenciales.setUserAccount("vialactea\\" + usuario, password);


    }

        public void Consulta_permisos_usuario()
    {
        //System.out.println(usuario + "------" + password);
     /*   QueryParameters queryParams = new QueryParameters();
        queryParams.setWhere("usuario = 'vialactea\\" + usuario + "' AND modulo = 'MOVIL'");
        queryParams.setReturnGeometry(false);
        String[] outfields = new String[]{"usuario","modulo", "widget","insert_","delete_","update_","select_"};
        queryParams.setOutFields(outfields);
*/
        AsyncQueryTask queryTask = new AsyncQueryTask();

        queryTask.execute(usuario);

    }

    private class AsyncQueryTask extends AsyncTask<String,Void,FeatureResult> {

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(MainActivity.this);

            progress = ProgressDialog.show(MainActivity.this, "",
                    "Espere por favor... Verificando usuario.");

        }

        @Override
        protected FeatureResult doInBackground(String... params) {
            System.out.println(params[0]);
            String whereClause = "usuario = 'vialactea\\" + params[0] + "' AND modulo = 'MOVIL'";
            QueryParameters myParameters = new QueryParameters();
            myParameters.setWhere(whereClause);
            myParameters.setReturnGeometry(false);
            String[] outfields = new String[]{"usuario","modulo", "widget","insert_","delete_","update_","select_"};
            myParameters.setOutFields(outfields);


                    FeatureResult results;  try {
                        QueryTask queryTask = new QueryTask("http://gisred.chilquinta.cl:5555/arcgis/rest/services/Admin/LogAccesos/MapServer/2",credenciales2);
                    results = queryTask.execute(myParameters);
                    return results;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            return null;
        }

        protected void onPostExecute(FeatureResult results){

            ArrayList arrayModulos =  new ArrayList();
            String userDominio = new String();

            if (results != null){
                int size = (int) results.featureCount();

                for (Object element : results) {
                    progress.incrementProgressBy(size / 100);
                    if (element instanceof Feature) {
                        Feature feature = (Feature) element;
                        arrayModulos.add(feature.getAttributeValue("widget"));
                        userDominio = (String)feature.getAttributeValue("usuario");

                    }
                }

                CharSequence Cs1 = "vialactea\\";
                boolean retval = userDominio.contains(Cs1);

                if (retval == true) {
                    Toast.makeText(MainActivity.this, "User Dominio", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("usuarioLogin", "vialactea\\" + usuario);
                    bundle.putString("passwordLogin", password);
                    bundle.putStringArrayList("widgets", arrayModulos);

                    Intent intent = new Intent(MainActivity.this,MenuActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "User Generico", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("usuarioLogin", usuario2);
                    bundle.putString("passwordLogin", password2);
                    bundle.putStringArrayList("widgets",arrayModulos);

                    Intent intent = new Intent(MainActivity.this,MenuActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }


            }else{
                    System.out.println("---------------------------"+results+"--------------------------------");
                    Toast.makeText(MainActivity.this,"Login Incorrecto",Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    return;
            }
            progress.dismiss();
        }
    }


}




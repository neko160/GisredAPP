package com.giscen.gisredapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.giscen.gisredapp.entity.customDialog;

import java.util.ArrayList;

public class EmpresaActivity extends AppCompatActivity {
    ArrayList<String> aEmpresas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_empresa );


        Toolbar toolbar = (Toolbar) findViewById(R.id.apptool);
        setSupportActionBar(toolbar);

        try {
            final Bundle bundle = getIntent().getExtras();
            aEmpresas = bundle.getStringArrayList("empresas");

            ImageButton btnChilquinta = (ImageButton) findViewById(R.id.btnChilquinta);
            if(aEmpresas.contains("chilquinta")){
                btnChilquinta.setImageResource(R.drawable.chilquinta);
                btnChilquinta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToMainMenu(bundle, "chilquinta");
                    }
                });
            }else{
                btnChilquinta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "No tiene acceso a los módulos de ésta empresa", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            ImageButton btnLitoral = (ImageButton) findViewById(R.id.btnLitoral);
            if(aEmpresas.contains("litoral")){
                btnLitoral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToMainMenu(bundle,"litoral");
                    }
                });
            }else{
                btnLitoral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "No tiene acceso a los módulos de ésta empresa", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            ImageButton btnCasablanca = (ImageButton) findViewById(R.id.btnCasablanca);
            if(aEmpresas.contains("casablanca")){
                btnCasablanca.setImageResource(R.drawable.casablanca);
                btnCasablanca.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToMainMenu(bundle,"casablanca");
                    }
                });
            }else{
                btnCasablanca.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "No tiene acceso a los módulos de ésta empresa", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            ImageButton btnLinares = (ImageButton) findViewById(R.id.btnLinares);
            if(aEmpresas.contains("linares")){
                btnLinares.setImageResource(R.drawable.linares);
                btnLinares.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToMainMenu(bundle,"linares");
                    }
                });
            }else{
                btnLinares.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "No tiene acceso a los módulos de ésta empresa", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            ImageButton btnParral = (ImageButton) findViewById(R.id.btnParral);
            if(aEmpresas.contains("parral")) {
                btnParral.setImageResource(R.drawable.parral);
                btnParral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToMainMenu(bundle,"parral");
                    }
                });
            }else{
                btnParral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "No tiene acceso a los módulos de ésta empresa", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void goToMainMenu(Bundle oBundle, String mEmpresa) {
        Intent intent = new Intent(getApplicationContext(), ModulosActivity.class);
        oBundle.putString("empresa", mEmpresa);
        intent.putExtras(oBundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //abrirFormNew();
                return true;
            case R.id.action_logout:
                customDialog myDialog = new customDialog();
                myDialog.showWarningDialog(  "Salir",  "¿Desea cerrar la sesión?","Sí", "Nó",2,EmpresaActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        /*DialogoConfirmacion dialogo = new DialogoConfirmacion();
        dialogo.show(getFragmentManager(), "tagAlerta");*/

    }
}
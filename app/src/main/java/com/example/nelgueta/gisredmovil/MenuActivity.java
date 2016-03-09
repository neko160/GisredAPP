package com.example.nelgueta.gisredmovil;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {

    String usuar, passw;
    ArrayList widgets;

    Boolean wStandard = false,wCNR = false ,wTecnored = false;

    public ListView maListViewPerso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

         Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Módulos");


        Bundle bundle = getIntent().getExtras();
        usuar = bundle.getString("usuarioLogin");
        passw = bundle.getString("passwordLogin");
        widgets = bundle.getStringArrayList("widgets");




        for (int i = 0; i < widgets.size(); i++) {
            System.out.println(widgets.get(i));
            if (widgets.get(i).equals("STANDARD")){
                wStandard = true;
            }
            else if (widgets.get(i).equals("CNR")) {
                wCNR =  true;
            }
            else if (widgets.get(i).equals("TECNORED")) {
                wTecnored =true;
            }
        }

        maListViewPerso =(ListView)findViewById(R.id.listviewperso);

        ArrayList<HashMap<String,String>> listItem = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> map;


        map = new HashMap<String, String>();
        map.put("titulo", "Estandard");
        map.put("description", "Acceso Usuarios standard");
        map.put("img", String.valueOf(R.drawable.logo_gisred));
        listItem.add(map);

        map = new HashMap<String, String>();
        map.put("titulo", "Ingreso Clientes CNR");
        map.put("description", "Acceso para ingresar Clientes(CNR)");
        map.put("img", String.valueOf(R.drawable.logo_gisred));
        listItem.add(map);

        map = new HashMap<String, String>();
        map.put("titulo", "Ingreso Clientes Tecnored");
        map.put("description", "Acceso para ingresar nuevos clientes instalados por tecnored");
        map.put("img", String.valueOf(R.drawable.logo_gisred));
        listItem.add(map);

        SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),listItem,R.layout.affichageitem,
                new String[]{"img","titulo","description"}, new int[]{R.id.img,R.id.titulo,R.id.description});

        maListViewPerso.setAdapter(mSchedule);

        //Enfin on met un écouteur d'évènement sur notre listView
        maListViewPerso.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                //on récupère la HashMap contenant les infos de notre item (titre, description, img)
                HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
                //on créer une boite de dialogue
             /*  AlertDialog.Builder adb = new AlertDialog.Builder(menu_activity.this);
                //on attribut un titre à notre boite de dialogue
                adb.setTitle("Sélection Item");
                //on insère un message à notre boite de dialogue, et ici on affiche le titre de l'item cliqué
                adb.setMessage("Votre choix : " + map.get("titre"));
                //on indique que l'on veut le bouton ok à notre boite de dialogue
                adb.setPositiveButton("Ok", null);
                //on affiche la boite de dialogue
                adb.show();
*/

                //Standard
                if(position == 0)
                {
                    if (wStandard ==true) {

                        Bundle bundle = new Bundle();
                        bundle.putString("usuarioLogin2", usuar);
                        bundle.putString("passwordLogin2",passw);

                        Intent intent = new Intent(MenuActivity.this,MapActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MenuActivity.this, "No tiene permisos para entrar a este modulo", Toast.LENGTH_SHORT).show();
                    }


                }//Ingreso Clientes CNR
                else if (position == 1)
                {
                    if (wCNR==true) {
                        Toast.makeText(MenuActivity.this, "Menu CNR", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MenuActivity.this, "No tiene permisos para entrar a este modulo", Toast.LENGTH_SHORT).show();
                    }

                }//Ingreso Clientes Tecnored
                else if (position == 2)
                {
                    if (wTecnored==true) {
                        Toast.makeText(MenuActivity.this, "Menu Tencored", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MenuActivity.this, "No tiene permisos para entrar a este modulo", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

}

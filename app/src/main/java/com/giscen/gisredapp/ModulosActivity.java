package com.giscen.gisredapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.security.UserCredential;
import com.giscen.gisredapp.entity.MenuClass;

import java.util.ArrayList;
import java.util.Arrays;

public class ModulosActivity extends AppCompatActivity {


    private MenuClass[] datos;
    private ListView lstOpciones;
    ArrayList<String> aModulos;
    ArrayList<String> aWidgets;
    private String sEmpresa;
    private String sImei;
    private Bundle bundle;

    String usuario, domain, password;
    UserCredential credenciales;

    private FeatureLayer oLayerAccess;

    // Variables de acceso
    //ArrayList arrayModulos = new ArrayList(Arrays.asList("STANDARD", "INGRESO_CLIENTES", "PROTOCOLO_INSPECCION", "OT", "LECTORES", "INTERRUPCIONES", "MICROMEDICION", "REPARTOS", "MANTENIMIENTO", "REGISTRO_EQUIPOS"));
    // ArrayList arrayModulos = new ArrayList(Arrays.asList("STANDARD", "INGRESO_CLIENTES", "PROTOCOLO_INSPECCION", "LECTORES", "TELEMEDIDA", "CATASTRO_AP", "INTERRUPCIONES", "MICROMEDICION", "REPARTOS", "ALUMBRADO_PUBLICO", "EH&S"));
    ArrayList arrayModulos = new ArrayList( Arrays.asList("STANDARD", "REPARTOS"));

    public void setCredenciales(String usuario , String password) {
        domain = getResources().getString(R.string.domain);

        credenciales = new UserCredential(domain + usuario,password);

        //oLayerAccess = new ArcGISFeatureLayer(getResources().getString(R.string.srv_LogAccess), ArcGISFeatureLayer.MODE.ONDEMAND, credenciales);
    }

    public void getWidgets() {
        if (arrayModulos != null && arrayModulos.size() > 0) {

            datos = new MenuClass[arrayModulos.size()];
            int cont = 0;

            for (Object modulo : arrayModulos) {

                try {
                    String sModulo = (String) modulo;
                    String sComplexModulo = sEmpresa + "@" + sModulo;
                    MenuClass oMenu = new MenuClass( sModulo.replace( "_", " " ), aModulos.contains( sComplexModulo ) );
                    datos[cont] = oMenu;
                    cont++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (datos != null && datos.length > 0) {
            AdaptadorMenus adaptador;
            adaptador = new AdaptadorMenus( ModulosActivity.this, datos );

            lstOpciones.setAdapter( adaptador );
        } else {
            Toast.makeText( ModulosActivity.this, "No hay datos, verifique credenciales", Toast.LENGTH_LONG ).show();
            Intent oIntent = new Intent( getApplicationContext(), LoginActivity.class );
            startActivity( oIntent );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_modulos );

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        lstOpciones = (ListView)findViewById(R.id.LstOpciones);

        bundle = getIntent().getExtras();
        aModulos = bundle.getStringArrayList("modulos");
        aWidgets = bundle.getStringArrayList("widgets");
        usuario = bundle.getString("usuarioLogin");
        password = bundle.getString("passwordLogin");
        sEmpresa = bundle.getString("empresa");
        sImei = bundle.getString("imei");

        Log.d("Error",usuario + password);


        setCredenciales(usuario, password);

        getWidgets();

        lstOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (view.isEnabled()){
                    Intent oIntent;
                    Bundle oBundle = new Bundle();

                    //String sFecha = DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()).toString();
                    //String sPagina = String.format("Mobile-%s-%s", sEmpresa.toLowerCase(), datos[position].getTitulo().toLowerCase());

                    if (datos[position].getTitulo().contains("REPARTOS")){
                        oIntent = new Intent(ModulosActivity.this, RepartoActivity.class);
                        oBundle.putStringArrayList("modulos", aModulos);
                    } else if (datos[position].getTitulo().contains("INSPECCION")){
                        oIntent = new Intent(ModulosActivity.this, RepartoActivity1.class);
                        oBundle.putStringArrayList("modulos", aModulos);
                 /*   } else if (datos[position].getTitulo().contains("OT")){
                        oIntent = new Intent(MainActivity.this, OtListActivity.class);
                        oBundle.putStringArrayList("modulos", aModulos);
                    }  else if (datos[position].getTitulo().contains("LECTORES")){
                        oIntent = new Intent(MainActivity.this, FormLectActivity.class);
                        oBundle.putStringArrayList("modulos", aModulos);
                    } else if (datos[position].getTitulo().contains("INTERRUPCIONES")){
                        oIntent = new Intent(MainActivity.this, PowerOnActivity.class);
                    } else if (datos[position].getTitulo().contains("EH&S")){
                        oIntent = new Intent(MainActivity.this, FormEhysActivity.class);
                        oBundle.putStringArrayList("modulos", aModulos);
                    } else if (datos[position].getTitulo().contains("MICRO")){
                        oIntent = new Intent(MainActivity.this, MicroMedidaActivity.class);
                        oBundle.putStringArrayList("modulos", aModulos);
                    } else if (datos[position].getTitulo().contains("CLIENTES")){
                        oIntent = new Intent(MainActivity.this, MapsActivity.class);
                        oBundle.putStringArrayList("modulos", aModulos);
                    } else if (datos[position].getTitulo().contains("CATASTRO")){
                        oIntent = new Intent(MainActivity.this, CatastroActivity.class);
                        oBundle.putStringArrayList("modulos", aModulos);
                    } else if (datos[position].getTitulo().contains("MEDIDORES")){
                        oIntent = new Intent(MainActivity.this, MedidorActivity.class);
                        oBundle.putStringArrayList("modulos", aModulos);
                    } else if (datos[position].getTitulo().contains("MANTENIMIENTO")){
                        oIntent = new Intent(MainActivity.this, FormMantActivity.class);
                        oBundle.putStringArrayList("modulos", aModulos);
                    } else if (datos[position].getTitulo().contains("EQUIPOS")){
                        oIntent = new Intent(MainActivity.this, RegEquipoActivity.class);
                        oBundle.putStringArrayList("modulos", aModulos);*/
                    } else
                        oIntent = new Intent(ModulosActivity.this, StandardActivity.class);

                    //Comentado el 02/03/17 para optimizar carga de mapa
                    /*Map<String, Object> attributes = new HashMap<>();

                    attributes.put("usuario", credenciales.getUserName());
                    attributes.put("fecha", sFecha);
                    attributes.put("pagina", sPagina);
                    attributes.put("modulo", "GISRED 2.0" + Util.getVersionPackage());
                    attributes.put("nom_equipo", Util.getDeviceName());
                    attributes.put("ip", sImei);

                    Graphic newFeature = new Graphic(null, null, attributes);
                    Graphic[] addsLogin = {newFeature};

                    oLayerAccess.applyEdits(addsLogin, null, null, callBackUnion());*/

                    oBundle.putString("empresa", sEmpresa);
                    oBundle.putString("usuario", usuario);
                    oBundle.putString("password", password);
                    oBundle.putString("modulo", datos[position].getTitulo());
                    oBundle.putStringArrayList("widgets", aWidgets);
                    oIntent.putExtras(oBundle);
                    startActivity(oIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No tiene permisos para éste módulo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        class AdaptadorMenus extends ArrayAdapter<MenuClass> {

            public AdaptadorMenus(Context context, MenuClass[] datos) {
                super(context, R.layout.list_item_menu, datos);
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View item = inflater.inflate(R.layout.list_item_menu, null);

                TextView lblTitulo = (TextView) item.findViewById(R.id.LblTitulo);
                lblTitulo.setText(datos[position].getTitulo());

                item.setEnabled(datos[position].getEstado());
                datos[position] = getDataByModule(datos[position]);

                TextView lblDescripcion = (TextView) item.findViewById(R.id.LblDescripcion);
                lblDescripcion.setText(datos[position].getDescripcion());

                ImageView oImage = (ImageView) item.findViewById(R.id.imageMenu);
                oImage.setImageResource(datos[position].getRes());

                return(item);
            }

            private MenuClass getDataByModule(MenuClass dato) {
                if (dato.getTitulo().contains("STANDARD")) {
                    dato.setDescripcion("Módulo de visualización standard");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_standard : R.mipmap.ic_menu_standard_g);
                } else if (dato.getTitulo().contains("CLIENTES")) {
                    dato.setDescripcion("Visualización e ingreso clientes en terreno");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_ing_clientes : R.mipmap.ic_menu_ing_clientes_g);
                } else if (dato.getTitulo().contains("INSPECCION")) {
                    dato.setDescripcion("Visualización e ingreso de inspecciones");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_protocolo_inspeccion : R.mipmap.ic_menu_protocolo_inspeccion_g);
                } else if (dato.getTitulo().contains("LECTORES")) {
                    dato.setDescripcion("Visualización e ingreso de lecturas");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_ing_lectores : R.mipmap.ic_menu_ing_lectores_g);
                } else if (dato.getTitulo().contains("OT")) {
                    dato.setDescripcion("Ordenes de trabajo");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_protocolo_inspeccion : R.mipmap.ic_menu_protocolo_inspeccion_g);
                } else if (dato.getTitulo().contains("TELEMEDIDA")) {
                    dato.setDescripcion("Visualización e ingreso de telemedidas");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_telemedida : R.mipmap.ic_menu_telemedida_g);
                } else if (dato.getTitulo().contains("ALUMBRADO")) {
                    dato.setDescripcion("Visualización e ingreso de alumbrado público");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_telemedida : R.mipmap.ic_menu_telemedida_g);
                } else if (dato.getTitulo().contains("INTERRUPCIONES")) {
                    dato.setDescripcion("Visualización de interrupciones");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_power_on : R.mipmap.ic_menu_power_on_g);
                } else if (dato.getTitulo().contains("EH&S")) {
                    dato.setDescripcion("Módulo EH&S");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_protocolo_inspeccion : R.mipmap.ic_menu_protocolo_inspeccion_g);
                } else if (dato.getTitulo().contains("MICRO")) {
                    dato.setDescripcion("Visualización e ingreso de catastros de medidores");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_telemedida : R.mipmap.ic_menu_telemedida_g);
                } else if (dato.getTitulo().contains("REPARTOS")) {
                    dato.setDescripcion("Ingreso de correspondencia repartida");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_telemedida : R.mipmap.ic_menu_telemedida_g);
                } else if (dato.getTitulo().contains("MANTENIMIENTO")) {
                    dato.setDescripcion("Ingreso de registros de mantención");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_protocolo_inspeccion : R.mipmap.ic_menu_protocolo_inspeccion_g);
                } else if (dato.getTitulo().contains("EQUIPOS")) {
                    dato.setDescripcion("Agregar y retirar equipos");
                    dato.setRes((dato.getEstado()) ? R.mipmap.ic_menu_protocolo_inspeccion : R.mipmap.ic_menu_protocolo_inspeccion_g);
                }
                return dato;
            }
        }
}
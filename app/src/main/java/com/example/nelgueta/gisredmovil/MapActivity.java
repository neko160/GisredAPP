package com.example.nelgueta.gisredmovil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.bing.BingMapsLayer;
import com.esri.core.geometry.Polygon;

import com.esri.core.io.UserCredentials;
import com.esri.core.io.EsriSecurityException;
import com.esri.android.map.event.OnStatusChangedListener;





public class MapActivity extends AppCompatActivity {

    ArrayList<Integer>  mSelectedItems = new ArrayList<Integer>();


    MapView myMapView = null;

    //INSTANCES
    UserCredentials credenciales;
    String usuar2, passw2;

    //url para token srv
    String urlToken;

    //ArrayList Layer
    public String[] listadoCapas = {"SED", "SSEE", "Salida Alimentador", "Red MT", "Red BT", "Red AP", "Postes", "Equipos Linea", "Equipos Puntos", "Luminarias", "Clientes", "Medidores",
            "Concesiones", "Direcciones", "Empalmes"};

    //public boolean[] listadoChecked = {false, false, false, false, false, false, false, false, false, false, false,false,false,false};
    //ArrayList<Boolean> fool = new ArrayList<Boolean>();
   // boolean[] fool = new boolean[];
    //ArrayList<Boolean> fool = new ArrayList<Boolean>();
    public boolean fool[] = {false, false, false, false, false, false, false, false, false, false, false, false,false,false,false};

            //(false, false, false, false, false, false, false, false, false, false, false,false,false,false);

    //url para feature layers
    /*String fea_urlSubDistribucion, fea_urlSSEE, fea_urlMT, fea_urlBT, fea_urlAP, fea_urlEquipos, fea_urlPostes, fea_urlLuminarias, fea_urlClientes,
            fea_urlEmpalmes, fea_urlTramos, fea_urlMapaBaseCHQ, fea_urlAlimentadores;*/

    //url para dinamyc layers
    String  din_urlMapaBase, din_urlEquiposPunto, din_urlEquiposLinea, din_urlTramos, din_urlNodos , din_urlLuminarias, din_urlClientes, din_urlConcesiones, din_urlMedidores, din_urlDirecciones ;


    //Set bing Maps
    String BingKey = "Asrn2IMtRwnOdIRPf-7q30XVUrZuOK7K2tzhCACMg7QZbJ4EPsOcLk6mE9-sNvUe";
    final BingMapsLayer mAerialBaseMaps = new BingMapsLayer(BingKey, BingMapsLayer.MapStyle.AERIAL);
    final BingMapsLayer mAerialWLabelBaseMaps = new BingMapsLayer(BingKey, BingMapsLayer.MapStyle.AERIAL_WITH_LABELS);
    final BingMapsLayer mRoadBaseMaps = new BingMapsLayer(BingKey, BingMapsLayer.MapStyle.ROAD);

   // ArcGISFeatureLayer LyAlimentadores, LySubDist, LyMT, LyBT, LyAP, LyEquipos, LyPostes, LyLuminarias, LyClientes, LyEmpalmes;
    ArcGISDynamicMapServiceLayer LySED, LySSEE, LySALIDAALIM, LyREDMT, LyREDBT , LyREDAP, LyPOSTES, LyEQUIPOSLINEA,LyEQUIPOSPTO, LyLUMINARIAS, LyCLIENTES, LyMEDIDORES, LyCONCESIONES,LyDIRECCIONES,LyEMPALMES,LyMapabase;



    //set Extent inicial
    Polygon mCurrentMapExtent = null;
    //Declara String de Layer
    String featureServiceURL;
    String LayerTramosURL;
    String LayerBaseChqURL;
    //Declara Tipo de Layer
    ArcGISFeatureLayer LayerAlimentadores;
    ArcGISDynamicMapServiceLayer LayerTramos;
    ArcGISDynamicMapServiceLayer LyerMapaChq;

    //Sets
    ArrayList myLayers = new ArrayList();
    private int choices;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        /*Get Credenciales String*/
        Bundle bundle = getIntent().getExtras();
        usuar2 = bundle.getString("usuarioLogin2");
        passw2 = bundle.getString("passwordLogin2");


        //Set Credenciales
        setCredenciales(usuar2, passw2);


        //Set Mapa
        setMap(R.id.map, 0xffffff, 0xffffff, 10, 10, true, true);

        setLayersURL(this.getResources().getString(R.string.url_Mapabase), "MAPABASE");
        setLayersURL(this.getResources().getString(R.string.url_token), "TOKENSRV");
        setLayersURL(this.getResources().getString(R.string.url_EquiposLinea), "EQUIPOS_LINEA");
        setLayersURL(this.getResources().getString(R.string.url_TRAMOS),"TRAMOS");
        setLayersURL(this.getResources().getString(R.string.url_EquiposPTO), "EQUIPOS_PTO");
        setLayersURL(this.getResources().getString(R.string.url_Nodos), "NODOS");
        setLayersURL(this.getResources().getString(R.string.url_Luminarias), "LUMINARIAS");
        setLayersURL(this.getResources().getString(R.string.url_Clientes), "CLIENTES");
        setLayersURL(this.getResources().getString(R.string.url_Concesiones), "CONCESIONES");
        setLayersURL(this.getResources().getString(R.string.url_Direcciones), "DIRECCIONES");
        setLayersURL(this.getResources().getString(R.string.url_medidores), "MEDIDORES");


        //Agrega layers dinámicos.
        addLayersToMap(credenciales, "DYNAMIC", "SED", din_urlEquiposPunto, null, false);
       /* addLayersToMap(credenciales, "DYNAMIC", "SSEE", din_urlEquiposPunto, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "SALIDAALIM", din_urlEquiposPunto, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "REDMT", din_urlTramos, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "REDBT", din_urlTramos, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "REDAP", din_urlTramos, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "POSTES", din_urlNodos, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "EQUIPOS_LINEA", din_urlEquiposLinea, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "EQUIPOS_PTO", din_urlEquiposPunto, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "LUMINARIAS", din_urlLuminarias, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "CLIENTES", din_urlClientes, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "MEDIDORES", din_urlMedidores, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "CONCESIONES", din_urlConcesiones, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "DIRECCIONES", din_urlDirecciones, null, false);
        addLayersToMap(credenciales, "DYNAMIC", "EMPALMES", din_urlClientes, null, false);*/

        //Añade Layer al Mapa
        myMapView.addLayer(mRoadBaseMaps, 0);
        myMapView.addLayer(LySED,1);
        /*myMapView.addLayer(LySSEE,2);
        myMapView.addLayer(LySALIDAALIM,3);
        myMapView.addLayer(LyREDMT,4);
        myMapView.addLayer(LyREDBT,5);
        myMapView.addLayer(LyREDAP,6);
        myMapView.addLayer(LyPOSTES,7);
        myMapView.addLayer(LyEQUIPOSLINEA,8);
        myMapView.addLayer(LyEQUIPOSPTO,9);
        myMapView.addLayer(LyLUMINARIAS,10);
        myMapView.addLayer(LyCLIENTES,11);
        myMapView.addLayer(LyMEDIDORES,12);
        myMapView.addLayer(LyCONCESIONES,13);
        myMapView.addLayer(LyDIRECCIONES,14);
        myMapView.addLayer(LyEMPALMES,15);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        boolean retVal = false;
        // Get the current map directly from this activity.
        MapView currentMap = (MapView) this.findViewById(R.id.map);
        String mapUrl = null;

        switch (item.getItemId()) {
            case R.id.Road:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                myMapView.removeLayer(0);
                myMapView.addLayer(mRoadBaseMaps, 0);
                return true;
            case R.id.Aerial:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                myMapView.removeLayer(0);
                myMapView.addLayer(mAerialBaseMaps, 0);
                return true;
            
            case R.id.AerialWithLabel:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                myMapView.removeLayer(0);
                myMapView.addLayer(mAerialWLabelBaseMaps, 0);
                return true;
            case R.id.Chilquinta:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                myMapView.removeLayer(0);
                addLayersToMap(credenciales, "DYNAMIC", "MAPABASECHQ", din_urlMapaBase, null, true);
                myMapView.addLayer(LyMapabase, 0);
                return true;

            case R.id.menu_capas:
                if (item.isChecked()) {
                    item.setChecked(false);

                    alertMultipleChoiceItems();

                    item.setChecked(false);
                } else item.setChecked(true);
                return true;



            default:
                retVal = super.onOptionsItemSelected(item);
                break;
        }

        return retVal;
    }

    public void setMap(int idMapa, int color1, int color2 ,int gridSize, int gridLine, boolean logoVisible, boolean wrapAround ){
        myMapView = (MapView) findViewById(idMapa);
        //Set color de fondo mapa base
        myMapView.setMapBackground(color1, color2, gridSize, gridLine);
        //Set logo Esri
        myMapView.setEsriLogoVisible(logoVisible);

        myMapView.enableWrapAround(wrapAround);
    }

    public void setCredenciales(String usuario , String password) {
        credenciales = new UserCredentials();
        credenciales.setUserAccount(usuario, password);
    }



    public void alertMultipleChoiceItems(){

        for (int i = 0; i < listadoCapas.length ; i++) {

            for (int a = 0; a < mSelectedItems.size() ; a++) {

                if (Arrays.asList(listadoCapas).indexOf(listadoCapas[i]) == mSelectedItems.get(a)){
                    System.out.println("true");
                    fool[i] = true;
                } else{
                    System.out.println("False");
                    //fool[i]= false;
                }
                break;

            }
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);

        // set the dialog title
        builder.setTitle("Capas")

                // specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive call backs when items are selected
                // R.array.choices were set in the resources res/values/strings.xml
                .setMultiChoiceItems(listadoCapas, fool, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            // if the user checked the item, add it to the selected items
                            mSelectedItems.add(which);

                        }

                        else if (mSelectedItems.contains(which)) {
                            // else if the item is already in the array, remove it
                            mSelectedItems.remove(Integer.valueOf(which));
                        }

                        // you can also add other codes here,
                        // for example a tool tip that gives user an idea of what he is selecting
                        // showToast("Just an example description.");
                    }

                })

                        // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // user clicked OK, so save the mSelectedItems results somewhere
                        // here we are trying to retrieve the selected items indices
                        String selectedIndex = "";
                        setLayerOff();

                        for (Integer i : mSelectedItems) {
                            selectedIndex += i + ", ";
                            switch (id) {
                                //token srv
                                case 0:
                                    LySED.setVisible(true);
                                    break;
                                case 1:
                                    LySSEE.setVisible(true);
                                    break;
                                case 2:
                                    LySALIDAALIM.setVisible(true);
                                    break;
                                case 3:
                                    LyREDMT.setVisible(true);
                                    break;
                                case 4:
                                    LyREDBT.setVisible(true);
                                    break;
                                case 5:
                                    LyREDAP.setVisible(true);
                                    break;
                                case 6:
                                    LyPOSTES.setVisible(true);
                                    break;
                                case 7:
                                    LyEQUIPOSLINEA.setVisible(true);
                                    break;
                                case 8:
                                    LyEQUIPOSPTO.setVisible(true);
                                    break;
                                case 9:
                                    LyLUMINARIAS.setVisible(true);
                                    break;
                                case 10:
                                    LyCLIENTES.setVisible(true);
                                    break;
                                case 11:
                                    LyMEDIDORES.setVisible(true);
                                    break;
                                case 12:
                                    LyCONCESIONES.setVisible(true);
                                    break;
                                case 13:
                                    LyDIRECCIONES.setVisible(true);
                                    break;
                                case 14:
                                    LyEMPALMES.setVisible(true);
                                    break;
                            }
                        }


                        Toast.makeText(myMapView.getContext(),
                                "Selected index: " + selectedIndex,
                                Toast.LENGTH_SHORT).show();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                })

                .show();

    }

    public void setLayerOff(){
        LySED.setVisible(false);
      /*  LySSEE.setVisible(false);
        LySALIDAALIM.setVisible(false);
        LyREDMT.setVisible(false);
        LyREDBT.setVisible(false);
        LyREDAP.setVisible(false);
        LyPOSTES.setVisible(false);
        LyEQUIPOSLINEA.setVisible(false);
        LyEQUIPOSPTO.setVisible(false);
        LyLUMINARIAS.setVisible(false);
        LyCLIENTES.setVisible(false);
        LyMEDIDORES.setVisible(false);
        LyCONCESIONES.setVisible(false);
        LyDIRECCIONES.setVisible(false);
        LyEMPALMES.setVisible(false);*/
    }

    public void setLayersURL(String layerURL, String tipo){
        switch (tipo){
            case "MAPABASE":
                din_urlMapaBase= layerURL;
                break;
            //token srv
            case "TOKENSRV":
                urlToken = layerURL;
                break;
            case "EQUIPOS_LINEA":
                din_urlEquiposLinea = layerURL;
                break;
            case "TRAMOS":
                din_urlTramos = layerURL;
                break;
            case "EQUIPOS_PTO":
                din_urlEquiposPunto = layerURL;
                break;
            case "NODOS":
                din_urlNodos= layerURL;
                break;
            case "LUMINARIAS":
                din_urlLuminarias= layerURL;
                break;
            case "CLIENTES":
                din_urlClientes= layerURL;
                break;
            case "DIRECCIONES":
                din_urlDirecciones= layerURL;
                break;
            case "MEDIDORES":
                din_urlMedidores= layerURL;
                break;
            case "CONCESIONES":
                din_urlConcesiones= layerURL;
                break;
            default:
                Toast.makeText(MapActivity.this,"Problemas inicializando layers url",Toast.LENGTH_SHORT);
                break;
        }
    }


    public void  addLayersToMap(UserCredentials credencial, String tipoLayer, String nombreCapa, String url, String mode, boolean visibilidad){

        // tipo layer feature
        if(tipoLayer=="FEATURE"){

            switch (nombreCapa){
                case "ALIMENTADORES":

                    if(mode=="SNAPSHOT"){
                       // LyAlimentadores = new ArcGISFeatureLayer(url, ArcGISFeatureLayer.MODE.SNAPSHOT, credencial);
                    }else if(mode=="ONDEMAND"){
                       // LyAlimentadores = new ArcGISFeatureLayer(url, ArcGISFeatureLayer.MODE.ONDEMAND, credencial);
                    }else if(mode=="SELECTION") {
                      //  LyAlimentadores = new ArcGISFeatureLayer(url, ArcGISFeatureLayer.MODE.SELECTION, credencial);
                    }else{
                        Toast.makeText(MapActivity.this,"FeatureLayer debe tener un modo.",Toast.LENGTH_SHORT);
                        return;
                    }
                    break;

                case "REDMT":
                   // LyMT = new ArcGISFeatureLayer(url,ArcGISFeatureLayer.MODE.ONDEMAND,credencial);
                    //LyMT.setVisible(visibilidad);
                    break;
                default:
                    Toast.makeText(MapActivity.this,"Problemas agregando layers url",Toast.LENGTH_SHORT);
                    break;
            }

            //tipo layer dinámico: mode = null
        }else{
            if(mode != null){
                Toast.makeText(MapActivity.this,"Layer dinámico no tiene mode, debe ser null",Toast.LENGTH_SHORT);
            }else {
                switch (nombreCapa) {
                    case "SED":
                        int array1[]; //declaracion arreglo de tipo numerico
                        array1 = new int[1];
                        array1[0] = 1;
                        LySED = new ArcGISDynamicMapServiceLayer(url,array1,credencial);
                        LySED.setVisible(visibilidad);
                        break;
                    case "SSEE":
                        int array2[]; //declaracion arreglo de tipo numerico
                        array2 = new int[1];
                        array2[0] = 0;
                        LySSEE = new ArcGISDynamicMapServiceLayer(url,array2,credencial);
                        LySSEE.setVisible(false);
                        break;
                    case "REDMT":
                        int array3[]; //declaracion arreglo de tipo numerico
                        array3 = new int[1];
                        array3[0] = 0;
                        LyREDMT = new ArcGISDynamicMapServiceLayer(url,array3,credencial);
                        LyREDMT.setVisible(visibilidad);
                        break;
                    case "REDBT":
                        int array4[]; //declaracion arreglo de tipo numerico
                        array4 = new int[1];
                        array4[0] = 1;
                        LyREDBT = new ArcGISDynamicMapServiceLayer(url,array4,credencial);
                        LyREDBT.setVisible(visibilidad);
                        break;
                    case "REDAP":
                        int array5[]; //declaracion arreglo de tipo numerico
                        array5 = new int[1];
                        array5[0] = 2;
                        LyREDAP = new ArcGISDynamicMapServiceLayer(url,array5,credencial);
                        LyREDAP.setVisible(visibilidad);
                        break;
                    case "POSTES":
                        int array6[]; //declaracion arreglo de tipo numerico
                        array6 = new int[1];
                        array6[0] = 2;
                        LyPOSTES = new ArcGISDynamicMapServiceLayer(url,array6,credencial);
                        LyPOSTES.setVisible(visibilidad);
                        break;
                    case "EQUIPOS_LINEA":
                        int array7[]; //declaracion arreglo de tipo numerico
                        array7 = new int[1];
                        array7[0] = 0;
                        LyEQUIPOSLINEA = new ArcGISDynamicMapServiceLayer(url,array7,credencial);
                        LyEQUIPOSLINEA.setVisible(visibilidad);
                        break;
                    case "EQUIPOS_PTO":
                        int array8[]; //declaracion arreglo de tipo numerico
                        array8 = new int[1];
                        array8[0] = 3;
                        LyEQUIPOSPTO = new ArcGISDynamicMapServiceLayer(url,array8,credencial);
                        LyEQUIPOSPTO.setVisible(visibilidad);
                        break;
                    case "LUMINARIAS":
                        int array9[]; //declaracion arreglo de tipo numerico
                        array9 = new int[1];
                        array9[0] = 0;
                        LyLUMINARIAS = new ArcGISDynamicMapServiceLayer(url,array9,credencial);
                        LyLUMINARIAS.setVisible(visibilidad);
                        break;
                    case "CLIENTES":
                        int array10[]; //declaracion arreglo de tipo numerico
                        array10 = new int[1];
                        array10[0] = 0;
                        LyCLIENTES = new ArcGISDynamicMapServiceLayer(url,array10,credencial);
                        LyCLIENTES.setVisible(visibilidad);
                        break;
                    case "MEDIDORES":
                        int array11[]; //declaracion arreglo de tipo numerico
                        array11 = new int[1];
                        array11[0] = 0;
                        LyMEDIDORES = new ArcGISDynamicMapServiceLayer(url,array11,credencial);
                        LyMEDIDORES.setVisible(visibilidad);
                        break;
                    case "CONCESIONES":
                        LyCONCESIONES = new ArcGISDynamicMapServiceLayer(url,null,credencial);
                        LyCONCESIONES.setVisible(visibilidad);
                        break;
                    case "DIRECCIONES":
                        LyDIRECCIONES = new ArcGISDynamicMapServiceLayer(url,null,credencial);
                        LyDIRECCIONES.setVisible(visibilidad);
                        break;
                    case "EMPALMES":
                        int array14[]; //declaracion arreglo de tipo numerico
                        array14 = new int[1];
                        array14[0] = 1;
                        LyEMPALMES = new ArcGISDynamicMapServiceLayer(url,array14,credencial);
                        LyEMPALMES.setVisible(visibilidad);
                        break;
                    default:
                        Toast.makeText(MapActivity.this, "Problemas agregando layers dinámicos.", Toast.LENGTH_SHORT);
                        break;
                }
            }
        }

        //CaMbios en el mapa
        myMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (STATUS.LAYER_LOADED == status) {
                    myMapView.setExtent(mCurrentMapExtent);

                }

                if (status == STATUS.LAYER_LOADING_FAILED) {
                    // Check if a layer is failed to be loaded due to security
                    if ((status.getError()) instanceof EsriSecurityException) {
                        EsriSecurityException securityEx = (EsriSecurityException) status
                                .getError();
                        if (securityEx.getCode() == EsriSecurityException.AUTHENTICATION_FAILED)
                            Toast.makeText(myMapView.getContext(),
                                    "Authentication Failed! Resubmit!",
                                    Toast.LENGTH_SHORT).show();
                        else if (securityEx.getCode() == EsriSecurityException.TOKEN_INVALID)
                            Toast.makeText(myMapView.getContext(),
                                    "Invalid Token! Resubmit!",
                                    Toast.LENGTH_SHORT).show();
                        else if (securityEx.getCode() == EsriSecurityException.TOKEN_SERVICE_NOT_FOUND)
                            Toast.makeText(myMapView.getContext(),
                                    "Token Service Not Found! Resubmit!",
                                    Toast.LENGTH_SHORT).show();
                        else if (securityEx.getCode() == EsriSecurityException.UNTRUSTED_SERVER_CERTIFICATE)
                            Toast.makeText(myMapView.getContext(),
                                    "Untrusted Host! Resubmit!",
                                    Toast.LENGTH_SHORT).show();

                        if (o instanceof ArcGISFeatureLayer) {
                            // Set user credential through username and password
                            UserCredentials creds = new UserCredentials();
                            creds.setUserAccount(usuar2, passw2);

                            LyMapabase.reinitializeLayer(creds);
                            LySED.reinitializeLayer(creds);
                            LySSEE.reinitializeLayer(creds);
                            LySALIDAALIM.reinitializeLayer(creds);
                            LyREDMT.reinitializeLayer(creds);
                            LyREDBT.reinitializeLayer(creds);
                            LyREDAP.reinitializeLayer(creds);
                            LyPOSTES.reinitializeLayer(creds);
                            LyEQUIPOSLINEA.reinitializeLayer(creds);
                            LyEQUIPOSPTO.reinitializeLayer(creds);
                            LyLUMINARIAS.reinitializeLayer(creds);
                            LyCLIENTES.reinitializeLayer(creds);
                            LyMEDIDORES.reinitializeLayer(creds);
                            LyCONCESIONES.reinitializeLayer(creds);
                            LyDIRECCIONES.reinitializeLayer(creds);
                            LyEMPALMES.reinitializeLayer(creds);
                        }
                    }
                }
            }
        });

    }

}

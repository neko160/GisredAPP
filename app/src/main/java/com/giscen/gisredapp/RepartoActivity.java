package com.giscen.gisredapp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.internal.jni.CoreRequest;
import com.esri.arcgisruntime.internal.jni.fe;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.giscen.gisredapp.entity.RepartoClass;
import com.giscen.gisredapp.util.RepartoSQLiteHelper;
import com.giscen.gisredapp.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RepartoActivity extends AppCompatActivity {

    private static final String TAG = RepartoActivity.class.getSimpleName();
    //GIS
    private MapView myMapView;

    private ServiceFeatureTable mServiceFeatureTable;
    FeatureLayer featureLayerBoleta;

    String usuar,passw,modulo,empresa;


    boolean bGpsActive = false;
    LocationDisplay ldm;
    private Point oUbicActual;
    public static Point mlocation = null;

    ArrayList<Integer> myDeletes;

    ArrayList<RepartoClass> arrayDatos;
    //Fin GIS
    //BD mas contadores
    public RepartoSQLiteHelper sqlReparto;
    final String dbName = "DbRepartos.db";

    int iContRep = 0;
    int iContRepSesion = 0;
    //FIn BD + contadores
    //Android
    //Permission name
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};


    //Fin Android

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparto);

        Toolbar toolbar =(Toolbar) findViewById( R.id.toolbarReparto );
        setSupportActionBar( toolbar );
        //    myMapView = findViewById(R.id.mapView);

        // create a map with streets basemap
        // ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, 40.0, -95.0, 4);

        // create service feature table from URL
        mServiceFeatureTable = new ServiceFeatureTable(getString(R.string.srv_repàrto_desa));

        // create a feature layer from table
        featureLayerBoleta = new FeatureLayer(mServiceFeatureTable);

        /*Get Credenciales String*/
        Bundle bundle = getIntent().getExtras();
        usuar = bundle.getString( "usuario" );
        passw = bundle.getString( "password" );
        modulo = bundle.getString( "modulo" );
        empresa = bundle.getString( "empresa" );

        // add the layer to the map
        //map.getOperationalLayers().add(featureLayerBoleta);
        myDeletes = new ArrayList<Integer>();
        // add a listener to the MapView to detect when a user has performed a single tap to add a new feature to
        // the service feature table
     /*   mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override public boolean onSingleTapConfirmed(MotionEvent event) {
                // create a point from where the user clicked
                android.graphics.Point point = new android.graphics.Point((int) event.getX(), (int) event.getY());

                // create a map point from a point
                Point mapPoint = mMapView.screenToLocation(point);

                // add a new feature to the service feature table
                addFeature(mapPoint, mServiceFeatureTable);
                return super.onSingleTapConfirmed(event);
            }
        });*/

        if(Build.VERSION.SDK_INT >= 23 ){
            verificaPermisos();
        }else{
            startGPS();
        }

        sqlReparto = new RepartoSQLiteHelper( RepartoActivity.this,dbName,null,2 );

        // Edit text Registro Boletas
        EditText txtListen = (EditText) findViewById( R.id.txtListen );
        txtListen.setEnabled( bGpsActive );

        if (!txtListen.hasFocus()) txtListen.requestFocus();

        txtListen.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().contains( "\n" ) || s.toString().length() == RepartoClass.length_code){
                    guardarRegistro(s.toString().trim());
                    s.clear();
                }
            }
        } );


        // set map to be displayed in map view
        loadMap();

        final FloatingActionButton btnGps = findViewById( R.id.action_gps );
        btnGps.setFocusable( false );
        btnGps.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
                if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
                    ldm.setAutoPanMode( (LocationDisplay.AutoPanMode.RECENTER) );
                    if(!ldm.isStarted());
                    ldm.startAsync();
                }
            }
        } );

        readCountData();
        updDashboard();

        Timer timer = new Timer();
        timer.schedule( new TimerTask() {
            @Override
            public void run() {
                runOnUiThread( () -> {
                    if(iContRep > 0 ){
                        Toast.makeText( getApplicationContext(),"Sincronizando datos...",Toast.LENGTH_SHORT ).show();
                        readData();
                        if(arrayDatos.size() > 0){

                            //enviarDatos(mServiceFeatureTable);
                            enviarDatos();
                        }
                    }
                } );
            }
        },0,120000 );

    }


    //private void enviarDatos(ServiceFeatureTable featureTable) {
    private void enviarDatos() {
        ServiceFeatureTable featureTable;

        featureTable = mServiceFeatureTable;

        featureTable.loadAsync();

        featureTable.addDoneLoadingListener( () -> {

            for(RepartoClass rep : arrayDatos){
                Integer myId = rep.getId();

                Map<String,Object> attributes = new HashMap<>();
                attributes.put("nis",rep.getNis());
                attributes.put("valor_captura",rep.getCodigo());
                attributes.put("empresa",empresa);
                attributes.put("modulo",rep.getTipo());
                attributes.put("fecha",rep.getFecha());

                String oTipo;
                oTipo  = rep.getTipo();

                Point oUbicacion = new Point( rep.getX(),rep.getY(),SpatialReference.create(32719));

                switch (oTipo){
                    case "BOL":
                        // creates a new feature using default attributes and point

                        Feature feature = featureTable.createFeature(attributes, oUbicacion);


                        // check if feature can be added to feature table
                        if (featureTable.canAdd()) {
                            // add the new feature to the feature table and to server
                            myDeletes.add( myId );
                            featureTable.addFeatureAsync(feature).addDoneListener(() -> applyEdits(featureTable));

                        } else {
                            runOnUiThread(() -> logToUser(true, getString(R.string.error_cannot_add_to_feature_table)));
                        }
                        break;
                    default:
                        throw new IllegalStateException( "Unexpected value: " + oTipo );
                }
            }
        } );

    }

    private void applyEdits(ServiceFeatureTable featureTable) {
        // apply the changes to the server
        final ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
        editResult.addDoneListener(() -> {
            try {

                List<FeatureEditResult> editResults = editResult.get();
                // check if the server edit was successful
                if (editResults != null && !editResults.isEmpty()) {
                    if (!editResults.get(0).hasCompletedWithErrors()) {
                        runOnUiThread(() -> logToUser(false, getString(R.string.feature_added)));
                        for (Integer del : myDeletes) {
                            deleteData( del );
                        }
                    } else {
                        throw editResults.get(0).getError();
                    }

                }
            } catch (InterruptedException | ExecutionException e) {
                runOnUiThread(() -> logToUser(true, getString(R.string.error_applying_edits, e.getCause().getMessage())));
            }
        });

    }

    public void deleteData(int id){
        SQLiteDatabase db = sqlReparto.getWritableDatabase();
        db.delete("repartos","id=" + id, null);
        db.close();
        Log.d("cuanto Va: " , "Por aca Paso: " +  String.valueOf( iContRep ) );
        if(iContRep > 0 ){
            iContRep--;
            updDashboard();
        }
    }

    private void verificaPermisos() {
        if(ContextCompat.checkSelfPermission( RepartoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale( RepartoActivity.this,Manifest.permission.ACCESS_FINE_LOCATION )){

            }else{
                ActivityCompat.requestPermissions( RepartoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Util.REQUEST_ACCESS_FINE_LOCATION );;
            }
        }else{
            startGPS();
        }
    }

    private void startGPS() {
        bGpsActive = verificaGPS();
    }

    private boolean verificaGPS() {
        LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
        if(!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
            alertNoGPS();
            return false;
        }
        return  true;
    }

    private void alertNoGPS() {
        final android.app.AlertDialog alertGps;
        final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alertGps = builder.create();
        alertGps.show();

    }


    private void readData() {
        SQLiteDatabase db = sqlReparto.getReadableDatabase();
        String[] sValues = {"id","codigo","x","y","fecha","tipo"};
        Cursor cData= db.query( "repartos",sValues,null,null,null,null,null );
        arrayDatos = new ArrayList<>();

        if(cData != null && cData.getCount() >0){
            iContRep = cData.getCount();

            cData.moveToFirst();

            do{
                RepartoClass oRep = new RepartoClass( cData.getInt( 0 ), cData.getString( 1 ), cData.getDouble( 2 ), cData.getDouble( 3 ), cData.getString( 4 ), cData.getString( 5 ) );
                arrayDatos.add(oRep);
            }while (cData.moveToNext());
            cData.close();
        }
    }

    private void readCountData() {
        SQLiteDatabase db = sqlReparto.getReadableDatabase();
        String[] sValues = {"id","codigo","x","y","fecha","tipo"};
        Cursor cData = db.query("repartos",sValues,null,null,null,null,null);

        if(cData !=null && cData.getCount() >=0){
            iContRep = cData.getCount();
            cData.close();
        }
        db.close();
    }

    private void loadMap() {
        myMapView = (MapView) findViewById( R.id.mapView );
        myMapView.setAttributionTextVisible( false );


        ArcGISMap map = new ArcGISMap( SpatialReference.create( 3857 ) );
        // create a map with streets basemap
        // ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, 40.0, -95.0, 4);
        map = new ArcGISMap( Basemap.Type.DARK_GRAY_CANVAS_VECTOR,40,-99,4);
        map.getOperationalLayers().add(featureLayerBoleta);

        map.addLoadStatusChangedListener( new LoadStatusChangedListener() {
            @Override
            public void loadStatusChanged(LoadStatusChangedEvent loadStatusChangedEvent) {
                String mapLoadStatus;
                mapLoadStatus = loadStatusChangedEvent.getNewLoadStatus().name();

                switch (mapLoadStatus){
                    case "LOADING":
                        break;
                    case "FAILED_TO_LOAD":
                        break;
                    case "NOT_LOADED":
                        break;
                    case "LOADED":
                        setupLocationDisplay();
                        break;
                }
            }
        } );
        myMapView.setMap( map );
    }

    private void setupLocationDisplay() {
        ldm = myMapView.getLocationDisplay();
        ldm.addDataSourceStatusChangedListener( dataSourceStatusChangedEvent -> {
            if(dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null){
                return;
            }
            int requestPermissionsCode = 2;
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

            if(!(ContextCompat.checkSelfPermission( RepartoActivity.this,requestPermissions[0] )== PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission( RepartoActivity.this ,requestPermissions[1]) == PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions( RepartoActivity.this ,requestPermissions,requestPermissionsCode );
            }else{
                String message = String.format( "Error in DataSourceStatusChangedListener: %s" ,
                        dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
            }

        } );

        ldm.setAutoPanMode( LocationDisplay.AutoPanMode.RECENTER );
        ldm.addLocationChangedListener( new LocationDisplay.LocationChangedListener(){

            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                try {
                    mlocation = new Point( locationChangedEvent.getLocation().getPosition().getX(),locationChangedEvent.getLocation().getPosition().getY(), SpatialReferences.getWgs84() );
                    oUbicActual = (Point) GeometryEngine.project( mlocation ,SpatialReference.create(32719));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } );
        ldm.startAsync();
    }

    private void guardarRegistro(String sValue) {
        if(RepartoClass.valCode( sValue ) && !sValue.isEmpty()){
            if(insertData(sValue)){
                iContRep++;
                iContRepSesion++;
                updDashboard();
            }else{
                Toast.makeText( RepartoActivity.this,"Error: registro " + sValue + " no guardado", Toast.LENGTH_SHORT).show();
            }
        }else{
            if(!sValue.isEmpty()){
                Toast.makeText( RepartoActivity.this, "Lectura con valor no válido: " + sValue, Toast.LENGTH_SHORT).show();
                alertFail();
            }
        }
    }

    private void alertFail() {
        ToneGenerator tgFail = new ToneGenerator( AudioManager.STREAM_NOTIFICATION,300 );
        tgFail.startTone( ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE,200 );

        Vibrator v = (Vibrator) getSystemService( Context.VIBRATOR_SERVICE );
        v.vibrate( 1000 );
    }

    private void updDashboard() {
        String sTextCont = getResources().getString( R.string.tvCont );
        sTextCont = sTextCont + " " + iContRep;

        TextView sTxtContador = (TextView) findViewById( R.id.tvContador );

        sTxtContador.setText( sTextCont );

        String sTextSesion = getResources().getString( R.string.tvContSesion );
        sTextSesion = sTextSesion + " " + iContRepSesion;

        TextView txtContSesion = (TextView ) findViewById( R.id.tvContadorSesion );
        txtContSesion.setText( sTextSesion );
    }

    private boolean insertData(String sValue) {
        SQLiteDatabase db = sqlReparto.getWritableDatabase();
        long nIns = -1;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYY HH:mm:ss");
        String currenDateTime = dateFormat.format( new Date());

        String oVal = sValue.substring( 8,10 );
        String oTipo;

        if(oVal.matches( "[0-9]*" )){
            oTipo = "BOL";
        }else{
            oTipo = sValue.substring( 8,10 );
        }
        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put( "codigo", sValue );
            valores.put("x",myDecimal(oUbicActual.getX()));
            valores.put( "y",myDecimal( oUbicActual.getY() ) );
            valores.put("fecha",currenDateTime);
            valores.put( "tipo",oTipo );

            nIns = db.insert( "repartos",null,valores );
            db.close();
        }
        return nIns > 0;
    }

    private Double myDecimal(Double myNumber) {
        Double myValor = null;
        DecimalFormat ft = new DecimalFormat("#.000000");
        myValor = Double.parseDouble( ft.format( myNumber ) );
        return myValor;
    }

    /**
     * Adds a new Feature to a ServiceFeatureTable and applies the changes to the
     * server.
     *
     * @param mapPoint     location to add feature
     * @param featureTable service feature table to add feature
     */
  /*  private void addFeature(Point mapPoint, final ServiceFeatureTable featureTable) {

        // create default attributes for the feature
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("typdamage", "Destroyed");
        attributes.put("primcause", "Earthquake");

        // creates a new feature using default attributes and point
        Feature feature = featureTable.createFeature(attributes, mapPoint);

        // check if feature can be added to feature table
        if (featureTable.canAdd()) {
            // add the new feature to the feature table and to server
            featureTable.addFeatureAsync(feature).addDoneListener(() -> applyEdits(featureTable));
        } else {
            runOnUiThread(() -> logToUser(true, getString(R.string.error_cannot_add_to_feature_table)));
        }
    }*/

    /**
     * Sends any edits on the ServiceFeatureTable to the server.
     *
     * @param featureTable service feature table
     */
  /*  private void applyEdits2(ServiceFeatureTable featureTable) {

        // apply the changes to the server
        final ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
        editResult.addDoneListener(() -> {
            try {
                List<FeatureEditResult> editResults = editResult.get();
                // check if the server edit was successful
                if (editResults != null && !editResults.isEmpty()) {
                    if (!editResults.get(0).hasCompletedWithErrors()) {
                        runOnUiThread(() -> logToUser(false, getString(R.string.feature_added)));
                    } else {
                        throw editResults.get(0).getError();
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                runOnUiThread(() -> logToUser(true, getString(R.string.error_applying_edits, e.getCause().getMessage())));
            }
        });
    }*/

    /**
     * Shows a Toast to user and logs to logcat.
     *
     * @param isError whether message is an error. Determines log level.
     * @param message message to display
     */
    private void logToUser(boolean isError, String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (isError) {
            Log.e(TAG, message);
        } else {
            Log.d(TAG, message);
        }
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                if(iContRep > 0 ){
                    Toast.makeText( getApplicationContext(),"Sincronizando datos...",Toast.LENGTH_SHORT ).show();
                    readData();
                    if(arrayDatos.size() > 0){
                        //enviarDatos(mServiceFeatureTable);
                        enviarDatos();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.reparto_menu,menu );
        return true;
    }

    @Override protected void onResume() {
        super.onResume();
        myMapView.resume();
    }

    @Override protected void onPause() {
        myMapView.pause();
        super.onPause();
    }

    @Override protected void onDestroy() {
        myMapView.dispose();
        super.onDestroy();
    }
}
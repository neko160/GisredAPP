package com.giscen.gisredapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.BingMapsLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.TimeExtent;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.security.AuthenticationChallengeResponse;
import com.esri.arcgisruntime.security.UserCredential;
import com.giscen.gisredapp.entity.RepartoClass;
import com.giscen.gisredapp.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class RepartoActivity1 extends AppCompatActivity {

    public static Point mLocation = null;
    private Point oUbicActual;
    private static final String TAG = LoginActivity.class.getSimpleName();

    MapView myMapView = null;
    LocationDisplay ldm;
    CardView cardView;

    //If the system environment is Android 6.0 or above, it is not enough to add permissions only in manifest, and code is needed to dynamically obtain permissions.
    //Privile  private int requestCode = 2;ge Request Code

    //Permission name
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};
    //INSTANCES
    UserCredential credenciales;
    String usuar, passw, modulo, empresa;

    String din_urlMapaBase;

    private  ServiceFeatureTable mServiceFeatureTable;
    public ServiceFeatureTable mServiceFeatureTable2;
    Feature feature ;


    //Set bing Maps
    String BingKey = "Asrn2IMtRwnOdIRPf-7q30XVUrZuOK7K2tzhCACMg7QZbJ4EPsOcLk6mE9-sNvUe";
    final BingMapsLayer mAerialBaseMaps = new BingMapsLayer( BingMapsLayer.Style.AERIAL, BingKey );
    final BingMapsLayer mAerialWLabelBaseMaps = new BingMapsLayer( BingMapsLayer.Style.HYBRID, BingKey );
    final BingMapsLayer mRoadBaseMaps = new BingMapsLayer( BingMapsLayer.Style.ROAD, BingKey );

    //Sets
    ArrayList<String> arrayWidgets;

    private static final String CLIENT_ID = "runtimelite,1000,rud1103995804,none,FA0RJAY3FPGXKXNCD069";

    EditText txtListen;
    public TextView txtContador;
    TextView txtContSesion;
    int iContRep = 0;
    int iContRepSesion = 0;
    boolean bGpsActive = false;
    boolean bBlueActive = true;

    public RepartoSQLiteHelper sqlReparto;
    final String dbName = "DbRepartos.db";

    ArrayList<RepartoClass> arrayDatos;

    RepartoService mService;
    boolean mBound = false;

    private BluetoothAdapter bAdapter;
    private ArrayList<BluetoothDevice> arrayDevices;
    String domain;

    ArrayList<Integer> myDeletes = new ArrayList<Integer>();

    FeatureLayer featureLayerCarta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_reparto );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbarReparto );
        setSupportActionBar( toolbar );

        ArcGISRuntimeEnvironment.setLicense( CLIENT_ID );
        domain = getResources().getString( R.string.domain );

        //Create a FeatureTable
        mServiceFeatureTable = new ServiceFeatureTable( getString( R.string.srv_repàrto_desa ) );
        mServiceFeatureTable2 = new ServiceFeatureTable( getString( R.string.srv_repàrto_desa2 ) );

        featureLayerCarta = new FeatureLayer( mServiceFeatureTable );

        /*Get Credenciales String*/
        Bundle bundle = getIntent().getExtras();
        usuar = bundle.getString( "usuario" );
        passw = bundle.getString( "password" );
        modulo = bundle.getString( "modulo" );
        empresa = bundle.getString( "empresa" );

        //Crea un intervalo entre primer dia del mes y dia actual
        Calendar oCalendarStart = Calendar.getInstance();
        oCalendarStart.set( Calendar.DAY_OF_MONTH, 1 );
        oCalendarStart.set( Calendar.HOUR, 6 );

        Calendar oCalendarEnd = Calendar.getInstance();
        oCalendarEnd.set( Calendar.HOUR, 23 );

        TimeExtent oTimeInterval = new TimeExtent( oCalendarStart, oCalendarEnd );


        //Set Credenciales
        setCredenciales( usuar, passw );

        if (Build.VERSION.SDK_INT >= 23) verifPermisos();
        else startGPS();

        sqlReparto = new RepartoSQLiteHelper( RepartoActivity1.this, dbName, null, 2 );

        txtContador = (TextView) findViewById( R.id.tvContador );
        txtContSesion = (TextView) findViewById( R.id.tvContadorSesion );

        // Edit text Registro Boletas
        txtListen = (EditText) findViewById( R.id.txtListen );
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
                // Toast.makeText(RepartoActivity.this, "Watcher funciona", Toast.LENGTH_SHORT).show();
                if (s.toString().contains( "\n" ) || s.toString().length() == RepartoClass.length_code) {
                    guardarRegistro( s.toString().trim() );
                    s.clear();
                }
            }
        } );
        loadMap();

        final FloatingActionButton btnGps = findViewById( R.id.action_gps );
        btnGps.setFocusable( false );
        btnGps.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
                if (locationManager.isProviderEnabled( (LocationManager.GPS_PROVIDER) )) {
                    ldm.setAutoPanMode( (LocationDisplay.AutoPanMode.RECENTER) );
                    if (!ldm.isStarted())
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
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        if (iContRep > 0) {
                            Toast.makeText( getApplicationContext(), "Sincronizando datos....", Toast.LENGTH_SHORT ).show();
                            readData();
                            if (arrayDatos.size() > 0) {
                                enviarDatos2();
                            }

                        }
                    }
                } );

            }
        } ,0,120000);

        //FIN On Create********
    }
    private void  enviaReparto(){

        new AuthenticationChallengeResponse( AuthenticationChallengeResponse.Action.CONTINUE_WITH_CREDENTIAL,credenciales );
        mServiceFeatureTable.setCredential( credenciales );

        ArrayList<Integer> myDeletes = new ArrayList<Integer>();
        ServiceFeatureTable featureTable = null;
        ServiceFeatureTable featureTableCarta = null;

        for(RepartoClass rep : arrayDatos){
            Integer MyId = rep.getId();

            Map<String,Object> attributes = new HashMap<>(  );
            attributes.put( "nis", rep.getNis() );
            attributes.put( "valor_captura", rep.getCodigo() );
            attributes.put( "empresa", empresa );
            attributes.put( "modulo", rep.getTipo() );
            attributes.put( "fecha", rep.getFecha() );


            String oTipo;
            oTipo = rep.getTipo();

            Point oUbicacion = new Point( rep.getX(), rep.getY(), SpatialReference.create( 32719 ) );
        }
    }



    private void enviarDatos2() {

        ArrayList<Integer> myDeletes = new ArrayList<>();



        for(RepartoClass rep : arrayDatos){

            Integer MyId = rep.getId();

            Map<String,Object> attributes = new HashMap<>(  );
            attributes.put( "nis", rep.getNis() );
            attributes.put( "valor_captura", rep.getCodigo() );
            attributes.put( "empresa", empresa );
            attributes.put( "modulo", rep.getTipo() );
            attributes.put( "fecha", rep.getFecha() );



            String oTipo;
            oTipo = rep.getTipo();

            Point oUbicacion = new Point( rep.getX(), rep.getY(), SpatialReference.create( 32719 ) );

            switch (oTipo){
                case "BOL":
                    try {

                       //feature = mServiceFeatureTable.createFeature(attributes,oUbicacion);
                        Feature feature = mServiceFeatureTable.createFeature(attributes, oUbicacion);
                       // mServiceFeatureTable.addFeatureAsync( feature );
                        if(mServiceFeatureTable.canAdd()){
                            mServiceFeatureTable.addFeatureAsync( feature ).addDoneListener( () -> applyEdits( mServiceFeatureTable ) );
                            myDeletes.add( MyId );

                        }else{
                            runOnUiThread( () -> logToUser( true,getString(R.string.app_name ) ) );
                        }
                    }catch (Exception e){

                    }
            }


        }

       //applyEdits(featureTableCarta);

    //    mServiceFeatureTable.addFeatureAsync( feature ).addDoneListener( () -> applyEdits( mServiceFeatureTable ) );

    }

    private void applyEdits(ServiceFeatureTable featureTable) {
        final ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
    editResult.addDoneListener( () -> {
        try {
            Log.d( "MyRepar: ", "-------------------Entre en en aappply");
            List<FeatureEditResult> editResults = editResult.get();
            if(editResults != null && !editResults.isEmpty()){
                Log.d( "MyRepar:" , "Entre en apply" );
                if(!editResults.get(0).hasCompletedWithErrors()){
                    runOnUiThread( () -> logToUser( false, getString( R.string.app_name ) ) );
                }else{
                    throw editResults.get( 0 ).getError();
                }
            }else{
                Log.d( "MyRepar:" , "NO hay registros" );
            }
        }catch (InterruptedException | ExecutionException e){

        }
    } );
    }


    private void deleteData(int id) {
        SQLiteDatabase db = sqlReparto.getWritableDatabase();
        db.delete("repartos", "id=" + id, null);
        db.close();

        if (iContRep > 0) {
            iContRep--;
            updDashboard();
        }
    }

    private void logToUser(boolean isError, String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            if (isError) {
                Log.e(TAG, message);
            } else {
                Log.d(TAG, message);
            }

    }

    private void updDashboard() {

        String sTextCont = getResources().getString(R.string.tvCont);
        sTextCont = sTextCont + " " + iContRep;

        txtContador.setText(sTextCont);

        String sTextSesion = getResources().getString(R.string.tvContSesion);
        sTextSesion = sTextSesion + " " + iContRepSesion;

        txtContSesion.setText(sTextSesion);
    }

    private void readData() {
        SQLiteDatabase db = sqlReparto.getReadableDatabase();
        String[] sValues = {"id", "codigo", "x", "y", "fecha", "tipo"};
        Cursor cData = db.query( "repartos", sValues, null, null, null, null, null, null );
        arrayDatos = new ArrayList<>();


        if (cData != null && cData.getCount() > 0) {
            iContRep = cData.getCount();

            cData.moveToFirst();

            do {
                RepartoClass oRep = new RepartoClass( cData.getInt( 0 ), cData.getString( 1 ), cData.getDouble( 2 ), cData.getDouble( 3 ), cData.getString( 4 ), cData.getString( 5 ) );
                arrayDatos.add( oRep );

            } while (cData.moveToNext());

            cData.close();
        }
    }

    private void readCountData() {
        SQLiteDatabase db = sqlReparto.getReadableDatabase();
        String[] sValues = {"id", "codigo", "x", "y","fecha","tipo"};
        Cursor cData = db.query("repartos", sValues, null, null, null, null, null, null);

        if (cData != null && cData.getCount() >= 0) {
            iContRep = cData.getCount();
            cData.close();
        }

        db.close();
    }

    private void guardarRegistro(String sValue) {

        if (RepartoClass.valCode(sValue) && !sValue.isEmpty()) {
            if (insertData(sValue)){
                iContRep++;

                iContRepSesion++;
                updDashboard();
            }
            else
                Toast.makeText( RepartoActivity1.this, "Error: registro " + sValue+ " no guardado", Toast.LENGTH_SHORT).show();
        }
        else {
            if (!sValue.isEmpty()){
                Toast.makeText( RepartoActivity1.this, "Lectura con valor no válido: " + sValue, Toast.LENGTH_SHORT).show();
                alertFail();
            }

        }
    }
    private void alertFail() {
        ToneGenerator tgFail = new ToneGenerator( AudioManager.STREAM_NOTIFICATION, 300);
        tgFail.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 200);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
    }

    private boolean insertData(String sValue) {

        SQLiteDatabase db = sqlReparto.getWritableDatabase();
        long nIns = -1;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String oVal = sValue.substring(8,10);
        String oTipo;

        if(oVal.matches( "[0-9]*")){
            oTipo = "BOL";
        }else{
            oTipo = sValue.substring(8,10);
        }

        if(db != null) {
            ContentValues valores = new ContentValues();
            valores.put("codigo", sValue);
            //valores.put("x", oUbicActual.getX());
            //valores.put("y", oUbicActual.getY());
            valores.put( "x",myDecimal( oUbicActual.getX() ) );
            valores.put( "y",myDecimal( oUbicActual.getY() ) );
            valores.put("fecha",currentDateTime);
            valores.put("tipo",oTipo);

            Log.d( "COORDINATE",String.valueOf( myDecimal( oUbicActual.getX() ) ) );
            Log.d( "COORDINATE",String.valueOf( myDecimal( oUbicActual.getY() ) ) );

            nIns = db.insert("repartos", null, valores);
            db.close();
        }
        return nIns > 0;
    }
    private Double myDecimal(Double mynumber){
        Double myValor= null;

        DecimalFormat ft = new DecimalFormat( "#.000000" );
        myValor =  Double.parseDouble( ft.format( mynumber ) );
        return myValor;
    }

    private void verifPermisos() {
        if (ContextCompat.checkSelfPermission( RepartoActivity1.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale( RepartoActivity1.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions( RepartoActivity1.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Util.REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            //Marcar como encendido
            startGPS();
        }
    }

    private void startGPS() {
        setStateGPS();
    }
    private void setStateGPS() {
        bGpsActive = verifGPS();
        //Marcar estado
    }
    private boolean verifGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertNoGps();
            return false;
        }
        return true;
    }

    private void alertNoGps() {
        final AlertDialog alertGps;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void setCredenciales(String usuar, String passw) {
        credenciales = new UserCredential(domain + usuar,passw);
    }

    private void loadMap() {
        myMapView = (MapView) findViewById(R.id.mapView);
        myMapView.setAttributionTextVisible( false );

        //Create FeatureLayer
      //  FeatureLayer featureLayer = new FeatureLayer(mServiceFeatureTable);
      //  FeatureLayer featureLayer2 = new FeatureLayer(mServiceFeatureTable2);

        ArcGISMap map = new ArcGISMap( SpatialReference.create(3857));
        //Add Layer to the map
        map.getOperationalLayers().add( mRoadBaseMaps );
        map.getOperationalLayers().add(featureLayerCarta);
       // map.getOperationalLayers().add(featureLayer2);

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
        myMapView.setMap(map);
    }

    private void setupLocationDisplay() {
        ldm = myMapView.getLocationDisplay();

        /* ** ADD ** */
        ldm.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {
            if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
                return;
            }

            int requestPermissionsCode = 2;
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            if (!(ContextCompat.checkSelfPermission( RepartoActivity1.this, requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission( RepartoActivity1.this, requestPermissions[1]) == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions( RepartoActivity1.this, requestPermissions, requestPermissionsCode);
            } else {
                String message = String.format("Error in DataSourceStatusChangedListener: %s",
                        dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
                //   Toast.makeText(RepartoActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

        ldm.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        ldm.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                try {
                    mLocation = new Point(locationChangedEvent.getLocation().getPosition().getX(),locationChangedEvent.getLocation().getPosition().getY(), SpatialReferences.getWgs84());
                    oUbicActual = (Point) GeometryEngine.project(mLocation,SpatialReference.create(32719));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ldm.startAsync();
    }

    private class RepartoSQLiteHelper extends SQLiteOpenHelper {

        //Sentencia SQL para crear la tabla de Usuarios
        String sqlCreate = "CREATE TABLE repartos (id INTEGER PRIMARY KEY AUTOINCREMENT, codigo TEXT, x NUMERIC, y NUMERIC, fecha TEXT,tipo TEXT)";

        public RepartoSQLiteHelper(Context contexto, String nombre,
                                   SQLiteDatabase.CursorFactory factory, int version) {
            super(contexto, nombre, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Se ejecuta la sentencia SQL de creación de la tabla
            db.execSQL(sqlCreate);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {

            //Se elimina la versión anterior de la tabla
            db.execSQL("DROP TABLE IF EXISTS repartos");

            //Se crea la nueva versión de la tabla
            db.execSQL(sqlCreate);
        }
    }
}
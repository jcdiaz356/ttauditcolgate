package com.dataservicios.ttauditcolgate;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.*;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.dataservicios.ttauditcolgate.Model.Audit;
import com.dataservicios.ttauditcolgate.SQLite.DatabaseHelper;
import com.dataservicios.ttauditcolgate.app.AppController;
import com.dataservicios.ttauditcolgate.librerias.GlobalConstant;
import com.dataservicios.ttauditcolgate.librerias.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by usuario on 14/01/2015.
 */
public class DetallePdv extends FragmentActivity {
   // private static final String URL_PDVS = "http://www.dataservicios.com/webservice/pdvs.php";
    private GoogleMap map;
    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewGroup linearLayout;
    private Button bt;
    private LocationManager locManager;
    private LocationListener locListener;
    private double latitude ;
    private double longitude ;
    private double lat ;
    private double lon ;
    private Marker MarkerNow;
    private ProgressDialog pDialog;

    private JSONObject params,paramsCordenadas;
    private SessionManager session;
    private String email_user, id_user, name_user;
    private int idPDV, IdRuta, idCompany;
    private String fechaRuta;
    EditText pdvs1,pdvsAuditados1,porcentajeAvance1;
    TextView tvTienda,tvDireccion ,tvRepresentante , tvPDVSdelDia , tvLong, tvLat;
    Button btGuardarLatLong, btCerrarAudit;
    Activity MyActivity = (Activity) this;

    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_pdv);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Detalle de PDV");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
        session = new SessionManager(MyActivity);
        map =((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        pdvs1 = (EditText)  findViewById(R.id.etPDVS);
        pdvsAuditados1 = (EditText)  findViewById(R.id.etPDVSAuditados);
        porcentajeAvance1 = (EditText)  findViewById(R.id.etPorcentajeAvance);
        tvTienda = (TextView)  findViewById(R.id.tvTienda);
        tvDireccion = (TextView)  findViewById(R.id.tvDireccion);
        tvRepresentante = (TextView)  findViewById(R.id.tvRepresentante);
        tvPDVSdelDia = (TextView)  findViewById(R.id.tvPDVSdelDia);
        tvLong = (TextView) findViewById(R.id.tvlogitud);
        tvLat = (TextView) findViewById(R.id.tvLatitud);
        btGuardarLatLong = (Button) findViewById(R.id.btGuardarLatLong);
        btCerrarAudit = (Button) findViewById(R.id.btCerrarAuditoria);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        name_user = user.get(SessionManager.KEY_NAME);
        // email
        email_user = user.get(SessionManager.KEY_EMAIL);
        // id
        id_user = user.get(SessionManager.KEY_ID_USER);

        db = new DatabaseHelper(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);


        btGuardarLatLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paramsCordenadas = new JSONObject();
                try {
                    paramsCordenadas.put("latitud", lat);
                    paramsCordenadas.put("longitud", lon);
                    paramsCordenadas.put("id", idPDV);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                guardarCoordenadas();
            }
        });

        btCerrarAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de cerrar la auditoría: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String strDate = sdf.format(c.getTime());
                        GlobalConstant.fin = strDate;

                        JSONObject paramsCloseAudit = new JSONObject();
                        try {
                            paramsCloseAudit.put("latitud_close", lat);
                            paramsCloseAudit.put("longitud_close", lon);
                            paramsCloseAudit.put("latitud_open", GlobalConstant.latitude_open);
                            paramsCloseAudit.put("longitud_open",  GlobalConstant.latitude_open);
                            paramsCloseAudit.put("tiempo_inicio",  GlobalConstant.inicio);
                            paramsCloseAudit.put("tiempo_fin",  GlobalConstant.fin);
                            paramsCloseAudit.put("tduser", id_user);
                            paramsCloseAudit.put("id", idPDV);
                            paramsCloseAudit.put("idruta", IdRuta);
                            insertaTiemporAuditoria(paramsCloseAudit);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);


            }
        });



        linearLayout = (ViewGroup) findViewById(R.id.lyControles);

        params = new JSONObject();
        Bundle bundle = getIntent().getExtras();
        idPDV= bundle.getInt("idPDV");
        IdRuta= bundle.getInt("idRuta");
        fechaRuta= bundle.getString("fechaRuta");
        tvPDVSdelDia.setText(fechaRuta);



        try {
            params.put("id", idPDV);
            params.put("idRoute", IdRuta);
            //Enviando

            params.put("iduser", id_user);

            //params.put("id_pdv",idPDV);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cargaPdvs();
        //cargarAditorias();
        cargarAditorias();

    }



    private void mostrarPosicion(Location loc) {
        if(loc != null)
        {

            latitude=loc.getLatitude();
            longitude= loc.getLongitude();

            Log.i("Posicion: ", String.valueOf(latitude + " - " + String.valueOf(longitude) + " - "));
        }
        else
        {

            Log.i("SIN Data","No hay datos para mostrar");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private void cargaPdvs(){
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonRoadDetail" ,params,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");


                            if (success == 1) {
//
                                JSONArray ObjJson;
                                ObjJson = response.getJSONArray("roadsDetail");
                                // looping through All Products
                                if(ObjJson.length() > 0) {
                                    for (int i = 0; i < ObjJson.length(); i++) {
                                        try {
                                            JSONObject obj = ObjJson.getJSONObject(i);
                                            tvTienda.setText(obj.getString("fullname"));
                                            tvDireccion.setText(obj.getString("address"));
                                            tvRepresentante.setText(obj.getString("district"));
                                            tvLat.setText(obj.getString("latitude"));
                                            tvLong.setText(obj.getString("longitude"));
                                            latitude=Double.valueOf(obj.getString("latitude"))  ;
                                            longitude=Double.valueOf(obj.getString("longitude"));
                                            map.clear();
                                            map.setMyLocationEnabled(true);
                                            MarkerNow = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Mi Ubicación")
                                                    //.snippet("Population: 4,137,400")
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(15).build();
                                            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                            // Establezco un listener para ver cuando cambio de posicion
                                            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                                public void onMyLocationChange(Location pos) {
                                                    // TODO Auto-generated method stub
                                                    // Extraigo la Lat y Lon del Listener
                                                    lat = pos.getLatitude();
                                                    lon = pos.getLongitude();
                                                    // Muevo la camara a mi posicion
                                                    //CameraUpdate cam = CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
                                                    //map.moveCamera(cam);
                                                    // Notifico con un mensaje al usuario de su Lat y Lon
                                                    //Toast.makeText(MyActivity,"Lat: " + lat + "\nLon: " + lon, Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidepDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }


    private void cargarAditorias(){
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST ,  GlobalConstant.dominio + "/JsonAuditsForStore" ,params,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            idCompany =response.getInt("company");
                            if (success == 1) {
                                JSONArray agentesObjJson;
                                db.deleteAllAudits();
                                agentesObjJson = response.getJSONArray("audits");
                                // looping through All Products
                                for (int i = 0; i < agentesObjJson.length(); i++) {
                                    JSONObject obj = agentesObjJson.getJSONObject(i);
                                    // Storing each json item in variable
                                    String idAuditoria = obj.getString("id");
                                    String auditoria = obj.getString("fullname");

                                    int totalAudit = db.getCountAuditForId(Integer.valueOf(idAuditoria));
                                    if(totalAudit==0) {
                                        Audit audit = new Audit();
                                        audit.setId(Integer.valueOf(idAuditoria));
                                        audit.setName(auditoria);
                                        audit.setStore_id(idPDV);
                                        audit.setScore(0);
                                        db.createAudit(audit);
                                    }

                                    int status = obj.getInt("state");
                                    bt = new Button(MyActivity);
                                    LinearLayout ly = new LinearLayout(MyActivity);
                                    ly.setOrientation(LinearLayout.VERTICAL);
                                    ly.setId(i+'_');
                                    LayoutParams params = new LayoutParams(
                                            LayoutParams.FILL_PARENT,
                                            LayoutParams.FILL_PARENT
                                    );
                                    params.setMargins(0, 10, 0, 10);
                                    ly.setLayoutParams(params);
                                    bt.setBackgroundColor(getResources().getColor(R.color.color_base));
                                    bt.setTextColor(getResources().getColor(R.color.counter_text_bg));
                                    bt.setText(auditoria);


                                    if(status==1) {
                                        Drawable  img = MyActivity.getResources().getDrawable( R.drawable.ic_check_on);
                                        img.setBounds( 0, 0, 60, 60 );  // set the image size
                                        bt.setCompoundDrawables( img, null, null, null );
                                        bt.setBackgroundColor(getResources().getColor(R.color.color_bottom_buttom_pressed));
                                        bt.setTextColor(getResources().getColor(R.color.color_base));
                                        bt.setEnabled(false);
                                    }  else {
                                        Drawable  img = MyActivity.getResources().getDrawable( R.drawable.ic_check_off);
                                        img.setBounds( 0, 0, 60, 60 );  // set the image size
                                        bt.setCompoundDrawables( img, null, null, null );
                                    }
                                    if(GlobalConstant.global_close_audit==1){

                                        bt.setBackgroundColor(getResources().getColor(R.color.color_bottom_buttom_pressed));
                                        bt.setTextColor(getResources().getColor(R.color.color_base));
                                        bt.setEnabled(false);
                                    }
                                    //bt.setBackground();
                                    bt.setId(Integer.valueOf(idAuditoria));
                                    bt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                                    bt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Toast.makeText(getActivity(), j  , Toast.LENGTH_LONG).show();
                                            Button button1 = (Button) v;
                                            String texto = button1.getText().toString();
                                            //Toast toast=Toast.makeText(getActivity(), selected, Toast.LENGTH_SHORT);
                                            Toast toast;
                                            toast = Toast.makeText(MyActivity, texto + ":" +  button1.getId(), Toast.LENGTH_LONG);
                                            toast.show();
                                            //int idBoton = Integer.valueOf(idAuditoria);
                                            Intent intent;
                                            int idAuditoria = button1.getId();

                                            Bundle argRuta = new Bundle();
                                            argRuta.clear();
                                            argRuta.putInt("company_id",idCompany);
                                            argRuta.putInt("idPDV",idPDV);
                                            argRuta.putInt("idRuta", IdRuta );
                                            argRuta.putString("fechaRuta",fechaRuta);
                                            argRuta.putInt("idAuditoria",idAuditoria);

                                            switch (idAuditoria) {
                                                case 2:
                                                    db.deleteAllPresenseProduct();
                                                    intent = new Intent("com.dataservicios.ttauditcolgate.PRESENCIAPRODUCTO");
                                                    intent.putExtras(argRuta);
                                                    startActivity(intent);

                                                    break;
                                                case 3:
                                                    intent = new Intent("com.dataservicios.ttauditcolgate.PRESENCIAMATERIAL");
                                                    intent.putExtras(argRuta);
                                                    startActivity(intent);

                                                    break;
                                                case 13:
                                                    intent = new Intent("com.dataservicios.ttauditcolgate.FACTURACION");
                                                    intent.putExtras(argRuta);
                                                    startActivity(intent);
                                                    break;

                                            }
                                        }
                                    });
                                    ly.addView(bt);
                                    linearLayout.addView(ly);
                                }
                                GlobalConstant.global_close_audit=0;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidepDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }


    private void guardarCoordenadas(){

        showpDialog();


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio+"/updatePositionStore" ,paramsCordenadas,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            if (success == 1) {
//
                                map.clear();
                                MarkerNow = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Mi Ubicación")
                                        //.snippet("Population: 4,137,400")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
//        Mostrando información del Market ni bien carga el mapa
                                // MarkerNow.showInfoWindow();
//        Ajuste de la cámara en el mayor nivel de zoom es posible que incluya los límites
                                map.setMyLocationEnabled(true);
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(15).build();
                                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                tvLat.setText(String.valueOf(lat));
                                tvLong.setText(String.valueOf(lon));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidepDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    private void insertaTiemporAuditoria(JSONObject parametros) {
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/insertaTiempo" ,parametros,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            if (success == 1) {
//
                                Log.d("DATAAAA", response.toString());
                                Toast.makeText(MyActivity, "Se ", Toast.LENGTH_LONG).show();


                                Intent intent = new Intent("com.dataservicios.ttauditcolgate.PREMIACION");
                                //intent.putExtras(argRuta);
                                startActivity(intent);

                                finish();
                            } else {
                                Toast.makeText(MyActivity, "No se ha podido enviar la información, intentelo mas tarde ",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(MyActivity, "No se ha podido enviar la información, intentelo mas tarde ",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidepDialog();
                    }
                }
        );


        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
//        Intent a = new Intent(this,PanelAdmin.class);
//        //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(a);
        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        finish();
        startActivity(getIntent());
    }

}

package com.dataservicios.ttauditcolgate.AditoriaColgate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dataservicios.ttauditcolgate.Model.Audit;
import com.dataservicios.ttauditcolgate.Model.PresenceProduct;
import com.dataservicios.ttauditcolgate.Model.PresencePublicity;
import com.dataservicios.ttauditcolgate.Model.Publicity;
import com.dataservicios.ttauditcolgate.R;
import com.dataservicios.ttauditcolgate.SQLite.DatabaseHelper;
import com.dataservicios.ttauditcolgate.adapter.PublicityAdapter;
import com.dataservicios.ttauditcolgate.app.AppController;
import com.dataservicios.ttauditcolgate.librerias.GlobalConstant;
import com.dataservicios.ttauditcolgate.librerias.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jaime Eduardo on 06/10/2015.
 */
public class PresenciaMaterial extends Activity{

    private  Activity MyActivity = this ;
    private static final String LOG_TAG = "Activity_Publicity";
    private SessionManager session;


    private ListView listView;
    private PublicityAdapter adapter;
    private DatabaseHelper db;

    private ProgressDialog pDialog;


    private List<Publicity> publicityList = new ArrayList<Publicity>();
    private List<PresencePublicity> presencePubli = new ArrayList<PresencePublicity>();

    private int pdv_id, rout_id, compay_id , idAuditoria,user_id , countProducts;
    private long  score = 0  ;
    private String fechaRuta ;
    private Button bt_guardar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.presencia_material_publicitario);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(getApplicationContext());



        bt_guardar = (Button) findViewById(R.id.btGuardar);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        compay_id =  bundle.getInt("company_id");
        pdv_id = bundle.getInt("idPDV");
        rout_id = bundle.getInt("idRuta");
        fechaRuta = bundle.getString("fechaRuta");
        idAuditoria = bundle.getInt("idAuditoria");


        Audit au = new Audit();
        au = db.getAudit(idAuditoria);
        getActionBar().setTitle(au.getName());

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;





        listView = (ListView) findViewById(R.id.listPublicidad);
        //publicityList =db.getAllPublicity();
       // adapter = new PublicityAdapter(this, db.getAllPublicity());
        adapter = new PublicityAdapter(this, publicityList);
        listView.setAdapter(adapter);
        Log.d(LOG_TAG, String.valueOf(db.getAllPublicity()));

        List<Publicity> publi = db.getAllPublicity();

        Log.d(LOG_TAG, String.valueOf(publi));
        for(int i = 0; i < publi.size(); i++){

            Publicity m = new Publicity();
            m.setId(publi.get(i).getId());
            m.setName(publi.get(i).getName());
            m.setActive(publi.get(i).getActive());
            m.setImage(publi.get(i).getImage());
            m.setCategory_name(publi.get(i).getCategory_name());
            m.setCategory_id(publi.get(i).getCategory_id());
            publicityList.add(m);
        }


        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // selected item
                String publicity_id = ((TextView) view.findViewById(R.id.tvId)).getText().toString();
                Toast toast = Toast.makeText(getApplicationContext(), publicity_id, Toast.LENGTH_SHORT);
                toast.show();

                Bundle argPDV = new Bundle();
                argPDV.putInt("pdv_id", Integer.valueOf(pdv_id));
                argPDV.putInt("rout_id", Integer.valueOf(rout_id));
                argPDV.putInt("publicity_id", Integer.valueOf(publicity_id));
                argPDV.putString("fechaRuta", fechaRuta);
                argPDV.putInt("idAuditoria", idAuditoria);
                Intent intent = new Intent("com.dataservicios.ttauditcolgate.DETAIPUBLICITY");
                intent.putExtras(argPDV);
                startActivity(intent);
            }
        });

        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar la lista de publicidades: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {


                        presencePubli.clear();
                        presencePubli = db.getAllPresencePublicity();
                        long acumuladorPuntaje = 0;

                        JSONArray jsonArrayPublycity = new JSONArray();
                        long ptCat38,ptCat39,ptCat40, ptCat41, ptCat42;
                        ptCat38 = 0 ;
                        ptCat40 = 0;
                        ptCat41 = 0;
                        ptCat42 = 0;
                        ptCat39 = 0;

                        for (PresencePublicity p : presencePubli) {

                            switch (p.getCategory_id()) {
                                case 38:
                                    if( p.getFound()==1 && p.getVisible()==1 && p.getLayout_correcto()==1) {
                                        ptCat38 =  150;
                                    }
                                    break;
                                case 39:
                                    if( p.getFound()==1 && p.getVisible()==1 && p.getLayout_correcto()==1) {
                                        ptCat39 =  200;
                                    }
                                    break;
                                case 40:
                                    if( p.getFound()==1 && p.getVisible()==1 && p.getLayout_correcto()==1) {
                                        ptCat40 =  150;
                                    }
                                    break;
                                case 41:
                                    if( p.getFound()==1 && p.getVisible()==1 && p.getLayout_correcto()==1) {
                                        ptCat41 =  100;
                                    }
                                    break;
                                case 42:
                                    if( p.getFound()==1 && p.getVisible()==1 && p.getLayout_correcto()==1) {
                                        ptCat42 =  100;
                                    }
                                    break;
                            }



                            JSONObject jsonObj= new JSONObject();
                            try {
                                jsonObj.put("publicity_id",String.valueOf(p.getPublicity_id()));
                                jsonObj.put("category_id",String.valueOf(p.getCategory_id()));
                                jsonObj.put("found",String.valueOf(p.getFound()));
                                jsonObj.put("layout_correct",String.valueOf(p.getLayout_correcto()));
                                jsonObj.put("visible",String.valueOf(p.getVisible()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsonArrayPublycity.put(jsonObj.toString());
                        }
//
                        score = ptCat38 + ptCat39 + ptCat40 + ptCat41 + ptCat42;

                        Audit audit =new Audit();
                        audit.setId(idAuditoria);
                        audit.setStore_id(pdv_id);
                        audit.setScore(score);
                        db.updateAudit(audit);

                        presencePubli.clear();
                        presencePubli = db.getAllPresencePublicityGroupCategory();

                        JSONArray jsonArrayScoreDetails = new JSONArray();
                        for (PresencePublicity p : presencePubli) {
                            if (p.getCategory_id()==38) {
                                JSONObject jsonObj= new JSONObject();
                                try {
                                    jsonObj.put("category_id",String.valueOf(p.getCategory_id()));
                                    jsonObj.put("score",String.valueOf(ptCat38));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                jsonArrayScoreDetails.put(jsonObj.toString());
                            }
                            if (p.getCategory_id()==39) {
                                JSONObject jsonObj= new JSONObject();
                                try {
                                    jsonObj.put("category_id",String.valueOf(p.getCategory_id()));
                                    jsonObj.put("score",String.valueOf(ptCat39));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                jsonArrayScoreDetails.put(jsonObj.toString());
                            }
                            if (p.getCategory_id()==40) {
                                JSONObject jsonObj= new JSONObject();
                                try {
                                    jsonObj.put("category_id",String.valueOf(p.getCategory_id()));
                                    jsonObj.put("score",String.valueOf(ptCat40));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                jsonArrayScoreDetails.put(jsonObj.toString());
                            }
                            if (p.getCategory_id()==41) {
                                JSONObject jsonObj= new JSONObject();
                                try {
                                    jsonObj.put("category_id",String.valueOf(p.getCategory_id()));
                                    jsonObj.put("score",String.valueOf(ptCat41));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                jsonArrayScoreDetails.put(jsonObj.toString());
                            }
                            if (p.getCategory_id()==42) {
                                JSONObject jsonObj= new JSONObject();
                                try {
                                    jsonObj.put("category_id",String.valueOf(p.getCategory_id()));
                                    jsonObj.put("score",String.valueOf(ptCat42));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                jsonArrayScoreDetails.put(jsonObj.toString());
                            }
                        }

                        JSONObject paramsData;
                        paramsData = new JSONObject();
                        try {
                            paramsData.put("store_id", pdv_id);
                            paramsData.put("audit_", idAuditoria);
                            paramsData.put("company_id", compay_id);
                            paramsData.put("rout_id", rout_id);
                            paramsData.put("user_id",user_id);
                            paramsData.put("status", "1");
                            paramsData.put("score",String.valueOf(score));
                            paramsData.put("publicity",jsonArrayPublycity);
                            paramsData.put("scores_details", jsonArrayScoreDetails );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        insertAudit(paramsData);
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);
            }
        });
    }

    private void insertAudit(JSONObject paramsData) {
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/saveAuditVisibility" ,paramsData,
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
                            //compay_id =response.getInt("company");
                            if (success == 1) {

                                Toast toast;
                                toast = Toast.makeText(MyActivity, "Ha ganado " + String.valueOf(score), Toast.LENGTH_LONG);
                                toast.show();
                                // onBackPressed();
//                                Bundle argRuta = new Bundle();
//                                argRuta.clear();
//                                argRuta.putInt("company_id",compay_id);
//                                argRuta.putInt("pdv_id",pdv_id);
//                                argRuta.putInt("idRuta", idRuta );
//                                argRuta.putInt("idAuditoria",idAuditoria);
//
//                                Intent intent;
//                                intent = new Intent(MyActivity,informacionCuatro.class);
//                                intent.putExtras(argRuta);
//                                startActivity(intent);
                                finish();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
//                this.finish();
//                Intent a = new Intent(this,PanelAdmin.class);
//                //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(a);
//                overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }


    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

        //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        Bundle argPDV = new Bundle();
//        argPDV.putInt("pdv_id", pdv_id );
//        argPDV.putInt("idRuta", idRuta );
//        argPDV.putString("fechaRuta",fechaRuta);
//        Intent a = new Intent(this,DetallePdv.class);
//        a.putExtras(argPDV);
//
//        startActivity(a);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
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



    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

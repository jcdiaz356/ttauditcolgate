package com.dataservicios.ttauditcolgate.AditoriaColgate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dataservicios.ttauditcolgate.Model.Audit;
import com.dataservicios.ttauditcolgate.Model.PresenceProduct;
import com.dataservicios.ttauditcolgate.Model.Product;
import com.dataservicios.ttauditcolgate.R;
import com.dataservicios.ttauditcolgate.SQLite.DatabaseHelper;
import com.dataservicios.ttauditcolgate.adapter.ProductsAdapter;
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
 * Created by Jaime Eduardo on 28/09/2015.
 */
public class PresenciaProducto extends Activity {
    private Activity MyActivity= this;
    private EditText etCodigo;
    private TextView tvResultado ;
    private Button btGuardar, btBuscar;

    private SessionManager session;


    private ListView listView;
    private ProductsAdapter adapter;
    private DatabaseHelper db;

    private ProgressDialog pDialog;


    private List<Product> productsList = new ArrayList<Product>();
    private List<PresenceProduct> presenceProd = new ArrayList<PresenceProduct>();

    private int pdv_id, rout_id, compay_id , idAuditoria,user_id , countProducts;
    private long  score = 0  ;
    private String fechaRuta ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.presencia_producto);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Presencia Producto");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        compay_id =  bundle.getInt("company_id");
        pdv_id = bundle.getInt("idPDV");
        rout_id = bundle.getInt("idRuta");
        fechaRuta = bundle.getString("fechaRuta");
        idAuditoria = bundle.getInt("idAuditoria");

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;

        etCodigo = (EditText) findViewById(R.id.etCodigo) ;
        tvResultado = (TextView) findViewById(R.id.tvResultado);
        btGuardar = (Button) findViewById(R.id.btGuardar) ;
        btBuscar = (Button) findViewById(R.id.btBuscar);

        db = new DatabaseHelper(getApplicationContext());

        listView = (ListView) findViewById(R.id.listProducts);
        adapter = new ProductsAdapter(this, productsList);
        listView.setAdapter(adapter);

        btBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = etCodigo.getText().toString().trim();
                int conuntElement = adapter.getCount();
                if (s.length() > 0) {
                    etCodigo.setText("");


                    if (conuntElement>0){
                        Object[] productItem = new Object[conuntElement];
                        for(int i = 0; i < conuntElement; i++){
                            productItem[i] = adapter.getItem(i);
                            Product pd =  new Product();
                            pd = (Product) productItem[i];
                            //Log.d("TAG", "Item nr: " + i + " " + adapter.getItem(i));
                            if (pd.getCode().equals(s.toString())){
                                Toast toast = Toast.makeText(MyActivity , "Producto ya se encuentra en la lista", Toast.LENGTH_SHORT );
                                toast.show();
                                etCodigo.requestFocus();
                                return;
                            }

                        }
                        addProductAdapter(s);
                        etCodigo.requestFocus();
                    } else {

                        addProductAdapter(s);
                        etCodigo.requestFocus();

                    }


                }
            }
        });

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (db.getCountPresenseProduct() == 0 ) {
                   Toast toast;
                   toast = Toast.makeText(MyActivity, "No se puede guardar la lista vacía", Toast.LENGTH_LONG);
                   toast.show();
                   return;
               }

                db.getAllPresenceProduct();
                int cremaDental = db.getCountPresenseProductForCategory(35);
                int cepillos = db.getCountPresenseProductForCategory(36);
                int jabones = db.getCountPresenseProductForCategory(26);
                int desodorantes =  db.getCountPresenseProductForCategory(24);
                int suavizantes = db.getCountPresenseProductForCategory(37);


                if (cremaDental >= 5) {
                    countProducts =  cremaDental;
                }

                if (cepillos >= 3) {
                    countProducts = countProducts +  cepillos;
                }

                if (jabones >= 4) {
                    countProducts = countProducts +  jabones;
                }
                if (desodorantes >= 4) {
                    countProducts = countProducts +  desodorantes;
                }
                if (suavizantes >= 3) {
                    countProducts = countProducts +  suavizantes;
                }




                if(countProducts>=19) {
                    score = 200 ;
                }

                Audit audit =new Audit();
                audit.setId(idAuditoria);
                audit.setStore_id(pdv_id);
                audit.setScore(score);
                db.updateAudit(audit);



                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        presenceProd.clear();
                        presenceProd = db.getAllPresenceProduct();

                        JSONArray jsonArrayProducts = new JSONArray();

                        for (PresenceProduct p : presenceProd) {

                            JSONObject jsonObj= new JSONObject();
                            try {
                                jsonObj.put("product_id",String.valueOf(p.getProduct_id()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsonArrayProducts.put(jsonObj.toString());
                        }
//                        for (loop) {
//                            JSONObject jsonObj= new JSONObject();
//                            jsonObj.put("product_id", srcOfPhoto);
//                            jsonArray.put(jsonObj.toString());
//                        }


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
                            paramsData.put("products",jsonArrayProducts);
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

    /**
     * Insert Data Auditory
     * @param paramsData
     */
    private void insertAudit(JSONObject paramsData) {
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/saveAuditPresencia" ,paramsData,
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

    private void addProductAdapter(String codeProduct){
        Product product = new Product();
        product = db.getProductCode(codeProduct.toString());

        if (product.getCode() != null) {
            PresenceProduct pd = new PresenceProduct();
            pd.setCategory_id(product.getCategory_id());
            pd.setProduct_id(product.getId());
            pd.setStore_id(pdv_id);

            db.createPresenseProduct(pd);
            db.getAllPresenceProduct();
            productsList.add(product);
            adapter.notifyDataSetChanged();

        } else {
            Toast toast = Toast.makeText(MyActivity , "Producto no se encuentra", Toast.LENGTH_SHORT );
            toast.show();
        }
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

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}

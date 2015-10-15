package com.dataservicios.ttauditcolgate.AditoriaColgate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dataservicios.ttauditcolgate.AndroidCustomGalleryActivity;
import com.dataservicios.ttauditcolgate.Model.PresenceProduct;
import com.dataservicios.ttauditcolgate.Model.PresencePublicity;
import com.dataservicios.ttauditcolgate.Model.Publicity;
import com.dataservicios.ttauditcolgate.R;
import com.dataservicios.ttauditcolgate.SQLite.DatabaseHelper;
import com.dataservicios.ttauditcolgate.app.AppController;
import com.dataservicios.ttauditcolgate.librerias.GlobalConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jaime Eduardo on 07/10/2015.
 */
public class DetailPublicity extends Activity {
    private Activity MyActivity = this ;
    private Integer store_id, rout_id, publicity_id , idAuditoria ;
    private String fechaRuta;
    private Switch sw_visible, sw_layout;
    private Button bt_guardar, bt_photo;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private DatabaseHelper db ;
    int  is_visible, is_layout, is_found;
    Publicity publicity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_publicity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Presencia de material publicitario");


        Bundle bundle = getIntent().getExtras();
        bt_guardar = (Button) findViewById(R.id.btGuardar);
        bt_photo = (Button) findViewById(R.id.btPhoto);
        sw_visible = (Switch) findViewById(R.id.swVisible);
        sw_layout = (Switch) findViewById(R.id.swLayoutCorrecto);



        store_id = bundle.getInt("pdv_id");
        rout_id =  bundle.getInt("rout_id");
        publicity_id =  bundle.getInt("publicity_id");
        //argPDV.putInt("idAuditoria", idAuditoria);
        fechaRuta = bundle.getString("fechaRuta");

        is_visible = 0;
        is_layout = 0;
        is_found = 1;

        db = new DatabaseHelper(getApplicationContext());

        if (imageLoader == null)  imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.thumbnail);


        publicity = new Publicity();
        publicity = db.getPublicity(publicity_id);

        if (publicity.getCategory_id() == 42){

            sw_layout.setEnabled(false);
            sw_layout.setVisibility(View.INVISIBLE);
        }

        thumbNail.setImageUrl(GlobalConstant.dominio + "/media/images/publicities/" + publicity.getImage(), imageLoader);

        sw_layout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_layout = 1;
                } else {
                    is_layout = 0;
                }
            }
        });

        sw_visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_visible = 1;
                } else {
                    is_visible = 0;
                }
            }
        });

        bt_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        PresencePublicity presenceP = new PresencePublicity();
                        presenceP.setCategory_id(publicity.getCategory_id());
                        presenceP.setFound(is_found);
                        presenceP.setLayout_correcto(is_layout);
                        presenceP.setVisible(is_visible);
                        presenceP.setPublicity_id(publicity_id);
                        presenceP.setStore_id(store_id);
                        db.createPresensePublicity(presenceP);
                        publicity.setActive(0);
                        db.updatePublicity(publicity);

                        String mensaje ="";

                        switch (publicity.getCategory_id()) {
                            case 38:
                                if(is_found==1 && is_layout==1)  mensaje = "Usted ha ganado 150 puntos";
                                else mensaje = "Usted ha ganado 0 puntos";

                                break;
                            case 39:
                                if(is_found==1 && is_layout==1)  mensaje = "Usted ha ganado 200 puntos";
                                else mensaje = "Usted ha ganado 0 puntos";

                                break;
                            case 40:
                                if(is_found==1 && is_layout==1)  mensaje = "Usted ha ganado 150 puntos";
                                else mensaje = "Usted ha ganado 0 puntos";

                                break;
                            case 41:
                                if(is_found==1 && is_layout==1)  mensaje = "Usted ha ganado 100 puntos";
                                else mensaje = "Usted ha ganado 0 puntos";

                                break;

                            case 42:
                                if(is_found==1 )  mensaje = "Usted ha ganado 100 puntos";
                                else mensaje = "Usted ha ganado 0 puntos";

                                break;

                        }

                        Toast toast = Toast.makeText(getApplicationContext(), mensaje  , Toast.LENGTH_SHORT);
                        toast.show();

                        finish();

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

    private void takePhoto() {
        /*Intent intent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

        startActivityForResult(intent, 100);
        */

        //Bundle bundle = getIntent().getExtras();
        //String id_agente = bundle.getString(TAG_ID);

        // getting values from selected ListItem
        // String aid = id_agente;
        // Starting new intent
        Intent i = new Intent( MyActivity, AndroidCustomGalleryActivity.class);
        Bundle bolsa = new Bundle();



        bolsa.putString("store_id",String.valueOf(store_id));
        bolsa.putString("publicities_id",String.valueOf(publicity_id));
        bolsa.putString("invoices_id","0");
        bolsa.putString("url_insert_image",GlobalConstant.dominio + "/insertImagesPublicities");
        bolsa.putString("tipo","1");

        i.putExtras(bolsa);
        startActivity(i);


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

}

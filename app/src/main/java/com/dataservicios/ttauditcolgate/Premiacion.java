package com.dataservicios.ttauditcolgate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dataservicios.ttauditcolgate.Model.Audit;
import com.dataservicios.ttauditcolgate.Model.PresencePublicity;
import com.dataservicios.ttauditcolgate.SQLite.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaime Eduardo on 10/10/2015.
 */
public class Premiacion extends Activity{
    private Activity MyActivity;
    private Button bt_Cerrar;
    private TextView tv_mensaje;

    private DatabaseHelper db;
    private List<Audit> audits = new ArrayList<Audit>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.premiacion);
        MyActivity = (Activity) this;

        bt_Cerrar = (Button) findViewById(R.id.btCerrar);
        tv_mensaje = (TextView) findViewById(R.id.tvMensaje);


        db =  new DatabaseHelper(MyActivity);

        long total=0;
        audits=db.getAllAudits();
        for (Audit audit : audits) {

            total =+ (long) audit.getScore();
        }

        tv_mensaje.setText(String.valueOf(total) + " Pts.");


        bt_Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
    }
}

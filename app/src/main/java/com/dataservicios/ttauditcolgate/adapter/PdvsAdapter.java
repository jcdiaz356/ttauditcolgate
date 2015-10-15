package com.dataservicios.ttauditcolgate.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dataservicios.ttauditcolgate.Model.Pdv;
import com.dataservicios.ttauditcolgate.R;
import com.dataservicios.ttauditcolgate.app.AppController;


import java.util.List;



/**
 * Created by usuario on 12/01/2015.
 */
public class PdvsAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Pdv> pdvItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public PdvsAdapter(Activity activity, List<Pdv> rutaItems) {
        this.activity = activity;
        this.pdvItems = rutaItems;
    }

    @Override
    public int getCount() {
        return pdvItems.size();
    }

    @Override
    public Object getItem(int location) {
        return pdvItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View view = convertView;
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_pdvs, null);
//        if (imageLoader == null)
//            imageLoader = AppController.getInstance().getImageLoader();
       NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
        TextView idPdv = (TextView) convertView.findViewById(R.id.tvId);
        TextView pdv = (TextView) convertView.findViewById(R.id.tvPdv);
        TextView direccion = (TextView) convertView.findViewById(R.id.tvDireccion);
        TextView distrito = (TextView) convertView.findViewById(R.id.tvDistrito);
        ImageView imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);
        // getting ruta data for the row
        Pdv m = pdvItems.get(position);
        idPdv.setText(String.valueOf(m.getId()));
        // thumbnail image
       // thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
        // rutaDia
        pdv.setText(m.getPdv());
        // pdvs
        direccion.setText( m.getDireccion());
        // release year
        distrito.setText(m.getDistrito());

        if(m.getStatus()==0){

            imgStatus.setImageResource(R.drawable.ic_check_off);

        } else if(m.getStatus()==1){
            imgStatus.setImageResource(R.drawable.ic_check_on);
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {

        // Deshabilitando los items del adptador segun el statu
        if( pdvItems.get(position).getStatus()==1){


            return false;

        }
        return true;
    }

}

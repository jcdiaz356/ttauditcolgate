package com.dataservicios.ttauditcolgate.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dataservicios.ttauditcolgate.Model.Pdv;
import com.dataservicios.ttauditcolgate.Model.Product;
import com.dataservicios.ttauditcolgate.R;
import com.dataservicios.ttauditcolgate.app.AppController;

import java.util.List;

/**
 * Created by Jaime Eduardo on 30/09/2015.
 */
public class ProductsAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Product> productsItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProductsAdapter(Activity activity, List<Product> productsItems) {
        this.activity = activity;
        this.productsItems = productsItems;
    }


    @Override
    public int getCount() {
        return productsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return productsItems.get(position);

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
            convertView = inflater.inflate(R.layout.list_row_product, null);

       // if (imageLoader == null)  imageLoader = AppController.getInstance().getImageLoader();

       // NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvCodigo = (TextView) convertView.findViewById(R.id.tvCodigo);

        ImageView imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);

        Product m = productsItems.get(position);

       // thumbNail.setImageUrl(m.getImage(), imageLoader);
        tvId.setText(String.valueOf(m.getId()));

        tvName.setText(m.getName());

        tvCodigo.setText( m.getCategory_name());


        //if(m.getStatus()==0){

            imgStatus.setImageResource(R.drawable.ic_check_on);

       // } else if(m.getStatus()==1){
           // imgStatus.setImageResource(R.drawable.ic_check_on);
       // }
        return convertView;
    }


}

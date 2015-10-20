package com.dataservicios.ttauditcolgate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


import com.dataservicios.ttauditcolgate.Services.UploadService;
import com.dataservicios.ttauditcolgate.adapter.ImageAdapter;
import com.dataservicios.ttauditcolgate.librerias.GlobalConstant;

//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.ContentBody;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 06/02/2015.
 */
public class AndroidCustomGalleryActivity extends Activity {


    private static final int TAKE_PICTURE = 1;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "-Colgate_foto_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private  String url_insert_image ;


    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private ImageAdapter imageAdapter;
    ArrayList<String> f = new ArrayList<String>();// list of file paths
    File[] listFile;
    ArrayList<String> names_file = new ArrayList<String>();
    Activity MyActivity ;
    String store_id,publicities_id,tipo, invoices_id;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        MyActivity = (Activity) this;

        Bundle bundle = getIntent().getExtras();



        store_id = bundle.getString("store_id");
        publicities_id = bundle.getString("publicities_id");
        invoices_id = bundle.getString("invoices_id");
        url_insert_image = bundle.getString("url_insert_image");
        tipo = bundle.getString("tipo");


        getFromSdcard();

        final GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter(MyActivity,f);
        imagegrid.setAdapter(imageAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }


        Button btn_photo = (Button)findViewById(R.id.take_photo);
        Button btn_upload = (Button)findViewById(R.id.save_images);
        // Register the onClick listener with the implementation above
        btn_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // create intent with ACTION_IMAGE_CAPTURE action
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                Bundle bundle = getIntent().getExtras();
                String idPDV = bundle.getString("idPDV");

                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = String.format("%06d", Integer.parseInt(store_id))+JPEG_FILE_PREFIX + timeStamp;
                File albumF = getAlbumDir();
                // to save picture remove comment
                File file = new File(albumF,imageFileName+JPEG_FILE_SUFFIX);

                Uri photoPath = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);

                mCurrentPhotoPath = getAlbumDir().getAbsolutePath();

                // start camera activity
                startActivityForResult(intent, TAKE_PICTURE);

            }
        });


        btn_upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

//                Bundle bundle = getIntent().getExtras();
//                String idPDV = bundle.getString("idPDV");
//                String idPoll = bundle.getString("idPoll");
//                String tipo = bundle.getString("tipo");

                File file= new File(Environment.getExternalStorageDirectory().toString()+ GlobalConstant.directory_images);

                if (file.isDirectory())
                {
                    listFile = file.listFiles();
                    int holder_counter = 1;
                    int contador=0;
                    //Verificando si se ha seleccionado alguna foto
                    if (listFile.length>0){
                        for (int i = 0; i < listFile.length; i++){
                            if (imageAdapter.getItem(holder_counter).checkbox.isChecked())
                            {
                                contador ++;
                            }
                            holder_counter++;
                        }
                        if (contador==0){
                            Toast toast;
                            toast = Toast.makeText(MyActivity , "Debe Seleccionar almenos una imagen", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                    } else{
                        Toast toast;
                        toast = Toast.makeText(MyActivity , "No hay ninguna foto en la galeria", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }

                    holder_counter=1;

                    for (int i = 0; i < listFile.length; i++)
                    {
                        if (  listFile[i].getName().substring(0,6).equals(String.format("%06d", Integer.parseInt(store_id)) ))
                        {
                            if (imageAdapter.getItem(holder_counter).checkbox.isChecked())

                            {


                                String name = listFile[i].getName();
                                names_file.add(name);
                            }
                            holder_counter++;
                        }
                    }
                }
                //new ServerUpdate().execute(names_file);

                //String foto = names_file.get(0);
                //Uri uri = Uri.fromFile(new File(file +"/"+ foto) );

//                Bundle bundle = getIntent().getExtras();
//                store_id = bundle.getString("store_id");
//                publicities_id = bundle.getString("publicities_id");
//                invoices_id = bundle.getString("invoices_id");
//                url_insert_image = bundle.getString("url_insert_image");
//                tipo = bundle.getString("tipo");



                Intent intent = new Intent(MyActivity, UploadService.class);
                //Log.i("FOO", uri.toString());
                Bundle argPDV = new Bundle();



                argPDV.putString("store_id",store_id );
                argPDV.putString("publicities_id",publicities_id );
                argPDV.putString("invoices_id",invoices_id );
                argPDV.putString("url_insert_image",url_insert_image );
                argPDV.putString("tipo",tipo);

                intent.putStringArrayListExtra("names_file", names_file);
                intent.putExtras(argPDV);
                startService(intent);
                finish();

            }
        });
    }


    //Enviar a AgenteDetailActivity


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = getIntent().getExtras();
        String idPDV = bundle.getString("idPDV");

        // getting values from selected ListItem
        String aid = idPDV;
        switch (item.getItemId()) {
            case android.R.id.home:
                // go to previous screen when app icon in action bar is clicked

                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
//                Intent intent = new Intent(this, AgenteDetailActivity.class);
//                Bundle bolsa = new Bundle();
//                bolsa.putString("id", aid);
//                intent.putExtras(bolsa);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    /* Photo album for this application */
    private String getAlbumName() {
       // return getString(R.string.album_name);
        return GlobalConstant.albunName;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
        }
    }


    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            mCurrentPhotoPath = null;
            Bundle bundle = getIntent().getExtras();

            Intent i = new Intent( AndroidCustomGalleryActivity.this , AndroidCustomGalleryActivity.class);
            Bundle bolsa = new Bundle();

            bolsa.putString("store_id",store_id );
            bolsa.putString("publicities_id",publicities_id );
            bolsa.putString("url_insert_image",url_insert_image );
            bolsa.putString("invoices_id",invoices_id );
            bolsa.putString("tipo",tipo);


            i.putExtras(bolsa);
            startActivity(i);
            finish();
        }

    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }


    public void getFromSdcard()
    {
        Bundle bundle = getIntent().getExtras();
        String store_id = bundle.getString("store_id");

        File file= new File(Environment.getExternalStorageDirectory().toString()+ GlobalConstant.directory_images);

        if (file.isDirectory())
        {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++)
            {
                if (  listFile[i].getName().substring(0,6).equals(String.format("%06d", Integer.parseInt(store_id)) ))
                {
                    f.add(listFile[i].getAbsolutePath());
                }

            }
        }
    }


    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }
}

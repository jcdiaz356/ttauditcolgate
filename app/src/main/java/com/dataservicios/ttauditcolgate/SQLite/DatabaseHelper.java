package com.dataservicios.ttauditcolgate.SQLite;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dataservicios.ttauditcolgate.Model.Audit;
import com.dataservicios.ttauditcolgate.Model.Encuesta;
import com.dataservicios.ttauditcolgate.Model.PresenceProduct;
import com.dataservicios.ttauditcolgate.Model.PresencePublicity;
import com.dataservicios.ttauditcolgate.Model.Product;
import com.dataservicios.ttauditcolgate.Model.Publicity;
import com.dataservicios.ttauditcolgate.Model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by usuario on 12/02/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";
    // Database Version
    private static final int DATABASE_VERSION = 14;
    // Database Name
    private static final String DATABASE_NAME = "ttaudit-colgate";
    // Table Names
    private static final String TABLE_POLL = "poll";
    private static final String TABLE_USER = "user";
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_PUBLICITY = "publicity";
    private static final String TABLE_PRESENCE_PRODUCTS = "presense_products";
    private static final String TABLE_PRESENCE_PUBLICITY = "presence_publicity";
    private static final String TABLE_AUDITS = "audits";



    //Name columns common
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    //Name columns user
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    //Name columns poll
    private static final String KEY_QUESTION = "name_question";
    private static final String KEY_ID_AUDITORIA = "auditoria_id";

    //Name columns Products
    private static final String KEY_CODE = "code";
    private static final String KEY_IMAGEN = "image";
    private static final String KEY_COMPANY_ID = "company_id";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_CATEGORY_NAME = "category_name";

    //Name column Presense Product
    private static final String KEY_STORY_ID = "store_id";
    private static final String KEY_PRODUCT_ID = "product_id";

    //Name column Presense Publicity
    private static final String KEY_PUBLICITY_ID = "publicity_id";
    private static final String KEY_FOUND = "found";
    private static final String KEY_VISIBLE = "visible";
    private static final String KEY_LAYOUT_CORRECT = "layout_correct";


    //Name column Table publicity
    private  static final String KEY_ACTIVE = "active";

    //Name column Audit
    private static final String KEY_SCORE = "score";

    //Nname





    // Table Create Statements
    // eNCUESTA table create statement
    private static final String CREATE_TABLE_POLL = "CREATE TABLE "
            + TABLE_POLL + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_ID_AUDITORIA + " INTEGER,"
            + KEY_QUESTION  + " TEXT " + ")";

    // User table create statement
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_PASSWORD + " TEXT " + ")";

    // Products table create statement
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE "
            + TABLE_PRODUCTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_CODE + " TEXT,"
            + KEY_IMAGEN + " TEXT, "
            + KEY_COMPANY_ID + " INTEGER, "
            + KEY_CATEGORY_ID + " INTEGER, "
            + KEY_CATEGORY_NAME + " TEXT " + ")";

    // Publicity table create statement

    private static final String CREATE_TABLE_PUBLICITY = "CREATE TABLE "
            + TABLE_PUBLICITY + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_IMAGEN + " TEXT, "
            + KEY_COMPANY_ID + " INTEGER, "
            + KEY_CATEGORY_ID + " INTEGER, "
            + KEY_CATEGORY_NAME + " TEXT, "
            + KEY_ACTIVE + " INTEGER " + ")";

    // Presence product create table statemnt
    private static final String CREATE_TABLE_PRESENCE_PRODUCTS  = "CREATE TABLE "
            + TABLE_PRESENCE_PRODUCTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_STORY_ID + " INTEGER, "
            + KEY_CATEGORY_ID + " INTEGER, "
            + KEY_PRODUCT_ID + " INTEGER )";

    // Presence product create table statemnt
    private static final String CREATE_TABLE_PRESENCE_PUBLICITY  = "CREATE TABLE "
            + TABLE_PRESENCE_PUBLICITY + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_STORY_ID + " INTEGER, "
            + KEY_CATEGORY_ID + " INTEGER, "
            + KEY_FOUND + " INTEGER, "
            + KEY_VISIBLE + " INTEGER, "
            + KEY_LAYOUT_CORRECT + " INTEGER, "
            + KEY_PUBLICITY_ID + " INTEGER )";

    // Audit create table statement
    private static final String CREATE_TABLE_AUDITS  = "CREATE TABLE "
            + TABLE_AUDITS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT ,"
            + KEY_STORY_ID + " INTEGER, "
            + KEY_SCORE + " INTEGER )";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//
    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_POLL);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_PRESENCE_PRODUCTS);
        db.execSQL(CREATE_TABLE_PRESENCE_PUBLICITY);
        db.execSQL(CREATE_TABLE_AUDITS);
        db.execSQL(CREATE_TABLE_PUBLICITY);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POLL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESENCE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESENCE_PUBLICITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUBLICITY);

        // create new tables
        onCreate(db);
    }


    // ------------------------ "POLS" table methods ----------------//

    /*
     * Creating a Encuesta
     */
    public long createEncuesta(Encuesta encuesta) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, encuesta.getId());
        values.put(KEY_ID_AUDITORIA, encuesta.getIdAuditoria());
        values.put(KEY_QUESTION, encuesta.getQuestion());

        // insert row
        //long todo_id = db.insert(TABLE_PEDIDO, null, values);
         db.insert(TABLE_POLL, null, values);

        long todo_id = encuesta.getId();
        return todo_id;
    }



    /*
     * get single Encuesta
     */
    public Encuesta getEncuesta(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_POLL + " WHERE "
                + KEY_ID + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Encuesta pd = new Encuesta();
        pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        pd.setIdAiditoria(c.getInt(c.getColumnIndex(KEY_ID_AUDITORIA)));
        pd.setQuestion((c.getString(c.getColumnIndex(KEY_QUESTION))));
        return pd;
    }

    /*
     * get single Encuesta por Auditoria
     */
    public Encuesta getEncuestaAuditoria(long idAuditoria) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_POLL + " WHERE "
                + KEY_ID_AUDITORIA + " = " + idAuditoria;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Encuesta pd = new Encuesta();
        pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        pd.setIdAiditoria(c.getInt(c.getColumnIndex(KEY_ID_AUDITORIA)));
        pd.setQuestion((c.getString(c.getColumnIndex(KEY_QUESTION))));
        return pd;
    }

    /**
     * getting all Encuesta
     * */
    public List<Encuesta> getAllEncuesta() {
        List<Encuesta> encuesta = new ArrayList<Encuesta>();
        String selectQuery = "SELECT  * FROM " + TABLE_POLL;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Encuesta pd = new Encuesta();
                pd.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                pd.setIdAiditoria(c.getInt((c.getColumnIndex(KEY_ID_AUDITORIA))));
                pd.setQuestion((c.getString(c.getColumnIndex(KEY_QUESTION))));

                // adding to todo list
                encuesta.add(pd);
            } while (c.moveToNext());
        }
        return encuesta;
    }

    /*
     * getting Encuesta count
     */
    public int getEncuestaCount() {
        String countQuery = "SELECT  * FROM " + TABLE_POLL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }
    /*
     * Updating a Encuesta
     */
    public int updateEncuesta(Encuesta encuesta) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION, encuesta.getQuestion());
        // updating row
        return db.update(TABLE_POLL, values, KEY_ID + " = ?",
                new String[] { String.valueOf(encuesta.getId()) });
    }

    /*
     * Deleting a Encuesta
     */
    public void deleteEncuesta(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POLL, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /*
     * Deleting all Encuesta
     */
    public void deleteAllEncuesta() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POLL, null, null);

    }


    //---------------------------------------------------------------//
    // ------------------------ "USER" table methods ----------------//
    //---------------------------------------------------------------//
    /*
     * Creating a USER
     */
    /*
     * Creating a USER
     */
    public long createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, user.getId());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());

        // insert row
        //long todo_id = db.insert(TABLE_PEDIDO, null, values);
        db.insert(TABLE_USER, null, values);

        long todo_id = user.getId();
        return todo_id;
    }



    /*
     * get single User id
     */
    public User getUser(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_ID + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        User pd = new User();
        pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        pd.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
        pd.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        pd.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
        return pd;
    }

    /**
     *
     * @param name
     * @return
     */
    public User getUserName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_NAME + " = " + name;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        User pd = new User();
        pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        pd.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        pd.setEmail((c.getString(c.getColumnIndex(KEY_EMAIL))));
        pd.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
        return pd;
    }

    /**
     *
     * @param email
     * @return
     */
    public User getUserEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_EMAIL + " = " + email;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        User pd = new User();
        pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        pd.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        pd.setEmail((c.getString(c.getColumnIndex(KEY_EMAIL))));
        pd.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
        return pd;
    }

    /**
     * getting all User
     * */
    public List<User> getAllUser() {
        List<User> users = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                User pd = new User();
                pd.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                pd.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                pd.setEmail((c.getString(c.getColumnIndex(KEY_EMAIL))));
                pd.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
                // adding to todo list
                users.add(pd);
            } while (c.moveToNext());
        }
        return users;
    }
    /*
         * getting User count
         */
    public int getUserCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.e(LOG, countQuery);
        cursor.close();
        // return count
        return count;
    }
    /*
     * Updating a User
     */
    public int updatePedido(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PASSWORD, user.getPassword());
        // updating row
        return db.update(TABLE_USER, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }


    /*
     * Deleting a User
     */
    public void deleteUser(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /*
     * Deleting all User
     */
    public void deleteAllUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null );

    }



    //---------------------------------------------------------------//
    // ------------------------ "PRODUCTS" table methods ----------------//
    //---------------------------------------------------------------//
    /*
     * Creating a PRODUCTS
     */
    /*
     * Creating a PRODUCTS
     */
    public long createProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, product.getId());
        values.put(KEY_NAME, product.getName());
        values.put(KEY_CODE, product.getCode());
        values.put(KEY_IMAGEN, product.getImage());
        values.put(KEY_COMPANY_ID, product.getCompany_id());
        values.put(KEY_CATEGORY_ID, product.getCategory_id());
        values.put(KEY_CATEGORY_NAME, product.getCategory_name());

        // insert row
        //long todo_id = db.insert(TABLE_PEDIDO, null, values);
        db.insert(TABLE_PRODUCTS, null, values);

        long todo_id = product.getId();
        return todo_id;
    }



    /*
     * get single User id
     */
    public Product getProduct(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS + " WHERE "
                + KEY_ID + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Product pd = new Product();
        pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        pd.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        pd.setCode((c.getString(c.getColumnIndex(KEY_CODE))));
        pd.setImage((c.getString(c.getColumnIndex(KEY_IMAGEN))));
        pd.setCompany_id((c.getInt(c.getColumnIndex(KEY_COMPANY_ID))));
        pd.setCategory_id((c.getInt(c.getColumnIndex(KEY_CATEGORY_ID))));
        pd.setCategory_name((c.getString(c.getColumnIndex(KEY_CATEGORY_NAME))));

        return pd;
    }

    /**
     *
     * @param name
     * @return
     */
    public Product getProductName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS + " WHERE "
                + KEY_NAME + " = " + name;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Product pd = new Product();
        pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        pd.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        pd.setCode((c.getString(c.getColumnIndex(KEY_CODE))));
        pd.setImage((c.getString(c.getColumnIndex(KEY_IMAGEN))));
        pd.setCompany_id((c.getInt(c.getColumnIndex(KEY_COMPANY_ID))));
        pd.setCategory_id((c.getInt(c.getColumnIndex(KEY_CATEGORY_ID))));
        pd.setCategory_name((c.getString(c.getColumnIndex(KEY_CATEGORY_NAME))));
        return pd;
    }

    /**
     *
     * @param code
     * @return
     */
    public Product getProductCode(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS + " WHERE "
                + KEY_CODE + " = " + code;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        Product pd = new Product();

        if (c.getCount() > 0) {
            c.moveToFirst();

            pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            pd.setName((c.getString(c.getColumnIndex(KEY_NAME))));
            pd.setCode((c.getString(c.getColumnIndex(KEY_CODE))));
            pd.setImage((c.getString(c.getColumnIndex(KEY_IMAGEN))));
            pd.setCompany_id((c.getInt(c.getColumnIndex(KEY_COMPANY_ID))));
            pd.setCategory_id((c.getInt(c.getColumnIndex(KEY_CATEGORY_ID))));
            pd.setCategory_name((c.getString(c.getColumnIndex(KEY_CATEGORY_NAME))));
        }
        return pd;

    }

    /**
     * getting all products
     * */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<Product>();
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Product pd = new Product();
                pd.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                pd.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                pd.setCode((c.getString(c.getColumnIndex(KEY_CODE))));
                pd.setImage((c.getString(c.getColumnIndex(KEY_IMAGEN))));
                pd.setCompany_id((c.getInt(c.getColumnIndex(KEY_COMPANY_ID))));
                pd.setCategory_id((c.getInt(c.getColumnIndex(KEY_CATEGORY_ID))));
                pd.setCategory_name((c.getString(c.getColumnIndex(KEY_CATEGORY_NAME))));
                // adding to todo list
                products.add(pd);
            } while (c.moveToNext());
        }
        return products;
    }
    /*
         * getting User count
         */
    public int getProductCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.e(LOG, countQuery);
        cursor.close();
        // return count
        return count;
    }
    /*
     * Updating a User
     */
    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, product.getName());
        values.put(KEY_CODE, product.getCode());
        values.put(KEY_COMPANY_ID, product.getCompany_id());
        values.put(KEY_IMAGEN, product.getImage());
        values.put(KEY_CATEGORY_ID, product.getCategory_id());
        values.put(KEY_CATEGORY_NAME, product.getCategory_name());


        // updating row
        return db.update(TABLE_PRODUCTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(product.getId()) });
    }


    /*
     * Deleting a User
     */
    public void deleteProduct(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /*
     * Deleting all User
     */
    public void deleteAllProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, null, null );

    }
//---------------------------------------------------------------//
    // ------------------------ "AUDIT" table methods ----------------//
    //------

    /**
     * Create Audit
     * @param audit
     * @return
     */
    public long createAudit(Audit audit) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, audit.getId());
        values.put(KEY_NAME, audit.getName());
        values.put(KEY_STORY_ID, audit.getStore_id());
        values.put(KEY_SCORE, audit.getScore());

        // insert row
        //long todo_id = db.insert(TABLE_PEDIDO, null, values);
        db.insert(TABLE_AUDITS, null, values);

        long todo_id = audit.getId();
        return todo_id;
    }
    public Audit getAudit(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_AUDITS + " WHERE "
                + KEY_ID + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Audit pd = new Audit();
        pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        pd.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        pd.setScore((c.getInt(c.getColumnIndex(KEY_SCORE))));
        pd.setStore_id((c.getInt(c.getColumnIndex(KEY_STORY_ID))));


        return pd;
    }

    /**
     * Update Audit
     * @param audit
     * @return
     */
    public int updateAudit(Audit audit) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, audit.getId());
        values.put(KEY_NAME, audit.getName());
        values.put(KEY_STORY_ID, audit.getStore_id());
        values.put(KEY_SCORE, audit.getScore());


        // updating row
        return db.update(TABLE_AUDITS, values, KEY_ID + " = ?", new String[] { String.valueOf(audit.getId()) });
    }

    /**
     * Delete all Audits
     */
    public void deleteAllAudits() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AUDITS, null, null );
    }

    /**
     * .Return List Audits
     * @return
     */
    public List<Audit> getAllAudits() {
        List<Audit> audit = new ArrayList<Audit>();
        String selectQuery = "SELECT  * FROM " + TABLE_AUDITS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Audit pd = new Audit();
                pd.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                pd.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                pd.setStore_id((c.getInt(c.getColumnIndex(KEY_STORY_ID))));
                pd.setScore((c.getLong(c.getColumnIndex(KEY_SCORE))));
                // adding to todo list
                audit.add(pd);
            } while (c.moveToNext());
        }
        return audit;
    }

    /**
     *
     * @param id
     * @return
     */

    public int getCountAuditForId(int id) {
        String countQuery = "SELECT  * FROM " + TABLE_PRESENCE_PRODUCTS + " WHERE " +  KEY_ID  + " = " + id ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.e(LOG, countQuery);
        cursor.close();
        // return count
        return count;
    }

    //---------------------------------------------------------------//
    // ------------------------ "PUBLICITY" table methods ----------------//
    //---------------------------------------------------------------//

    /**
     * Create publicity
     * @param publicity
     * @return
     */
    public long createPublicity(Publicity publicity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, publicity.getId());
        values.put(KEY_NAME, publicity.getName());
        values.put(KEY_ACTIVE, publicity.getActive());
        values.put(KEY_IMAGEN, publicity.getImage());
        values.put(KEY_COMPANY_ID, publicity.getCompany_id());
        values.put(KEY_CATEGORY_ID, publicity.getCategory_id());
        values.put(KEY_CATEGORY_NAME, publicity.getCategory_name());

        // insert row
        //long todo_id = db.insert(TABLE_PEDIDO, null, values);
        db.insert(TABLE_PUBLICITY, null, values);

        long todo_id = publicity.getId();
        return todo_id;
    }


    public Publicity getPublicity(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PUBLICITY + " WHERE "
                + KEY_ID + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Publicity pd = new Publicity();
        pd.setId(c.getInt((c.getColumnIndex(KEY_ID))));
        pd.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        pd.setActive((c.getInt(c.getColumnIndex(KEY_ACTIVE))));
        pd.setImage((c.getString(c.getColumnIndex(KEY_IMAGEN))));
        pd.setCompany_id((c.getInt(c.getColumnIndex(KEY_COMPANY_ID))));
        pd.setCategory_id((c.getInt(c.getColumnIndex(KEY_CATEGORY_ID))));
        pd.setCategory_name((c.getString(c.getColumnIndex(KEY_CATEGORY_NAME))));

        return pd;
    }

    /**
     * Get all publicity
     * @return ListPublicity
     */
    public List<Publicity> getAllPublicity() {
        List<Publicity> publicity = new ArrayList<Publicity>();
        String selectQuery = "SELECT  * FROM " + TABLE_PUBLICITY;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Publicity pd = new Publicity();
                pd.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                pd.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                pd.setActive((c.getInt(c.getColumnIndex(KEY_ACTIVE))));
                pd.setImage((c.getString(c.getColumnIndex(KEY_IMAGEN))));
                pd.setCompany_id((c.getInt(c.getColumnIndex(KEY_COMPANY_ID))));
                pd.setCategory_id((c.getInt(c.getColumnIndex(KEY_CATEGORY_ID))));
                pd.setCategory_name((c.getString(c.getColumnIndex(KEY_CATEGORY_NAME))));
                // adding to todo list
                publicity.add(pd);
            } while (c.moveToNext());
        }
        return publicity;
    }
    public int updatePublicity(Publicity publicity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, publicity.getName());
        values.put(KEY_ACTIVE, publicity.getActive());
        values.put(KEY_COMPANY_ID, publicity.getCompany_id());
        values.put(KEY_IMAGEN, publicity.getImage());
        values.put(KEY_CATEGORY_ID, publicity.getCategory_id());
        values.put(KEY_CATEGORY_NAME, publicity.getCategory_name());


        // updating row
        return db.update(TABLE_PUBLICITY, values, KEY_ID + " = ?",
                new String[] { String.valueOf(publicity.getId()) });
    }
    /**
     * Delete All Publicity
     */
    public void deleteAllPublicity() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PUBLICITY, null, null );

    }


    //---------------------------------------------------------------//
    // ------------------------ "PRESENSE PRODUCTS" table methods ----------------//
    //---------------------------------------------------------------//


    /**
     *
     * @param presenseProduct
     * @return is
     */
    public long createPresenseProduct(PresenceProduct presenseProduct) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //values.put(KEY_ID, product.getId());
        values.put(KEY_STORY_ID, presenseProduct.getStore_id());
        values.put(KEY_CATEGORY_ID, presenseProduct.getCategory_id());
        values.put(KEY_PRODUCT_ID, presenseProduct.getProduct_id());

        // insert row
        //long todo_id = db.insert(TABLE_PEDIDO, null, values);
        long id = db.insert(TABLE_PRESENCE_PRODUCTS, null, values);

        //long todo_id = presenseProduct.getId();
        return id;
    }

    /**
     * Return List Presenc Products
     * @return
     */
    public List<PresenceProduct> getAllPresenceProduct() {
        List<PresenceProduct> products = new ArrayList<PresenceProduct>();
        String selectQuery = "SELECT  * FROM " + TABLE_PRESENCE_PRODUCTS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                PresenceProduct pd = new PresenceProduct();
                pd.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                pd.setProduct_id((c.getInt(c.getColumnIndex(KEY_PRODUCT_ID))));

                pd.setCategory_id((c.getInt(c.getColumnIndex(KEY_CATEGORY_ID))));
                pd.setStore_id((c.getInt(c.getColumnIndex(KEY_STORY_ID))));
                // adding to todo list
                products.add(pd);
            } while (c.moveToNext());
        }
        return products;
    }

    /**
     * Retun total presence products selected
     * @return
     */
    public int getCountPresenseProduct() {
        String countQuery = "SELECT  * FROM " + TABLE_PRESENCE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.e(LOG, countQuery);
        cursor.close();
        // return count
        return count;
    }

    /**
     *
     * @param category_id
     * @return count
     */
    public int getCountPresenseProductForCategory(int category_id) {
        String countQuery = "SELECT  * FROM " + TABLE_PRESENCE_PRODUCTS + " WHERE " +  KEY_CATEGORY_ID  + " = " + category_id ;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.e(LOG, countQuery);
        cursor.close();
        // return count
        return count;
    }



    /**
     * Delete all Presense  Product
     */
    public void deleteAllPresenseProduct() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRESENCE_PRODUCTS, null, null );

    }

//---------------------------------------------------------------//
    // ------------------------ "PRESENSE PUBLYCITY" table methods ----------------//
    //---------------------------------------------------------------//



    public long createPresensePublicity(PresencePublicity presencePublicity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //values.put(KEY_ID, product.getId());
        values.put(KEY_STORY_ID, presencePublicity.getStore_id());
        values.put(KEY_CATEGORY_ID, presencePublicity.getCategory_id());
        values.put(KEY_PUBLICITY_ID, presencePublicity.getPublicity_id());
        values.put(KEY_FOUND, presencePublicity.getFound());
        values.put(KEY_VISIBLE, presencePublicity.getVisible());
        values.put(KEY_LAYOUT_CORRECT, presencePublicity.getLayout_correcto());


        // insert row
        //long todo_id = db.insert(TABLE_PEDIDO, null, values);
        long id = db.insert(TABLE_PRESENCE_PUBLICITY, null, values);

        //long todo_id = presenseProduct.getId();
        return id;
    }

    /**
     * Return List Presenc Products
     * @return
     */
    public List<PresencePublicity> getAllPresencePublicity() {
        List<PresencePublicity> publicities = new ArrayList<PresencePublicity>();
        String selectQuery = "SELECT  * FROM " + TABLE_PRESENCE_PUBLICITY;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                PresencePublicity pd = new PresencePublicity();
                pd.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                pd.setPublicity_id((c.getInt(c.getColumnIndex(KEY_PUBLICITY_ID))));
                pd.setCategory_id((c.getInt(c.getColumnIndex(KEY_CATEGORY_ID))));
                pd.setStore_id((c.getInt(c.getColumnIndex(KEY_STORY_ID))));
                pd.setFound((c.getInt(c.getColumnIndex(KEY_FOUND))));
                pd.setVisible((c.getInt(c.getColumnIndex(KEY_VISIBLE))));
                pd.setLayout_correcto((c.getInt(c.getColumnIndex(KEY_LAYOUT_CORRECT))));
                // adding to todo list
                publicities.add(pd);
            } while (c.moveToNext());
        }
        return publicities;
    }


    public List<PresencePublicity> getAllPresencePublicityGroupCategory() {
        List<PresencePublicity> publicities = new ArrayList<PresencePublicity>();
        String selectQuery = "SELECT  * FROM " + TABLE_PRESENCE_PUBLICITY + " GROUP BY " + KEY_CATEGORY_ID ;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                PresencePublicity pd = new PresencePublicity();
                pd.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                pd.setPublicity_id((c.getInt(c.getColumnIndex(KEY_PUBLICITY_ID))));
                pd.setCategory_id((c.getInt(c.getColumnIndex(KEY_CATEGORY_ID))));
                pd.setStore_id((c.getInt(c.getColumnIndex(KEY_STORY_ID))));
                pd.setFound((c.getInt(c.getColumnIndex(KEY_FOUND))));
                pd.setVisible((c.getInt(c.getColumnIndex(KEY_VISIBLE))));
                pd.setLayout_correcto((c.getInt(c.getColumnIndex(KEY_LAYOUT_CORRECT))));
                // adding to todo list
                publicities.add(pd);
            } while (c.moveToNext());
        }
        return publicities;
    }
    /**
     * Retun total presence products selected
     * @return
     */
    public int getCountPresensePublicity() {
        String countQuery = "SELECT  * FROM " + TABLE_PRESENCE_PUBLICITY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.e(LOG, countQuery);
        cursor.close();
        // return count
        return count;
    }

    /**
     *
     * @param category_id
     * @return count
     */
    public int getCountPresensePublicityForCategory(int category_id) {
        String countQuery = "SELECT  * FROM " + TABLE_PRESENCE_PUBLICITY + " WHERE " +  KEY_CATEGORY_ID  + " = " + category_id ;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.e(LOG, countQuery);
        cursor.close();
        // return count
        return count;
    }



    /**
     * Delete all Presense  Product
     */
    public void deleteAllPresensePublicity() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRESENCE_PUBLICITY, null, null );

    }



    public static boolean checkDataBase(Context context) {
        SQLiteDatabase checkDB = null;
        try {
            File database=context.getDatabasePath(DATABASE_NAME);
            if (database.exists()) {
                Log.i("Database", "Found");
                String myPath = database.getAbsolutePath();
                Log.i(LOG, myPath);
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
                //return true;
            } else {
                // Database does not exist so copy it from assets here
                Log.i(LOG, "Not Found");
                //return false;
            }
        } catch(SQLiteException e) {
            Log.i(LOG, "Not Found");
        } finally {
            if(checkDB != null) {
                checkDB.close();
            }
        }
        return checkDB != null ? true : false;
    }

}

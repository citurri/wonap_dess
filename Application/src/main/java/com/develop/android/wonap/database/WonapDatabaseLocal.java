package com.develop.android.wonap.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.develop.android.wonap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class WonapDatabaseLocal extends SQLiteOpenHelper {

    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreateValuesApp = "CREATE TABLE w_values_app (id INTEGER PRIMARY KEY, key  VARCHAR(50), value VARCHAR(255),create_date TIMESTAMP, write_date TIMESTAMP)";

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "WonapDatabaseLocal";

    private static String WEBSERVER = "";
    private static Context contexto;


    public WonapDatabaseLocal(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        WEBSERVER = context.getString(R.string.web_server);
        contexto = context;
    }

    public WonapDatabaseLocal(Context contexto, String nombre_db,
                              CursorFactory factory, int version) {

        super(contexto, nombre_db, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        //Log.v("DATABASE:", "Creando tabla USERS");
        db.execSQL(sqlCreateValuesApp);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS w_values_app");
        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreateValuesApp);
    }

    public String getValueApp(String key) {

        String query = "SELECT d.value FROM w_values_app d WHERE d.key = '"+key+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String value = "";
        if (cursor.moveToFirst()) {
            do {
                value = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        db.close();
        return value;
    }

    public String getLastWriteDateValueApp() {
        String last_write_date = "0000-00-00 00:00:00";
        SQLiteDatabase db = this.getWritableDatabase();
        //String[] args = new String[] {"usu1"};
        Cursor c = db.rawQuery("SELECT IFNULL(MAX(write_date),'0000-00-00 00:00:00') FROM w_values_app", null);
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                last_write_date = c.getString(0);
            } while (c.moveToNext());
        }
        db.close();
        return last_write_date.replace(" ", "X");
    }

    public void addUpdateValuesApp(String s) {
        try {
            JSONArray arr = new JSONArray(s);
            if (arr.length() != 0) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    //SE EJECUTA DE ACUERDO A LA ACTUALIZACION QUE SE NECESITE
                    SQLiteDatabase db = this.getWritableDatabase();
                    if (db != null) {
                        //Insertamos los datos en la tabla Usuarios
                        db.execSQL("INSERT OR REPLACE INTO w_values_app (id, key, value, create_date , write_date) " +
                                "VALUES (" + obj.getString("id") + ",'" + obj.getString("key") + "', '" + obj.getString("value") + "','" + obj.getString("create_date") + "','" + obj.getString("write_date") + "')");
                        db.close();
                    }
                    //Log.v("addUpdateNoticias:", obj.getString("id") + obj.getString("noticias_name"));

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

     public List<String> getLastWriteDates() {

        List<String> lastWriteDates = new LinkedList<>();
        lastWriteDates.add(getLastWriteDateValueApp());
        return lastWriteDates;
    }

    public void addUpdateAll(String[] s) {
        addUpdateValuesApp(s[0]);
    }

}


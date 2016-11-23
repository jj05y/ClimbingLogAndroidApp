package nils.and.lamp.app.Core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Vector;


public class ClimbDataBaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DataBaseOfClimbs";
    private static final int DATABASE_VERSION = 6;

    private static final String TABLE_CLIMBS = "ClimbsTable";
    private static final String TABLE_PICTURES = "PicturesTable";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_GRADE = "grade";
    private static final String KEY_LENGTH = "length";
    private static final String KEY_DESC = "desc";
    private static final String KEY_IMAGEKEY = "imagekey";
    private static final String KEY_IMAGE = "image";
    public static final String KEY_COORDS = "coords";


    public ClimbDataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CLIMB_TABLE =
                "CREATE TABLE " + TABLE_CLIMBS + " ( " +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        KEY_NAME + " TEXT, " +
                        KEY_GRADE + " TEXT, " +
                        KEY_LENGTH + " TEXT, " +
                        KEY_DESC + " TEXT, " +
                        KEY_IMAGEKEY + " TEXT, " +
                        KEY_COORDS + " TEXT " +
                        ")";

        db.execSQL(CREATE_CLIMB_TABLE);

        String CREATE_PIC_TABLE =
                "CREATE TABLE " + TABLE_PICTURES + " ( " +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        KEY_IMAGEKEY + " TEXT, " +
                        KEY_IMAGE + " BLOB " +
                        ")";

        db.execSQL(CREATE_PIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB", "Cleared Tables");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIMBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);
        onCreate(db);
    }

    //CRUD

    //Create
    public void addClimb(String name, String grade, String length, String desc, Bitmap image, String coords) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_GRADE, grade);
        values.put(KEY_LENGTH, length);
        values.put(KEY_DESC, desc);
        values.put(KEY_IMAGEKEY, name);
        values.put(KEY_COORDS, coords);
        db.insert(TABLE_CLIMBS, null, values);
        Log.d("DB", "put in: " + values);

        values = new ContentValues();
        values.put(KEY_IMAGEKEY, name);
        values.put(KEY_IMAGE, createByteArray(image));
        db.insert(TABLE_PICTURES, null, values);
        Log.d("DB", "put in image with key: " + name);
        db.close();
    }


    //Read

    public Vector<Climb> getClimbs() {
        Vector<Climb> climbsList = new Vector<>();
        String selectQuery = "SELECT * FROM " + TABLE_CLIMBS;
        Log.d(null, " select query " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount()>0 && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String grade = cursor.getString(cursor.getColumnIndex(KEY_GRADE));
                String length = cursor.getString(cursor.getColumnIndex(KEY_LENGTH));
                String desc = cursor.getString(cursor.getColumnIndex(KEY_DESC));
                String imagekey = cursor.getString(cursor.getColumnIndex(KEY_IMAGEKEY));
                String coords = cursor.getString(cursor.getColumnIndex(KEY_COORDS));
                climbsList.add(new Climb(imagekey ,name, grade, length, desc, coords));
            } while (cursor.moveToNext());
        }
        db.close();
        for (Climb climb : climbsList) {
            Log.d("DB", climb.toString());
        }
        return climbsList;
    }


    public Bitmap getPicture(String imageKey) {
        String selectQuery = "SELECT " + KEY_IMAGE + " FROM " + TABLE_PICTURES + " WHERE " + KEY_IMAGEKEY + "=\'" + imageKey + "\'";
        Log.d(null, " select query " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Bitmap image = null;

        if (cursor.moveToFirst()) {
            do {
                Log.d("DB", "trying to get image with key: " + imageKey);
                image = getBitmapFromBlob(cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE)));
                Log.d("DB", "Got image with key: " + imageKey);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return image;
    }

    public boolean isUnique(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + KEY_NAME +" FROM " + TABLE_CLIMBS + " WHERE " + KEY_NAME + "=\'" + name + "\'";
        Log.d(null, " select query " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        return  cursor.getCount() == 0;

    }


    //Update
    public void updateClimb(String name, String grade, String length, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_GRADE, grade);
        values.put(KEY_LENGTH, length);
        values.put(KEY_DESC, desc);
        db.update(TABLE_CLIMBS, values, KEY_NAME + "=\'" + name + "\'", null);
        Log.d("DB", "updated in: " + values);
        db.close();
    }

    //Delete
    public void deleteClimb(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_CLIMBS, KEY_NAME + " = \"" + name + "\"", null);
            db.delete(TABLE_PICTURES, KEY_NAME + " = \"" + name + "\"", null);
        } catch (SQLiteException e) {
            Log.d("DB", "Issues!");
        }
        db.close();
    }


       private byte[] createByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, stream);
        return stream.toByteArray();
    }

    private Bitmap getBitmapFromBlob(byte[] bytes ) {
        return BitmapFactory.decodeByteArray(
                bytes , 0,
                bytes.length);
    }

    public boolean isEmpty() {
        String selectQuery = "SELECT " + KEY_ID +" FROM " + TABLE_CLIMBS;
        Log.d("DB", " select query " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("DB", "Num rows in DB: " + cursor.getCount());
        return cursor.getCount() == 0;
    }

/*
    private byte[] getByteArrayFromUri(Uri image) {
        byte[] bArray = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream objOstream = new ObjectOutputStream(baos);
            objOstream.writeObject(image);
            bArray = baos.toByteArray();

        } catch (IOException e) {
            Log.d(null, "Problem in createByteArray");
            e.printStackTrace();
        }

        return bArray;
    }


    private Uri getUriFromBlob(byte[] bytes) {
        Uri uri = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            uri = (Uri) ois.readObject();
        } catch (IOException e) {
            Log.d(null, "Problem in decodeByteArray");
        } catch (ClassNotFoundException e) {
            Log.d(null, "Problem in decodeByteArray");
        }

        return uri;
    }*/

}

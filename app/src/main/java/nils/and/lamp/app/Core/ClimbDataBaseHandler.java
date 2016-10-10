package nils.and.lamp.app.Core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;


public class ClimbDataBaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DataBaseOfClimbs";
    private static final int DATABASE_VERSION = 8;
    private static final String TABLE_LABEL = "ClimbsTable";


    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_GRADE = "grade";
    private static final String KEY_LENGTH = "length";
    private static final String KEY_DESC = "desc";
    private static final String KEY_IMAGE = "image";


    public ClimbDataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LABEL_TABLE =
                "CREATE TABLE " + TABLE_LABEL + " ( " +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        KEY_NAME + " TEXT, " +
                        KEY_GRADE + " TEXT, " +
                        KEY_LENGTH + " TEXT, " +
                        KEY_DESC + " TEXT, " +
                        KEY_IMAGE + " BLOB " +
                        ")";

        db.execSQL(CREATE_LABEL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LABEL);
        onCreate(db);
    }

    //CRUD

    //Create
    public void addClimb(String name, String grade, String length, String desc, Uri image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_GRADE, grade);
        values.put(KEY_LENGTH, length);
        values.put(KEY_DESC, desc);
        values.put(KEY_IMAGE, getByteArrayFromUri(image));

        db.insert(TABLE_LABEL, null, values);
        Log.d("DB", "put in: " + values);
        db.close();
    }


    //Read

    public Vector<Climb> getClimbs() {
        Vector<Climb> climbsList = new Vector<>();
        String selectQuery = "SELECT * FROM " + TABLE_LABEL;
        Log.d(null, " select query " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount()>0 && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String grade = cursor.getString(cursor.getColumnIndex(KEY_GRADE));
                String length = cursor.getString(cursor.getColumnIndex(KEY_LENGTH));
                String desc = cursor.getString(cursor.getColumnIndex(KEY_DESC));
                Uri image = getUriFromBlob(cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE)));
                climbsList.add(new Climb(image ,name, grade, length, desc));
            } while (cursor.moveToNext());
        }
        db.close();
        for (Climb climb : climbsList) {
            Log.d("DB", climb.toString());
        }
        return climbsList;
    }


    //Update


    //Delete
    public void deleteClimb(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LABEL, KEY_NAME + " = \"" + name + "\"" , null);
        db.close();
    }


/*    private byte[] createByteArray(Drawable d) {
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bitmap = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private Drawable getBitmapFromBlob(byte[] bytes ) {
        Bitmap bitMapImage = BitmapFactory.decodeByteArray(
                bytes , 0,
                bytes.length);
        return new BitmapDrawable(bitMapImage);
    }*/

    public boolean isEmpty() {
        String selectQuery = "SELECT " + KEY_ID +" FROM " + TABLE_LABEL;
        Log.d("DB", " select query " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("DB", "Num rows in DB: " + cursor.getCount());
        return cursor.getCount() == 0;
    }


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
    }

}

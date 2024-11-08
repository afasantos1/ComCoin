package com.example.comcoin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "coinsdb";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "mycoins";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for our course name column
    private static final String NAME_COL = "name";

    // below variable id for our course duration column.
    private static final String DESCRIPTION_COL = "description";

    // below variable for our course description column.
    private static final String IMAGE_COL = "image";

    // below variable is for our course tracks column.
    private static final String ISCOMMEMORATIVE_COL = "is_commemorative";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating 
        // an sqlite query and we are 
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT,"
                + IMAGE_COL + " TEXT,"
                + ISCOMMEMORATIVE_COL + " BOOLEAN)";

        // at last we are calling a exec sql 
        // method to execute above sql query
        db.execSQL(query);
    }


    // this method is use to add new course to our sqlite database.
    public void addNewCoin(String coinName, String coinDescription, String coinImageURI, Boolean isCommemorative) {

        // on below line we are creating a variable for 
        // our sqlite database and calling writable method 
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a 
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values 
        // along with its key and value pair.
        values.put(NAME_COL, coinName);
        values.put(DESCRIPTION_COL, coinDescription);
        values.put(IMAGE_COL, coinImageURI);
        values.put(ISCOMMEMORATIVE_COL, isCommemorative);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // we have created a new method for reading all the courses.
    public ArrayList<Coins> readCoins()
    {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to
        // read data from database.
        Cursor cursorCoins
                = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<Coins> coinsArrayList
                = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorCoins.moveToFirst()) {
            do {
                // on below line we are adding the data from
                Boolean Com = cursorCoins.getInt(4) > 0;
                // cursor to our array list.
                coinsArrayList.add(new Coins(
                        cursorCoins.getString(1),
                        cursorCoins.getString(3),
                        cursorCoins.getString(2),
                        Com));

            } while (cursorCoins.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCoins.close();
        return coinsArrayList;
    }

    public void deleteCoin(String coinImage){
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_NAME, "image=?", new String[]{coinImage});
        db.close();
    }

    public void checkCoinExists(String name, String image) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE name=? AND image=?", new String[]{name, image});

        if (cursor.getCount() > 0) {
            Log.d("CheckCoin", "Coin exists in the database.");
        } else {
            Log.d("CheckCoin", "Coin does not exist in the database.");
        }

        cursor.close();
    }


    public void updateCoin(String originalName, String originalImg, String novoNome, String novaDesc, String novaImgURI, boolean novoCom) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the coin exists first
        checkCoinExists(originalName, originalImg);

        ContentValues values = new ContentValues();
        values.put(NAME_COL, novoNome);
        values.put(IMAGE_COL, novaImgURI);
        values.put(DESCRIPTION_COL, novaDesc);
        values.put(ISCOMMEMORATIVE_COL, novoCom ? 1 : 0); // Assuming you store BOOLEAN as INTEGER

        int rowsUpdated = db.update(TABLE_NAME, values, "name=? AND image=?", new String[]{originalName, originalImg});
        Log.d("ROWS UPDATED", Integer.toString(rowsUpdated));
        db.close();
    }


}
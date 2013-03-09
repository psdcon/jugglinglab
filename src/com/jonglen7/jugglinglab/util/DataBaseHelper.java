// http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/

package com.jonglen7.jugglinglab.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{

    private String DATABASE_PATH;
	private static String DATABASE_NAME = "BDD.db";
	private static final int DATABASE_VERSION = 1; // To be changed when modifying the database
	private SQLiteDatabase myDataBase; 

	private final Context myContext;

	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
        this.DATABASE_PATH = context.getDatabasePath(DATABASE_NAME).getPath();
	}	

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 */
	public void createDataBase() throws IOException{
		boolean dbExist = checkDataBase();
		if(dbExist){
			//do nothing - database already exist
		}else{
			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase(){
		SQLiteDatabase checkDB = null;
		try{
			checkDB = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){
			//database does't exist yet.
		}
 
		if(checkDB != null){
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException{
		//Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

		//Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(DATABASE_PATH);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException{
		//Open the database
		myDataBase = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close() {
		if(myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	    // When a user first installs the application, we apply all the upgrades
	    this.onUpgrade(db, 1, -1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//	    TODO Romain (Database): Choose between the 2 possible ways to update the database
//	    1/ Write the SQL queries here
//	    2/ Write them in separate files, let Android find them and run them
//	       For example, have 2.sql and 3.sql in the assets folder and run the
//	       files that need to be, so if oldVersion = 2, only run 3.sql
//	    
//	    switch(oldVersion) {
//	    case 1:
//	        // Do the update here...
//	        // We want both updates, so no break statement here
//	    case 2:
//	    }
//	    
//	    try {
//            myContext.getAssets().list(myContext.getFilesDir().getAbsolutePath());
//            // Test if this command works first and if it does, keep the list of
//            // files that we are insterested in (*.sql).
//            // Then, use their names (2.sql, 3.sql, ...) to know which ones need
//            // to be run
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
	}

	// Add your public helper methods to access and get content from the database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
	// to you to create adapters for your views.

	public Cursor execQuery(String query) {
		Cursor cursor = null;
		try{
			cursor = myDataBase.rawQuery(query, null);
	    }catch(SQLException sqle){
	 		throw sqle;
	    }
		return cursor;
	}

	public static DataBaseHelper init(Context context) {
		DataBaseHelper myDbHelper = new DataBaseHelper(context);
		
	    try {
	    	myDbHelper.createDataBase();
	 	} catch (IOException ioe) {
	 		throw new Error("Unable to create database");
	 	}
	 
	 	try {
	 		myDbHelper.openDataBase();
	 	}catch(SQLException sqle){
	 		throw sqle;
	 	}
	
		return myDbHelper;
	}
	
}

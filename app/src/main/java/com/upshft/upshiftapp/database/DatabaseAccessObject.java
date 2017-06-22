package com.upshft.upshiftapp.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

//creation of database object that can be extended by RevenueDAO for CRUD functionality
public class DatabaseAccessObject {

	protected static SQLiteDatabase database;
	protected DatabaseHelper databaseHelper;
	protected Context context;

	public DatabaseAccessObject(Context context) {
		this.context = context;
		this.databaseHelper = DatabaseHelper.getHelper(context);

		open();
	}

	public void open() throws SQLException {
		if(databaseHelper == null)
			databaseHelper = DatabaseHelper.getHelper(context);

		database = databaseHelper.getWritableDatabase();
	}
	
	public void close() {
		databaseHelper.close();
		database = null;
	}
}

package com.upshft.upshiftapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.upshft.upshiftapp.database.deadheads.DeadheadTable;
import com.upshft.upshiftapp.database.expenses.ExpenseTable;
import com.upshft.upshiftapp.database.revenues.RevenueTable;
import com.upshft.upshiftapp.database.shifts.ShiftTable;

// main database helper to set up sqlite db table columns and datatypes. manages creation and version management
public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "upshift";
	private static final int DATABASE_VERSION = 1;

	private static DatabaseHelper instance;

	public static synchronized DatabaseHelper getHelper(Context context) {
		if (instance == null)
			instance = new DatabaseHelper(context);
		return instance;
	}

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onOpen(SQLiteDatabase database) {
		super.onOpen(database);

		if (!database.isReadOnly()) {
			// Enable foreign key constraints
			database.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(ShiftTable.CREATE_TABLE);
		database.execSQL(DeadheadTable.CREATE_TABLE);
		database.execSQL(RevenueTable.CREATE_TABLE);
		database.execSQL(ExpenseTable.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if(oldVersion == 1)
		{
			// modify database and upgrade to version 2
		}

		if(oldVersion == 2)
		{
			// modify database and upgrade to version 3
		}

		// ... other version changes until converted to the current version
	}
}

package com.upshft.upshiftapp.database;

public abstract class KeyedTable
{
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_ID_DATATYPE = "INTEGER PRIMARY KEY";

	public static final String WHERE_ID_EQUALS = String.format("%s = ?", COLUMN_ID);

	public abstract String getTableName();
}

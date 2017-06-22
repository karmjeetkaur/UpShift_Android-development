package com.upshft.upshiftapp.database.deadheads;

import com.upshft.upshiftapp.database.KeyedTable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DeadheadTable extends KeyedTable
{
	public static final String TABLE_NAME = "deadheads";

	public static final String COLUMN_START = "start";
	public static final String COLUMN_START_DATATYPE = "DATE";
	public static final SimpleDateFormat COLUMN_START_FORMATTER = new SimpleDateFormat("M/d/yyyy h:mm:ss.SSS a z", Locale.ENGLISH);

	public static final String COLUMN_END = "end";
	public static final String COLUMN_END_DATATYPE = "DATE";
	public static final SimpleDateFormat COLUMN_END_FORMATTER = new SimpleDateFormat("M/d/yyyy h:mm:ss.SSS a z", Locale.ENGLISH);

	public static final String COLUMN_DISTANCE = "distance";
	public static final String COLUMN_DISTANCE_DATATYPE = "DOUBLE";

	public static final String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME + " ("
			+ COLUMN_ID + " " + COLUMN_ID_DATATYPE + ", "
			+ COLUMN_START + " " + COLUMN_START_DATATYPE + ", "
			+ COLUMN_END + " " + COLUMN_END_DATATYPE + ", "
			+ COLUMN_DISTANCE + " " + COLUMN_DISTANCE_DATATYPE + ");";

	@Override
	public String getTableName()
	{
		return TABLE_NAME;
	}
}

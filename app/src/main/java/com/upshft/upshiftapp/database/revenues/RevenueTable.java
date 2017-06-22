package com.upshft.upshiftapp.database.revenues;

import com.upshft.upshiftapp.database.KeyedTable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RevenueTable extends KeyedTable
{
	public static final String TABLE_NAME = "revenues";

	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_DATE_DATATYPE = "DATE";
	public static final SimpleDateFormat COLUMN_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

	public static final String COLUMN_PLATFORM = "platform";
	public static final String COLUMN_PLATFORM_DATATYPE = "TEXT";

	public static final String COLUMN_AMOUNT = "amount";
	public static final String COLUMN_AMOUNT_DATATYPE = "DOUBLE";

	public static final String COLUMN_TIP = "tip";
	public static final String COLUMN_TIP_DATATYPE = "DOUBLE";

	public static final String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME + " ("
					+ COLUMN_ID + " " + COLUMN_ID_DATATYPE + ", "
					+ COLUMN_DATE + " " + COLUMN_DATE_DATATYPE + ", "
					+ COLUMN_PLATFORM + " " + COLUMN_PLATFORM_DATATYPE + ", "
					+ COLUMN_AMOUNT + " " + COLUMN_AMOUNT_DATATYPE + ", "
					+ COLUMN_TIP + " " + COLUMN_TIP_DATATYPE + ");";

	@Override
	public String getTableName()
	{
		return TABLE_NAME;
	}
}

package com.upshft.upshiftapp.database.shifts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.upshft.upshiftapp.database.RecordDAO;
import java.util.Date;

//implementation of database operations
public class ShiftRecordDAO extends RecordDAO<ShiftTable, ShiftRecord>
{


	public ShiftRecordDAO(Context context)
	{
		super(context, new ShiftTable());
	}

	@Override
	protected ContentValues getContentValues(ShiftRecord shiftRecord)
	{
		ContentValues values = new ContentValues();
		values.put(ShiftTable.COLUMN_START, shiftRecord.getStart().getTime());
		values.put(ShiftTable.COLUMN_END, shiftRecord.getEnd().getTime());
		return values;
	}

	@Override
	protected ShiftRecord readRecord(Cursor cursor)
	{
		ShiftRecord shiftRecord = null;
		shiftRecord = new ShiftRecord();
		shiftRecord.setId(cursor.getInt(0));
		shiftRecord.setStart(new Date(cursor.getLong(1)));
		shiftRecord.setEnd(new Date(cursor.getLong(2)));
		return shiftRecord;
	}
}

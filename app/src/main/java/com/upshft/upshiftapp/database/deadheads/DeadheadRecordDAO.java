package com.upshft.upshiftapp.database.deadheads;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.upshft.upshiftapp.database.RecordDAO;
import java.util.Date;

//implementation of database operations
public class DeadheadRecordDAO extends RecordDAO<DeadheadTable, DeadheadRecord> {
	public DeadheadRecordDAO(Context context) {
		super(context, new DeadheadTable());
	}

	@Override
	protected ContentValues getContentValues(DeadheadRecord deadheadRecord) {
		ContentValues values = new ContentValues();
		values.put(DeadheadTable.COLUMN_START, deadheadRecord.getStart().getTime());
		values.put(DeadheadTable.COLUMN_END, deadheadRecord.getEnd().getTime());
		values.put(DeadheadTable.COLUMN_DISTANCE, deadheadRecord.getDistance());
		return values;
	}

	@Override
	protected DeadheadRecord readRecord(Cursor cursor)
	{
		DeadheadRecord deadheadRecord = null;
		deadheadRecord = new DeadheadRecord();
		deadheadRecord.setId(cursor.getInt(0));
		deadheadRecord.setStart(new Date(cursor.getLong(1)));
		deadheadRecord.setEnd(new Date(cursor.getLong(2)));
		deadheadRecord.setDistance(cursor.getDouble(3));
		return deadheadRecord;
	}
}

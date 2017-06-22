package com.upshft.upshiftapp.database.revenues;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.upshft.upshiftapp.database.RecordDAO;
import java.util.Date;

//implementation of database operations
public class RevenueRecordDAO extends RecordDAO<RevenueTable, RevenueRecord> {
	public RevenueRecordDAO(Context context){
		super(context, new RevenueTable());
	}

	@Override
	protected ContentValues getContentValues(RevenueRecord revenueRecord)
	{
		ContentValues values = new ContentValues();
		values.put(RevenueTable.COLUMN_DATE, revenueRecord.getDate().getTime());
		values.put(RevenueTable.COLUMN_PLATFORM, revenueRecord.getPlatform());
		values.put(RevenueTable.COLUMN_AMOUNT, revenueRecord.getAmount());
		values.put(RevenueTable.COLUMN_TIP, revenueRecord.getTip());
		return values;
	}

	@Override
	protected RevenueRecord readRecord(Cursor cursor)
	{
		RevenueRecord revenueRecord = null;
		revenueRecord = new RevenueRecord();
		revenueRecord.setId(cursor.getInt(0));
		revenueRecord.setDate(new Date(cursor.getLong(1)));
		revenueRecord.setPlatform(cursor.getString(2));
		revenueRecord.setAmount(cursor.getDouble(3));
		revenueRecord.setTip(cursor.getDouble(4));
		return revenueRecord;
	}
}

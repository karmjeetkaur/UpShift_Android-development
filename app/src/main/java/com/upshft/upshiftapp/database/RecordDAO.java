package com.upshft.upshiftapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

//implementation of database operations
public abstract class RecordDAO<Table extends KeyedTable, Record extends KeyedRecord> extends DatabaseAccessObject {
	private Table table;

	public RecordDAO(Context context, Table table)
	{
		super(context);

		this.table = table;
	}

	public long insert(Record record) {
		ContentValues values = getContentValues(record);

		return database.insert(table.getTableName(), null, values);
	}

	public long update(Record record) {
		ContentValues values = getContentValues(record);

		return database.update(table.getTableName(), values, Table.WHERE_ID_EQUALS, new String[]{String.valueOf(record.getId())});
	}

	protected abstract ContentValues getContentValues(Record record);

	// delete db entry currently implemented by a long click on the list view entry in activity_main/activity_revenue_record_list, used in
	public int delete(Record record) {
		return database.delete(table.getTableName(), Table.WHERE_ID_EQUALS, new String[]{String.valueOf(record.getId())});
	}

	//USING query() method to move cursor through db entries
	public ArrayList<Record> getRecords()
	{
		ArrayList<Record> records = new ArrayList<Record>();

		Cursor cursor = database.query(table.getTableName(),
				null,
				null,
				null,
				null,
				null,
				null);

		while (cursor.moveToNext()) {
			Record record = readRecord(cursor);

			records.add(record);
		}

		return records;
	}
	
	//Retrieves a single employee record with the given id
	public Record getRecord(long id) {
		Cursor cursor = database.query(table.getTableName(),
				null,
				null,
				null,
				null,
				null,
				null);

		if(cursor.moveToNext())
		{
			return readRecord(cursor);
		}

		return null;
	}

	/**
	 *
	 * @param cursor - a cursor set on a row with all columns selected in declared order
	 * @return - a Record reflecting the cursor row position
	 */
	protected abstract Record readRecord(Cursor cursor);
}

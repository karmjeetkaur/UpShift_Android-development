package com.upshft.upshiftapp.database.expenses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.upshft.upshiftapp.database.RecordDAO;

import java.util.Date;

//implementation of database operations
public class ExpenseRecordDAO extends RecordDAO<ExpenseTable, ExpenseRecord> {
	public ExpenseRecordDAO(Context context){
		super(context, new ExpenseTable());
	}

	@Override
	protected ContentValues getContentValues(ExpenseRecord expenseRecord) {
		ContentValues values = new ContentValues();
		values.put(ExpenseTable.COLUMN_DATE, expenseRecord.getDate().getTime());
		values.put(ExpenseTable.COLUMN_CATEGORY, expenseRecord.getCategory());
		values.put(ExpenseTable.COLUMN_AMOUNT, expenseRecord.getAmount());
		values.put(ExpenseTable.COLUMN_DESCRIPTION, expenseRecord.getDescription());
		return values;
	}

	@Override
	protected ExpenseRecord readRecord(Cursor cursor)
	{
		ExpenseRecord expenseRecord = null;
		expenseRecord = new ExpenseRecord();
		expenseRecord.setId(cursor.getInt(0));
		expenseRecord.setDate(new Date(cursor.getLong(1)));
		expenseRecord.setCategory(cursor.getString(2));
		expenseRecord.setAmount(cursor.getDouble(3));
		expenseRecord.setDescription(cursor.getString(4));
		return expenseRecord;
	}
}

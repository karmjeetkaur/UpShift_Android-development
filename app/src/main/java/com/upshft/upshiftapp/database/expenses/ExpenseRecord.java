package com.upshft.upshiftapp.database.expenses;

import android.os.Parcel;
import com.upshft.upshiftapp.database.KeyedRecord;
import java.util.Date;

public class ExpenseRecord extends KeyedRecord {
    private Date date;
    private String category;
    private double amount;
    private String description;
    private long date_time;
    String total_amount;
    String clueKey;

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public ExpenseRecord() {
        super();
    }

    public ExpenseRecord(long date_time, String category, String total_amount, String description) {
        this.date_time = date_time;
        this.category = category;
        this.total_amount = total_amount;
        this.description = description;
    }


    private ExpenseRecord(Parcel in) {
        super(in);

        this.date = new Date(in.readLong());
        this.category = in.readString();
        this.amount = in.readDouble();
        this.description = in.readString();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("ExpenseRecord {super=%s, date=%s, category=%s, amount=%f, description=%s}", super.toString(), date, category, amount, description);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getId();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        ExpenseRecord other = (ExpenseRecord) obj;
        if (!date.equals(other.date))
            return false;
        if (!category.equals(other.category))
            return false;
        if (amount != other.amount)
            return false;
        if (!description.equals(other.description))
            return false;

        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeLong(getDate().getTime());
        parcel.writeString(getCategory());
        parcel.writeDouble(getAmount());
        parcel.writeString(getDescription());
    }

    public static final Creator<ExpenseRecord> CREATOR = new Creator<ExpenseRecord>() {
        public ExpenseRecord createFromParcel(Parcel in) {
            return new ExpenseRecord(in);
        }

        public ExpenseRecord[] newArray(int size) {
            return new ExpenseRecord[size];
        }
    };

    public long getDate_time() {
        return date_time;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }

    public String getClueKey() {
        return clueKey;
    }

    public void setClueKey(String clueKey) {
        this.clueKey = clueKey;
    }
}

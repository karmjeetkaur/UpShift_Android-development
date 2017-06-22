package com.upshft.upshiftapp.database.revenues;

import android.os.Parcel;
import android.util.Log;
import com.upshft.upshiftapp.database.KeyedRecord;
import java.util.Date;

public class RevenueRecord extends KeyedRecord {
	private Date date;
	private String platform;
	private double amount;
	private double tip;
	private long date_time;
	String total_amount;
	String total_tip;
	String clueKey;

	public String getTotal_tip() {
		return total_tip;
	}

	public void setTotal_tip(String total_tip) {
		this.total_tip = total_tip;
	}

	public RevenueRecord() {
		super();
	}

	public RevenueRecord(long date_time, String platform, String total_amount,String total_tip)
	{
		this.date_time = date_time;
		this.platform = platform;
		this.total_amount = total_amount;
		this.total_tip = total_tip;
	}


	private RevenueRecord(Parcel in) {
		super(in);
		this.date = new Date(in.readLong());
		this.platform = in.readString();
		this.amount = in.readDouble();
		this.tip = in.readDouble();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platorm) {
		this.platform = platorm;

		Log.e("RevenueRecords", "--platorm----"+platorm);
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getTip() {
		return tip;
	}

	public void setTip(double tip) {
		this.tip = tip;
	}

	@Override
	public String toString() {
		return String.format("RevenueRecord {super=%s, date=%s, platform=%s, amount=%f, tip=%f}", super.toString(), date, platform, amount, tip);
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
		if(!super.equals(obj))
			return false;
		RevenueRecord other = (RevenueRecord) obj;
		if (!date.equals(other.date))
			return false;
		if (!platform.equals(other.platform))
			return false;
		if (amount != other.amount)
			return false;
		if (tip != other.tip)
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
		parcel.writeString(getPlatform());
		parcel.writeDouble(getAmount());
		parcel.writeDouble(getTip());
	}

	public static final Creator<RevenueRecord> CREATOR = new Creator<RevenueRecord>() {
		public RevenueRecord createFromParcel(Parcel in) {
			return new RevenueRecord(in);
		}

		public RevenueRecord[] newArray(int size) {
			return new RevenueRecord[size];
		}
	};
	public long getDate_time() {
		return date_time;
	}

	public void setDate_time(long date_time) {
		this.date_time = date_time;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public String getClueKey() {
		return clueKey;
	}

	public void setClueKey(String clueKey) {
		this.clueKey = clueKey;
	}
}

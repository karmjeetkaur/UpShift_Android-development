package com.upshft.upshiftapp.database.shifts;

import android.os.Parcel;

import com.upshft.upshiftapp.database.KeyedRecord;
import java.util.Date;

public class ShiftRecord extends KeyedRecord {
	private Date start;
	private Date end;

	public long startDate;
	public long endDate;

	public ShiftRecord() {
		super();
	}

	private ShiftRecord(Parcel in) {
		super(in);
		this.start = new Date(in.readLong());
		this.end = new Date(in.readLong());
	}

	public ShiftRecord(Date start_time, Date end_time)
	{
		this.start = start_time;
		this.end =  end_time;
	}


	public ShiftRecord(long startDate, long endDate)
	{
		this.startDate = startDate;
		this.endDate = endDate;
	}

//	public java.util.Map<String, String> getCreationDate() {
//		return ServerValue.TIMESTAMP;
//	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

//	@Override
//	public String toString() {
//		return String.format("ShiftRecord{start=%s, end=%s}", start, end);
//	}

	@Override
	public String toString() {
		return String.format("ShiftRecord {super=%s, start=%s, end=%s}", super.toString(), start, end);
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
		ShiftRecord other = (ShiftRecord) obj;
		if (!start.equals(other.start))
			return false;
		if (!end.equals(other.end))
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
		parcel.writeLong(getStart().getTime());
		parcel.writeLong(getEnd().getTime());
	}

	public static final Creator<ShiftRecord> CREATOR = new Creator<ShiftRecord>() {
		public ShiftRecord createFromParcel(Parcel in) {
			return new ShiftRecord(in);
		}

		public ShiftRecord[] newArray(int size) {
			return new ShiftRecord[size];
		}
	};

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
}

package com.upshft.upshiftapp.database.deadheads;

import android.os.Parcel;
import com.upshft.upshiftapp.database.KeyedRecord;
import java.util.Date;

public class DeadheadRecord extends KeyedRecord
{
	private Date start;
	private Date end;
	private double distance;
	public long startDate;
	public long endDate;

	public DeadheadRecord() {
		super();
	}


	public DeadheadRecord(long startDate, long endDate, Double distance)
	{
		this.startDate = startDate;
		this.endDate = endDate;
		this.distance = distance;
	}

	private DeadheadRecord(Parcel in) {
		super(in);

		this.start = new Date(in.readLong());
		this.end = new Date(in.readLong());
		this.distance = in.readDouble();
	}

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

	public double getDistance()
	{
		return distance;
	}

	public void setDistance(double distance)
	{
		this.distance = distance;
	}

	@Override
	public String toString() {
		return String.format("DeadheadRecord {super=%s, start=%s, end=%s, distance=%s}", super.toString(), start, end, distance);
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
		DeadheadRecord other = (DeadheadRecord) obj;
		if (!start.equals(other.start))
			return false;
		if (!end.equals(other.end))
			return false;
		if (distance != other.distance)
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
		parcel.writeDouble(getDistance());
	}

	public static final Creator<DeadheadRecord> CREATOR = new Creator<DeadheadRecord>() {
		public DeadheadRecord createFromParcel(Parcel in) {
			return new DeadheadRecord(in);
		}

		public DeadheadRecord[] newArray(int size) {
			return new DeadheadRecord[size];
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

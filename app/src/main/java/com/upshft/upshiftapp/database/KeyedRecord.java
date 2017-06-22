package com.upshft.upshiftapp.database;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class KeyedRecord implements Parcelable {
	private int id;

	public KeyedRecord() {
		super();
	}

	protected KeyedRecord(Parcel in) {
		super();

		this.id = in.readInt();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("KeyedRecord {id=%d}", id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyedRecord other = (KeyedRecord) obj;
		if (id != other.id)
			return false;

		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(getId());
	}
}

package com.example.geoexplorer;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Continent implements Parcelable {

    //variables
    private String continent;
    private ArrayList<Country> countries;


    //constructor
    public Continent(String continent) {
        this.continent = continent;
        this.countries = new ArrayList<>();
    }

    @Override
    public String toString() {
        return continent;
    }

    //mehtods added to make the class parcable
    protected Continent(Parcel in) {
        continent = in.readString();
        countries = in.createTypedArrayList(Country.CREATOR);
    }

    public static final Creator<Continent> CREATOR = new Creator<Continent>() {
        @Override
        public Continent createFromParcel(Parcel in) {
            return new Continent(in);
        }

        @Override
        public Continent[] newArray(int size) {
            return new Continent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(continent);
        dest.writeTypedList(countries);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Continent other = (Continent) obj;
        // Compare continent names, ignoring case and whitespace
        return continent.equalsIgnoreCase(other.continent);
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }

    public void setCountries(ArrayList<Country> countries) {
        this.countries = countries;
    }
}

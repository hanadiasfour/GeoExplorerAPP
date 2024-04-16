package com.example.geoexplorer;

import android.os.Parcel;
import android.os.Parcelable;

public class Country implements Parcelable {

    private String id, name, capital, religion, language, independance, population,continent;
    private boolean fav;

    public Country(String id, String name, String capital, String religion, String language, String population,
                   String independance, String continent) {
        this.id = id;
        this.name = name;
        this.capital = capital;
        this.religion = religion;
        this.language = language;
        this.population = population;
        this.independance = independance;
        this.continent = continent;
        this.fav = false;
    }

    public Country(String name){
        this.name = name;
    }



    protected Country(Parcel in) {
        id = in.readString();
        name = in.readString();
        capital = in.readString();
        religion = in.readString();
        language = in.readString();
        independance = in.readString();
        population = in.readString();
        continent = in.readString();
        fav = in.readByte() != 0; // fav == true if byte != 0
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(capital);
        dest.writeString(religion);
        dest.writeString(language);
        dest.writeString(independance);
        dest.writeString(population);
        dest.writeString(continent);
        dest.writeByte((byte) (fav ? 1 : 0)); // if fav == true, byte == 1
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Country country = (Country) obj;
        return name != null && name.equalsIgnoreCase(country.name);
    }


    @Override
    public String toString() {
        return name;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }


    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getIndependance() {
        return independance;
    }

    public void setIndependance(String independance) {
        this.independance = independance;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }
}

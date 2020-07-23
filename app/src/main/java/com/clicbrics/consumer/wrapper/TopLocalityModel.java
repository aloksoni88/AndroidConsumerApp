package com.clicbrics.consumer.wrapper;

public class TopLocalityModel{
    public long id;
    public long cityId;
    public String cityName;
    public int rank;
    public String name;

    /*public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/



    @Override
    public String toString() {
        return "CityName: " + cityName + ", " + "CityId: " + cityId + ", " + " Name: " + name;
    }

    /*@Override
    public int compareTo(TopLocalityModel topLocalityModel) {
        return -Integer.valueOf(rank).compareTo(topLocalityModel.rank);
    }*/
}

package com.clicbrics.consumer.utils;

import android.graphics.Bitmap;

/**
 * Created by Alok on 16-06-2016.
 */
public class CountryDto implements Comparable<CountryDto> {
    private String countryName="";
    private String flagName="";
    private String countryCode="";
    private Bitmap flagImage;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getFlagName() {
        return flagName;
    }

    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String toString(){
        return countryName;
    }

    public Bitmap getFlagImage() {
        return flagImage;
    }

    public void setFlagImage(Bitmap flagImage) {
        this.flagImage = flagImage;
    }

    public boolean equals(Object objects){
        if(objects == this){
            return true;
        }
        if(!(objects instanceof CountryDto)){
            return false;
        }
        CountryDto countryDto = (CountryDto) objects;
        return (countryName == countryDto.countryName || (countryName != null && countryName.equals(countryDto.getCountryName())));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((countryName == null) ? 0 : countryName.hashCode());
        return result;
    }



    @Override
    public int compareTo(CountryDto countryDto) {
        return this.countryName.compareTo(countryDto.getCountryName());
    }
}

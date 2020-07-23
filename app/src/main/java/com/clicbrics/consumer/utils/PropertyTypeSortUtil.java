package com.clicbrics.consumer.utils;

import com.buy.housing.backend.propertyEndPoint.model.PropertyType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Alok on 19-08-2018.
 */
public class PropertyTypeSortUtil {

    public List<PropertyType> getSortedProperty(List<PropertyType> propertyTypes){
        if(propertyTypes == null){
            return propertyTypes;
        }
        List<PropertyType> propertyTypeList = new ArrayList<>();
        List<PropertyType> bhkTypeList = new ArrayList<>();
        List<PropertyType> studioTypeList = new ArrayList<>();
        List<PropertyType> plotTypeList = new ArrayList<>();
        List<PropertyType> commercialTypeList = new ArrayList<>();
        List<PropertyType> otherList = new ArrayList<>();
        propertyTypeList.addAll(propertyTypes);
        if(propertyTypeList != null && propertyTypeList.size() > 1) {
            for(int i=0; i<propertyTypeList.size(); i++){
                PropertyType propertyType = propertyTypeList.get(i);
                if(propertyType.getNumberOfBedrooms() != 0){
                    bhkTypeList.add(propertyType);
                }else if(propertyType.getType() != null && propertyType.getType().equalsIgnoreCase("Studio")){
                    studioTypeList.add(propertyType);
                }else if(propertyType.getType().equalsIgnoreCase("land")){
                    plotTypeList.add(propertyType);
                }else if(UtilityMethods.isCommercial(propertyType.getType())){
                    commercialTypeList.add(propertyType);
                }else{
                    otherList.add(propertyType);
                }
            }
            if(bhkTypeList != null && bhkTypeList.size() > 1){
                Collections.sort(bhkTypeList, new Comparator<PropertyType>() {
                    @Override
                    public int compare(PropertyType propertyType1, PropertyType propertyType2) {
                        if(propertyType1.getNumberOfBedrooms() != propertyType2.getNumberOfBedrooms()) {
                            return propertyType1.getNumberOfBedrooms().compareTo(propertyType2.getNumberOfBedrooms());
                        }else{
                            return propertyType1.getSuperArea().compareTo(propertyType2.getSuperArea());
                        }
                    }
                });
            }

            if(studioTypeList != null && studioTypeList.size() > 1){
                Collections.sort(studioTypeList, new Comparator<PropertyType>() {
                    @Override
                    public int compare(PropertyType propertyType1, PropertyType propertyType2) {
                        return propertyType1.getSuperArea().compareTo(propertyType2.getSuperArea());
                    }
                });
            }

            if(plotTypeList != null && plotTypeList.size() > 1){
                Collections.sort(plotTypeList, new Comparator<PropertyType>() {
                    @Override
                    public int compare(PropertyType propertyType1, PropertyType propertyType2) {
                        if(propertyType1.getSuperArea() != propertyType2.getSuperArea()) {
                            return propertyType1.getSuperArea().compareTo(propertyType2.getSuperArea());
                        }else{
                            long price1 = propertyType1.getBsp()* propertyType1.getSuperArea();
                            long price2 = propertyType2.getBsp()* propertyType2.getSuperArea();
                            return  (int)(price1-price2);
                        }
                    }
                });
            }
            if(commercialTypeList != null && commercialTypeList.size() > 1){
                Collections.sort(commercialTypeList, new Comparator<PropertyType>() {
                    @Override
                    public int compare(PropertyType propertyType1, PropertyType propertyType2) {
                        if(propertyType1.getSuperArea() != propertyType2.getSuperArea()) {
                            return propertyType1.getSuperArea().compareTo(propertyType2.getSuperArea());
                        }else{
                            long price1 = propertyType1.getBsp()* propertyType1.getSuperArea();
                            long price2 = propertyType2.getBsp()* propertyType2.getSuperArea();
                            return  (int)(price1-price2);
                        }
                    }
                });
            }

            if(propertyTypeList != null){
                propertyTypeList.clear();
            }
            propertyTypeList = new ArrayList<>();
            propertyTypeList.addAll(studioTypeList);
            propertyTypeList.addAll(bhkTypeList);
            propertyTypeList.addAll(plotTypeList);
            propertyTypeList.addAll(commercialTypeList);
            if(otherList != null && !otherList.isEmpty()){
                Collections.sort(otherList, new Comparator<PropertyType>() {
                    @Override
                    public int compare(PropertyType propertyType1, PropertyType propertyType2) {
                        return propertyType1.getSuperArea().compareTo(propertyType2.getSuperArea());
                    }
                });
                propertyTypeList.addAll(otherList);
            }
            ArrayList<PropertyType> finalList = new ArrayList<>();
            finalList.addAll(propertyTypeList);
            return finalList;
        }
        return propertyTypeList;
    }
}

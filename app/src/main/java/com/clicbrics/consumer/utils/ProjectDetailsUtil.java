package com.clicbrics.consumer.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.buy.housing.backend.propertyEndPoint.model.Bank;
import com.buy.housing.backend.propertyEndPoint.model.GetProjectDetailResponse;
import com.buy.housing.backend.propertyEndPoint.model.Project;
import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.EmiActivity;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Alok on 17-08-2018.
 */
public class ProjectDetailsUtil {

    static int bedIcon = -1;
    public static boolean checkCommercial(List<PropertyType> propertyTypeList){
        boolean isCommercial=false;
        if(propertyTypeList != null && !propertyTypeList.isEmpty()){
            isCommercial = true;
            for(int i=0; i<propertyTypeList.size(); i++){
                PropertyType propertyType = propertyTypeList.get(i);
                if(propertyType != null && !UtilityMethods.isCommercial(propertyType.getType())){
                    isCommercial = false;
                }
            }
        }
        return isCommercial;
    }

    public static boolean isOffer(Project project){
        long currentDate = System.currentTimeMillis();
        boolean isOffer = false;
        //Button offerImage = findViewById(R.id.offer_image);
        if(project != null && project.getStartOfferDate() != null
                && project.getEndOfferDate() != null){
            long offerStartdate = project.getStartOfferDate();
            long offerEndDate = project.getEndOfferDate();
            if (currentDate >= offerStartdate && currentDate <= offerEndDate
                    && (!TextUtils.isEmpty(project.getOffer())) ) {
                isOffer = true;
            } else {
                isOffer = false;
            }
        }else{
            if (project != null && !TextUtils.isEmpty(project.getOffer())) {
                isOffer = true;
            } else {
                isOffer = false;
            }
        }
        return isOffer;
    }

    public static String getBedNPropertyTypeStr(List<PropertyType> propertyTypeList) {
        String bedNPropertyType = "";
        if(propertyTypeList != null){
            int listSize = propertyTypeList.size();
            boolean hasApartment = false, hasVilla = false, hasFloor = false, hasPlot = false, hasCommercial = false, hasCommercialLand = false;
            Set<Integer> bedRoomSet = new TreeSet<>();
            String commercialTypeStr = "";

            for(int i=0; i<listSize ; i++){
                PropertyType propertyType = propertyTypeList.get(i);
                if(propertyType != null){
                    if(propertyType.getNumberOfBedrooms() != null && propertyType.getNumberOfBedrooms() != 0){
                        bedRoomSet.add(propertyType.getNumberOfBedrooms());
                    }

                    if(UtilityMethods.hasApartment(propertyType.getType())){
                        hasApartment = true;
                    }else if(UtilityMethods.hasVilla(propertyType.getType())){
                        hasVilla = true;
                    }else if(propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentFloor.toString())){
                        hasFloor = true;
                    }else if(propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.Land.toString())){
                        hasPlot = true;
                    }else if(UtilityMethods.isCommercial(propertyType.getType())){
                        commercialTypeStr = UtilityMethods.getCommercialTypeName(propertyType.getType());
                        if(UtilityMethods.isCommercialLand(propertyType.getType())){
                            hasCommercialLand = true;
                        }else{
                            hasCommercial = true;
                        }
                    }
                }
            }
            String propertyTypeStr ="";
            if(hasApartment){
                propertyTypeStr = Constants.AppConstants.PropertyType.Apartment.toString();
            }else if(hasVilla){
                propertyTypeStr = Constants.AppConstants.PropertyType.Villa.toString();
            }else if(hasFloor){
                propertyTypeStr = "Independent Floor";
            }else if(hasPlot){
                propertyTypeStr = "Plot";
            }else if(hasCommercial || hasCommercialLand){
                propertyTypeStr = commercialTypeStr;
            }

            if(bedRoomSet != null && !bedRoomSet.isEmpty()){
                String bedStr = "";
                int count = bedRoomSet.size();
                for(Integer bed : bedRoomSet){
                    count--;
                    if(count != 0){
                        bedStr = bedStr + bed + ", ";
                    }else{
                        bedStr = bedStr + bed + " BHK";
                    }
                }
                bedStr = bedStr + " " + propertyTypeStr;
                bedNPropertyType =  bedStr;
            }else{
                bedNPropertyType = propertyTypeStr;
            }

            if(hasApartment && (hasVilla || hasPlot || hasCommercial)){
                bedIcon = R.drawable.bed_generic_icon;
            }else if(hasPlot && !hasApartment && !hasVilla){
                bedIcon = R.drawable.plot_detail_icon;
            }else if(hasCommercial && !hasApartment && !hasVilla && !hasPlot){
                bedIcon = R.drawable.ic_shop_selected;
            }
        }

        return bedNPropertyType;
    }

    public static String getAreaRange(Context context,List<PropertyType> propertyTypeList,Project project) {
        String areaRangeNStatus = "";
        if(propertyTypeList != null){
            int listSize = propertyTypeList.size();
            boolean hasApartment = false, hasVilla = false, hasFloor = false, hasPlot = false, hasCommercial = false, hasCommercialLand = false;
            Set<Integer> areaSet = new TreeSet<>();

            /*String projectStatusStr = project.getProjectStatus().replace("NotStarted","New Launch")
                    .replace("InProgress", "Under Construction")
                    .replace("ReadyToMove", "Ready To Move");*/

            for(int i=0; i<listSize ; i++){
                PropertyType propertyType = propertyTypeList.get(i);
                if(propertyType != null){
                    if(propertyType.getSuperArea() != null){
                        areaSet.add(propertyType.getSuperArea());
                    }

                    if(UtilityMethods.hasApartment(propertyType.getType())){
                        hasApartment = true;
                    }else if(UtilityMethods.hasVilla(propertyType.getType())){
                        hasVilla = true;
                    }else if(propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentFloor.toString())){
                        hasFloor = true;
                    }else if(propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.Land.toString())){
                        hasPlot = true;
                    }else if(UtilityMethods.isCommercial(propertyType.getType())){
                        if(UtilityMethods.isCommercialLand(propertyType.getType())){
                            hasCommercialLand = true;
                        }else{
                            hasCommercial = true;
                        }
                    }
                }
            }
            boolean isLand = false;
            if(hasApartment){
                //do noting
            }else if(hasVilla){
                //do noting
            }else if(hasFloor){
                //do noting
            }else if(hasPlot){
                isLand = true;
            }else if(hasCommercial || hasCommercialLand){
                if(hasCommercialLand){
                    isLand = true;
                }
            }
            if(areaSet != null && !areaSet.isEmpty()){
                ArrayList<Integer> arrayList = new ArrayList<>(areaSet);
                String area = "";
                if(arrayList.size() == 1){
                    if(isLand) {
                        area = UtilityMethods.getArea(context, arrayList.get(0), true)
                                + " " + UtilityMethods.getUnit(context, true);
                    }else{
                        area = UtilityMethods.getArea(context, arrayList.get(0), false)
                                + " " + UtilityMethods.getUnit(context, false);
                    }
                }else {
                    int minArea = arrayList.get(0);
                    int maxArea = arrayList.get(arrayList.size()-1);
                    if (isLand) {
                        area = UtilityMethods.getArea(context, minArea, true)
                                + " - " + UtilityMethods.getArea(context,maxArea,true)
                                + " "+ UtilityMethods.getUnit(context, true);
                    }else {
                        area = UtilityMethods.getArea(context, minArea, false)
                                + " - " + UtilityMethods.getArea(context,maxArea,false)
                                + " "+ UtilityMethods.getUnit(context, false);
                    }
                }
                areaRangeNStatus = area;
            }
        }
        return areaRangeNStatus;
    }

    public static String getPriceNBSPStr(Context context,List<PropertyType> propertyTypeList){
        String priceNBSPStr = "";
        if(propertyTypeList != null){
            int listSize = propertyTypeList.size();
            boolean hasApartment = false, hasVilla = false, hasFloor = false, hasPlot = false, hasCommercial = false, hasCommercialLand = false;
            Set<Long> priceSet = new TreeSet<>();
            Set<Long> bspSet = new TreeSet<>();


            for(int i=0; i<listSize ; i++){
                PropertyType propertyType = propertyTypeList.get(i);
                if(propertyType != null){
                    if(propertyType.getSuperArea() != null && propertyType.getBsp() != null){
                        priceSet.add(propertyType.getBsp()* propertyType.getSuperArea());
                    }
                    if(propertyType.getBsp() != null){
                        bspSet.add(propertyType.getBsp());
                    }

                    if(UtilityMethods.hasApartment(propertyType.getType())){
                        hasApartment = true;
                    }else if(UtilityMethods.hasVilla(propertyType.getType())){
                        hasVilla = true;
                    }else if(propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentFloor.toString())){
                        hasFloor = true;
                    }else if(propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.Land.toString())){
                        hasPlot = true;
                    }else if(UtilityMethods.isCommercial(propertyType.getType())){
                        if(UtilityMethods.isCommercialLand(propertyType.getType())){
                            hasCommercialLand = true;
                        }else{
                            hasCommercial = true;
                        }
                    }
                }
            }
            boolean isLand = false;
            if(hasApartment){
                //do nothing
            }else if(hasVilla){
                //do nothing
            }else if(hasFloor){
                //do nothing
            }else if(hasPlot){
                isLand = true;
            }else if(hasCommercial || hasCommercialLand){
                if(hasCommercialLand){
                    isLand = true;
                }
            }

            String priceRangeStr = "";
            if(priceSet != null && !priceSet.isEmpty()){
                String price = "";
                ArrayList<Long> priceList = new ArrayList<>(priceSet);
                if(priceList.size() == 1){
                    if(priceList.get(0) != 0){
                        price = UtilityMethods.getPriceWord(priceList.get(0));
                    }
                }else{
                    price = "\u20B9" + " " + UtilityMethods.getPriceWordWithoutSymbol(priceList.get(0)) + " - "
                            + UtilityMethods.getPriceWordWithoutSymbol(priceList.get(priceList.size() - 1));
                }
                priceRangeStr = price;
            }
            String bspStr = "";
            if(bspSet != null && !bspSet.isEmpty()){
                ArrayList<Long> bspList = new ArrayList<>(bspSet);
                String bsp = "";
                if(bspList.size() == 1){
                    if(bspList.get(0) != 0) {
                        if (isLand) {
                            String bspValue = UtilityMethods.getBSP(context, bspList.get(0), true);
                            String unit = UtilityMethods.getUnit(context, true);
                            bsp = "BSP \u20B9" + bspValue + unit;
                        } else {
                            String bspValue = UtilityMethods.getBSP(context, bspList.get(0), false);
                            String unit = UtilityMethods.getUnit(context, false);
                            bsp = "BSP \u20B9" + bspValue + unit;
                        }
                    }
                }else{
                    if(isLand){
                        String bspMin = UtilityMethods.getBSP(context,bspList.get(0),true);
                        String bspMax = UtilityMethods.getBSP(context,bspList.get(bspList.size()-1),true);
                        String unit = UtilityMethods.getUnit(context,true);
                        bsp = "BSP \u20B9" + bspMin + " - " + bspMax + unit;
                    }else{
                        String bspMin = UtilityMethods.getBSP(context,bspList.get(0),true);
                        String bspMax = UtilityMethods.getBSP(context,bspList.get(bspList.size()-1),true);
                        String unit = UtilityMethods.getUnit(context,true);
                        bsp = "BSP \u20B9" + bspMin + " - " + bspMax + unit;
                    }
                }
                bspStr = bsp;
            }
            if(priceRangeStr.isEmpty() && bspStr.isEmpty()){
                priceNBSPStr = context.getString(R.string.price_on_request);
            }else {
                priceNBSPStr = priceRangeStr + "  |  " + bspStr;
            }
        }
        return priceNBSPStr;
    }

    public static String getEMIStr(final Context context, final List<PropertyType> propertyTypeList, Project project){
        String emiStr = "";
        float defaultInterestRate = UtilityMethods.getFloatInPref(context, Constants.AppConfigConstants.DEFAULT_INTEREST_RATE, 8.60f);
        Bank bank = null;
        if(project != null && project.getBankLoan() != null && !project.getBankLoan().isEmpty()){
            ArrayList<Float> interestList = new ArrayList<>();
            ArrayList<Bank> bankLoanList = new ArrayList<>();
            bankLoanList.addAll(project.getBankLoan());
            for (int i = 0; i < project.getBankLoan().size(); i++) {
                if (project.getBankLoan().get(i) != null && project.getBankLoan().get(0).getInterest()!= null) {
                    float interestRate = project.getBankLoan().get(0).getInterest();
                    interestList.add(interestRate);
                }
            }
            Collections.sort(interestList);
            Collections.sort(bankLoanList, new Comparator<Bank>() {
                @Override
                public int compare(Bank bank1, Bank bank2) {
                    if(bank1.getInterest() != null && bank2.getInterest() != null) {
                        return Float.compare(bank1.getInterest(), bank2.getInterest());
                    }else{
                        return 0;
                    }
                }
            });
            bank = bankLoanList.get(0);
            defaultInterestRate = interestList.get(0);
        }
        com.clicbrics.consumer.wrapper.Bank bankWrapper= null;
        if(bank != null){
            if(bank.getInterest() != null && bank.getInterest() <= defaultInterestRate){
                defaultInterestRate = bank.getInterest();
                bankWrapper = new com.clicbrics.consumer.wrapper.Bank();
                if(bank.getId() != null){
                    bankWrapper.setId(bank.getId());
                }
                if(bank.getOffersList() != null){
                    bankWrapper.setOffersList(bank.getOffersList());
                }
                if(bank.getInterest() != null){
                    bankWrapper.setInterest(bank.getInterest());
                }
                if(bank.getName() != null){
                    bankWrapper.setName(bank.getName());
                }
                if(bank.getLogo() != null){
                    bankWrapper.setLogo(bank.getLogo());
                }
                if(bank.getInterestType() != null){
                    bankWrapper.setInterestType(bank.getInterestType());
                }
                if(bank.getContactList() != null){
                    bankWrapper.setContactList(bank.getContactList());
                }
            }
        }
        final float finalInterestRate = defaultInterestRate;
        final ArrayList<Double> emiList = new ArrayList();
        if(propertyTypeList != null) {
            for (int i = 0; i < propertyTypeList.size(); i++) {
                if (((propertyTypeList.get(i).getSuperArea() != null) && (propertyTypeList.get(i).getSuperArea() != 0)) &&
                        ((propertyTypeList.get(i).getBsp() != null) && (propertyTypeList.get(i).getBsp() != 0))) {
                    double emiAmount = calculateEmi(propertyTypeList.get(i).getType(),
                            propertyTypeList.get(i).getSuperArea() * propertyTypeList.get(i).getBsp(), finalInterestRate);
                    if (emiAmount != 0) {
                        emiList.add(emiAmount);
                    }
                }
            }
        }

        if ((emiList != null) && (!emiList.isEmpty())) {
            Collections.sort(emiList);
            emiStr = "EMI starts from " + UtilityMethods.getPriceWordTillThousand(emiList.get(0)) + " per month";
        }
        LinearLayout emiLayout = ((ProjectDetailsScreen)context).findViewById(R.id.id_emi_layout);
        final com.clicbrics.consumer.wrapper.Bank finalBankWrapper = bankWrapper;
        emiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmiActivity(context,propertyTypeList,emiList, finalInterestRate, finalBankWrapper);
            }
        });
        return emiStr;
    }



    private static void showEmiActivity(Context context,List<PropertyType> propertyTypeList, ArrayList<Double> emiList,
                                 float finalInterestRate, com.clicbrics.consumer.wrapper.Bank bankWrapper){
        if(propertyTypeList != null && propertyTypeList.size() > 0){
            if(propertyTypeList.size() == 1){
                Intent intent = new Intent(context, EmiActivity.class);
                long housePrice = propertyTypeList.get(0).getBsp() * propertyTypeList.get(0).getSuperArea();
                intent.putExtra(Constants.IntentKeyConstants.IS_FROM_DETAIL_LINK, true);      //flag to evaluate from where we are calling this activity.
                if(bankWrapper != null){
                    Gson gson = new Gson();
                    String json = gson.toJson(bankWrapper);
                    intent.putExtra(Constants.IntentKeyConstants.IS_BANK_DETAILS_AVAIL, true);
                    intent.putExtra(Constants.IntentKeyConstants.BANK_DETAILS, json);
                }else{
                    intent.putExtra(Constants.IntentKeyConstants.IS_BANK_DETAILS_AVAIL, false);
                }
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_VALUE, housePrice);
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_ID, propertyTypeList.get(0).getId());
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, propertyTypeList.get(0).getProjectId());
                intent.putExtra(Constants.IntentKeyConstants.TYPE, propertyTypeList.get(0).getType());
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_SIZE, propertyTypeList.get(0).getSuperArea());
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_TYPE, propertyTypeList.get(0).getNumberOfBedrooms().intValue());
                context.startActivity(intent);
            }else{
                for(int i=0; i< propertyTypeList.size(); i++){
                    double emiAmount = calculateEmi(propertyTypeList.get(i).getType(),
                            propertyTypeList.get(i).getSuperArea() * propertyTypeList.get(i).getBsp(), finalInterestRate);
                    if(emiAmount == emiList.get(0)){
                        Intent intent = new Intent(context, EmiActivity.class);
                        long housePrice = propertyTypeList.get(i).getBsp() * propertyTypeList.get(i).getSuperArea();
                        intent.putExtra(Constants.IntentKeyConstants.IS_FROM_DETAIL_LINK, true);      //flag to evaluate from where we are calling this activity.
                        if(bankWrapper != null){
                            Gson gson = new Gson();
                            String json = gson.toJson(bankWrapper);
                            intent.putExtra(Constants.IntentKeyConstants.IS_BANK_DETAILS_AVAIL, true);
                            intent.putExtra(Constants.IntentKeyConstants.BANK_DETAILS, json);
                        }else{
                            intent.putExtra(Constants.IntentKeyConstants.IS_BANK_DETAILS_AVAIL, false);
                        }
                        intent.putExtra(Constants.IntentKeyConstants.PROPERTY_VALUE, housePrice);
                        intent.putExtra(Constants.IntentKeyConstants.PROPERTY_ID, propertyTypeList.get(i).getId());
                        intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, propertyTypeList.get(i).getProjectId());
                        intent.putExtra(Constants.IntentKeyConstants.TYPE, propertyTypeList.get(i).getType());
                        intent.putExtra(Constants.IntentKeyConstants.PROPERTY_SIZE, propertyTypeList.get(i).getSuperArea());
                        intent.putExtra(Constants.IntentKeyConstants.PROPERTY_TYPE, propertyTypeList.get(i).getNumberOfBedrooms().intValue());
                        context.startActivity(intent);
                        break;
                    }else{
                        continue;
                    }
                }
            }
        }
    }

    private static double calculateEmi(String type, long totalAmount, float interestRate) {
        double downPayment;
        if (type.equalsIgnoreCase(Constants.AppConstants.PropertyType.Land.toString())) {
            downPayment = (totalAmount) * (0.40);
        } else {
            downPayment = (totalAmount) * (0.20);
        }
        double principleAmount = totalAmount - downPayment;
        double rate = interestRate / (12 * 100); /*one month interest*/
        double time = 20 * 12; /*one month period*/
        return (principleAmount * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);
    }

    public static int getBedIcon(){
        if(bedIcon == -1){
            return R.drawable.bed_icon;
        }
        return bedIcon;
    }
}

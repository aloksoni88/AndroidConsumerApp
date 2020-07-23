package com.clicbrics.consumer.wrapper;

import com.clicbrics.consumer.utils.Item;

/**
 * Created by Paras on 06-11-2016.
 */
public class PaymentPlanHeader implements Item{

    private final String title;
    private final String subTitle;

    public PaymentPlanHeader(String title,String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    public String getSubTitle(){
        return subTitle;
    }

    public String getTitle(){
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}

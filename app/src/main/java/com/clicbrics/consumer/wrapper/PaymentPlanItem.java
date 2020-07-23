package com.clicbrics.consumer.wrapper;

import com.clicbrics.consumer.utils.Item;

/**
 * Created by Paras on 06-11-2016.
 */
public class PaymentPlanItem implements Item {

    public final String title;
    public final String subtitle;

    public PaymentPlanItem(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    public boolean isSection() {
        return false;
    }

}
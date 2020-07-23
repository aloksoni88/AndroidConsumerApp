package com.clicbrics.consumer.wrapper;

import com.clicbrics.consumer.utils.Item;

/**
 * Created by Paras on 21-10-2016.
 */
public class ListSectionItem implements Item {

    private final String title;

    public ListSectionItem(String title) {
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }

}
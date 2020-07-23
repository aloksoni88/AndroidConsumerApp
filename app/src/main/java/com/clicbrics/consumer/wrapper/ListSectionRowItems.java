package com.clicbrics.consumer.wrapper;

import com.clicbrics.consumer.utils.Item;

/**
 * Created by Paras on 21-10-2016.
 */
public class ListSectionRowItems  implements Item {

    public final String key;
    public final String value;

    public ListSectionRowItems(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean isSection() {
        return false;
    }

}

package com.taotao.common.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ItemCatResult {
    //封装所有的类目信息
    @JsonProperty("data")
    private List<ItemCatData> itemCats = new ArrayList<ItemCatData>();

    public List<ItemCatData> getItemCats() {
        return itemCats;
    }

    public void setItemCats(List<ItemCatData> itemCats) {
        this.itemCats = itemCats;
    }
}

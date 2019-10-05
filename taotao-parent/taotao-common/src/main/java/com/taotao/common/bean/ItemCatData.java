package com.taotao.common.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ItemCatData {
    //序列化成json数据时为u,即对应json数据结构中名字为u的数据
    @JsonProperty("u")
    private String url;
    @JsonProperty("n")
    private String name;
    @JsonProperty("i")
    //因为items中存在二级和三级目录，其数据格式不统一，故使用？
    //二级类目中的i为对象，三级类目中的i为字符串类型
    private List<?> items;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<?> getItems() {
        return items;
    }

    public void setItems(List<?> items) {
        this.items = items;
    }
}

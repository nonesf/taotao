package com.taotao.search.pojo;

import java.util.List;

public class SearchResult {

    private Long total;
    private List<?> list;

    public SearchResult(Long total, List<?> list) {
        this.total = total;
        this.list = list;
    }

    public SearchResult() {

    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

}

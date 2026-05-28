package com.diary.common;

import java.util.List;

public class PageResult<T> {
    private long total;
    private long pages;
    private List<T> records;

    public PageResult() {}

    public PageResult(long total, long pages, List<T> records) {
        this.total = total;
        this.pages = pages;
        this.records = records;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public long getPages() { return pages; }
    public void setPages(long pages) { this.pages = pages; }
    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
}
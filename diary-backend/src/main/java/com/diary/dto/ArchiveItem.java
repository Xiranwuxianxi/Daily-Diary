package com.diary.dto;

public class ArchiveItem {
    private String yearMonth;
    private long count;

    public ArchiveItem() {}

    public ArchiveItem(String yearMonth, long count) {
        this.yearMonth = yearMonth;
        this.count = count;
    }

    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
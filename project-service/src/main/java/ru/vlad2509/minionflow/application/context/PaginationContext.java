package ru.vlad2509.minionflow.application.context;

public class PaginationContext {

    private final int pageSize;
    private final int pageIndex;

    private int total = 0;
    private int pageCount = 0;

    // TODO: фильтрация и сортировка по полям

    public PaginationContext(int pageSize, int pageIndex) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        if (pageSize < 0)
            throw new IllegalArgumentException("Page size must be greater than zero");
        if (pageIndex < 0)
            throw new IllegalArgumentException("Page index must be greater than zero");
    }

    public void acceptResult(int total, int pageCount) {
        this.total = total;
        this.pageCount = pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getTotal() {
        return total;
    }

    public int getPageCount() {
        return pageCount;
    }
}

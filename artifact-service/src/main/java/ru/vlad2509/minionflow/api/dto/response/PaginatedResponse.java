package ru.vlad2509.minionflow.api.dto.response;

import ru.vlad2509.minionflow.application.context.PaginationContext;

import java.util.List;

public record PaginatedResponse<T extends Record>(
        int total,
        int pageCount,
        int pageSize,
        int pageIndex,
        List<T> records
) {

    public static <T extends Record> PaginatedResponse<T> of(PaginationContext context, List<T> records) {
        return new PaginatedResponse<T>(context.getTotal(), context.getPageCount(), context.getPageSize(), context.getPageIndex(), records);
    }

}

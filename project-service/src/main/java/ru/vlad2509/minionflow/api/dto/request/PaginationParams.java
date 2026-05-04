package ru.vlad2509.minionflow.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import ru.vlad2509.minionflow.application.context.PaginationContext;

public record PaginationParams(

        @QueryParam("size")
        @Min(1)
        @Max(1000)
        @DefaultValue("20")
        int pageSize,

        @QueryParam("page")
        @Min(0)
        @Max(1000000)
        @DefaultValue("0")
        int pageIndex

) {

    public PaginationContext toContext(){
        return new PaginationContext(pageSize, pageIndex);
    }

}

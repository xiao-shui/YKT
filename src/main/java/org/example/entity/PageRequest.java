package org.example.entity;

import lombok.Data;
import jakarta.validation.constraints.Min;

@Data
public class PageRequest {
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页数量必须大于0")
    private Integer pageSize = 10;

    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }

    public Integer getLimit() {
        return pageSize;
    }
}


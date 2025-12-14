package org.example.entity;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class Inventory {
    private Integer inventoryId;

    @NotNull(message = "仓库ID不能为空")
    private Integer warehouseId;

    @NotNull(message = "SKU ID不能为空")
    private Integer skuId;

    @Min(value = 0, message = "数量不能小于0")
    private Integer quantity = 0;

    @Min(value = 0, message = "已分配数量不能小于0")
    private Integer allocatedQty = 0;

    private Integer availableQty;

    private LocalDateTime lastCheckTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}


package org.example.entity;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class Warehouse {
    private Integer warehouseId;

    @NotBlank(message = "仓库名称不能为空")
    @Size(max = 50, message = "仓库名称长度不能超过50")
    private String warehouseName;

    @Size(max = 200, message = "位置长度不能超过200")
    private String location;

    @Size(max = 50, message = "管理员长度不能超过50")
    private String manager;

    @Min(value = 0, message = "容量不能小于0")
    private Integer capacity;

    private Integer usedCapacity = 0;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status = 1;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}


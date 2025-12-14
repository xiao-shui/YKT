package org.example.entity;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Integer productId;

    @NotBlank(message = "产品名称不能为空")
    @Size(max = 100, message = "产品名称长度不能超过100")
    private String productName;

    @Size(max = 50, message = "分类长度不能超过50")
    private String category;

    @Size(max = 50, message = "品牌长度不能超过50")
    private String brand;

    private String description;

    @NotNull(message = "基础价格不能为空")
    @DecimalMin(value = "0.01", message = "基础价格必须大于0")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确")
    private BigDecimal basePrice;

    @Pattern(regexp = "上架|下架", message = "状态只能是'上架'或'下架'")
    private String status = "上架";

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}


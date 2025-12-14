package org.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class InventoryVO extends Inventory {
    private String warehouseName;
    private String productName;
    private String color;
    private String size;
}


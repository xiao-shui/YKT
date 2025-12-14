package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Inventory;
import org.example.entity.InventoryVO;
import java.util.List;

@Mapper
public interface InventoryMapper {
    List<InventoryVO> selectAll(@Param("warehouseId") Integer warehouseId,
                               @Param("productName") String productName,
                               @Param("limit") Integer limit,
                               @Param("offset") Integer offset);

    Long count(@Param("warehouseId") Integer warehouseId,
              @Param("productName") String productName);

    Inventory selectById(Integer inventoryId);

    int insert(Inventory inventory);

    int update(Inventory inventory);

    int deleteById(Integer inventoryId);
}


package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Warehouse;
import java.util.List;

@Mapper
public interface WarehouseMapper {
    List<Warehouse> selectAll(@Param("warehouseName") String warehouseName,
                             @Param("status") Integer status,
                             @Param("limit") Integer limit,
                             @Param("offset") Integer offset);

    Long count(@Param("warehouseName") String warehouseName,
              @Param("status") Integer status);

    Warehouse selectById(Integer warehouseId);

    int insert(Warehouse warehouse);

    int update(Warehouse warehouse);

    int deleteById(Integer warehouseId);
}


package org.example.service;

import org.example.entity.PageRequest;
import org.example.entity.PageResult;
import org.example.entity.Warehouse;
import org.example.mapper.WarehouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;

    public PageResult<Warehouse> getList(PageRequest pageRequest, String warehouseName, Integer status) {
        List<Warehouse> list = warehouseMapper.selectAll(warehouseName, status,
                pageRequest.getLimit(), pageRequest.getOffset());
        Long total = warehouseMapper.count(warehouseName, status);
        return new PageResult<>(list, total, pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    public Warehouse getById(Integer warehouseId) {
        return warehouseMapper.selectById(warehouseId);
    }

    @Transactional
    public int save(Warehouse warehouse) {
        if (warehouse.getWarehouseId() == null) {
            return warehouseMapper.insert(warehouse);
        } else {
            return warehouseMapper.update(warehouse);
        }
    }

    @Transactional
    public int delete(Integer warehouseId) {
        return warehouseMapper.deleteById(warehouseId);
    }
}


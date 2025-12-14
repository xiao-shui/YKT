package org.example.service;

import org.example.entity.Inventory;
import org.example.entity.InventoryVO;
import org.example.entity.PageRequest;
import org.example.entity.PageResult;
import org.example.mapper.InventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    public PageResult<InventoryVO> getList(PageRequest pageRequest, Integer warehouseId, String productName) {
        List<InventoryVO> list = inventoryMapper.selectAll(warehouseId, productName,
                pageRequest.getLimit(), pageRequest.getOffset());
        Long total = inventoryMapper.count(warehouseId, productName);
        return new PageResult<>(list, total, pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    public Inventory getById(Integer inventoryId) {
        return inventoryMapper.selectById(inventoryId);
    }

    @Transactional
    public int save(Inventory inventory) {
        if (inventory.getInventoryId() == null) {
            return inventoryMapper.insert(inventory);
        } else {
            return inventoryMapper.update(inventory);
        }
    }

    @Transactional
    public int delete(Integer inventoryId) {
        return inventoryMapper.deleteById(inventoryId);
    }
}


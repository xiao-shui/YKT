package org.example.controller;

import org.example.common.Result;
import org.example.entity.Inventory;
import org.example.entity.InventoryVO;
import org.example.entity.PageRequest;
import org.example.entity.PageResult;
import org.example.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public Result<PageResult<InventoryVO>> getList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "warehouseId", required = false) Integer warehouseId,
            @RequestParam(value = "productName", required = false) String productName) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        PageResult<InventoryVO> result = inventoryService.getList(pageRequest, warehouseId, productName);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Inventory> getById(@PathVariable("id") Integer id) {
        Inventory inventory = inventoryService.getById(id);
        return Result.success(inventory);
    }

    @PostMapping
    public Result<String> save(@Validated @RequestBody Inventory inventory) {
        inventoryService.save(inventory);
        return Result.success("保存成功", null);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable("id") Integer id, @Validated @RequestBody Inventory inventory) {
        inventory.setInventoryId(id);
        inventoryService.save(inventory);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable("id") Integer id) {
        inventoryService.delete(id);
        return Result.success("删除成功", null);
    }
}


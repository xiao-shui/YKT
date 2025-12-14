package org.example.controller;

import org.example.common.Result;
import org.example.entity.PageRequest;
import org.example.entity.PageResult;
import org.example.entity.Warehouse;
import org.example.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping
    public Result<PageResult<Warehouse>> getList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "warehouseName", required = false) String warehouseName,
            @RequestParam(value = "status", required = false) Integer status) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        PageResult<Warehouse> result = warehouseService.getList(pageRequest, warehouseName, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Warehouse> getById(@PathVariable("id") Integer id) {
        Warehouse warehouse = warehouseService.getById(id);
        return Result.success(warehouse);
    }

    @PostMapping
    public Result<String> save(@Validated @RequestBody Warehouse warehouse) {
        warehouseService.save(warehouse);
        return Result.success("保存成功", null);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable("id") Integer id, @Validated @RequestBody Warehouse warehouse) {
        warehouse.setWarehouseId(id);
        warehouseService.save(warehouse);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable("id") Integer id) {
        warehouseService.delete(id);
        return Result.success("删除成功", null);
    }
}


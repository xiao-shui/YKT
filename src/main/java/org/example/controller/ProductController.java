package org.example.controller;

import org.example.common.Result;
import org.example.entity.PageRequest;
import org.example.entity.PageResult;
import org.example.entity.Product;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<PageResult<Product>> getList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "status", required = false) String status) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        PageResult<Product> result = productService.getList(pageRequest, productName, category, brand, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable("id") Integer id) {
        Product product = productService.getById(id);
        return Result.success(product);
    }

    @PostMapping
    public Result<String> save(@Validated @RequestBody Product product) {
        productService.save(product);
        return Result.success("保存成功", null);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable("id") Integer id, @Validated @RequestBody Product product) {
        product.setProductId(id);
        productService.save(product);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable("id") Integer id) {
        productService.delete(id);
        return Result.success("删除成功", null);
    }
}


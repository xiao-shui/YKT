package org.example.service;

import org.example.entity.PageRequest;
import org.example.entity.PageResult;
import org.example.entity.Product;
import org.example.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    public PageResult<Product> getList(PageRequest pageRequest, String productName, String category, String brand, String status) {
        List<Product> list = productMapper.selectAll(productName, category, brand, status,
                pageRequest.getLimit(), pageRequest.getOffset());
        Long total = productMapper.count(productName, category, brand, status);
        return new PageResult<>(list, total, pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    public Product getById(Integer productId) {
        return productMapper.selectById(productId);
    }

    @Transactional
    public int save(Product product) {
        if (product.getProductId() == null) {
            return productMapper.insert(product);
        } else {
            return productMapper.update(product);
        }
    }

    @Transactional
    public int delete(Integer productId) {
        return productMapper.deleteById(productId);
    }
}


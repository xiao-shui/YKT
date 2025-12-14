package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Product;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> selectAll(@Param("productName") String productName,
                           @Param("category") String category,
                           @Param("brand") String brand,
                           @Param("status") String status,
                           @Param("limit") Integer limit,
                           @Param("offset") Integer offset);

    Long count(@Param("productName") String productName,
              @Param("category") String category,
              @Param("brand") String brand,
              @Param("status") String status);

    Product selectById(Integer productId);

    int insert(Product product);

    int update(Product product);

    int deleteById(Integer productId);
}


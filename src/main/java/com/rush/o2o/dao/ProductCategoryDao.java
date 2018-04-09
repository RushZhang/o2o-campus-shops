package com.rush.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.rush.o2o.entity.ProductCategory;

public interface ProductCategoryDao {
	List<ProductCategory> queryProductCategoryList(long shopId);
	
	//批量新增商品类别, 返回影响的数量
	int batchInsertProductCategory(List<ProductCategory> productCategoryList);
	
	int deleteProductCategory(@Param("productCategoryId") long productCategoryId, @Param("shopId") long shopId);
}

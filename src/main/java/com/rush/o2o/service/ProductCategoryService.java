package com.rush.o2o.service;

import java.util.List;

import com.rush.o2o.dto.ProductCategoryExecution;
import com.rush.o2o.entity.ProductCategory;
import com.rush.o2o.exceptions.ProductCategoryOperationException;

public interface ProductCategoryService {
	List<ProductCategory> getProductCategoryList(Long shopId);
	//增删改的方法最好通过ProductCategoryExecution来返回
	
	ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList) throws ProductCategoryOperationException;
	
	ProductCategoryExecution deleteProductCategory(long productCategoryId, long shopId) throws ProductCategoryOperationException;	
}

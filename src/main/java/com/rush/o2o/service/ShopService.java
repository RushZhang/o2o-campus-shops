package com.rush.o2o.service;

import java.io.InputStream;

import com.rush.o2o.dto.ShopExecution;
import com.rush.o2o.entity.Shop;
import com.rush.o2o.exceptions.ShopOperationException;

public interface ShopService {
	
	
	//根据shopCondition分页返回相应的店铺列表
	ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize);
	
	Shop getByShopId(long shopId);

	ShopExecution modifyShop(Shop shop, InputStream shopImgInputStream, String fileName) throws ShopOperationException;
	
	ShopExecution addShop(Shop shop, InputStream shopImgInputStream, String fileName) throws ShopOperationException;
}

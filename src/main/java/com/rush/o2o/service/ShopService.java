package com.rush.o2o.service;

import java.io.InputStream;

import com.rush.o2o.dto.ShopExecution;
import com.rush.o2o.entity.Shop;

public interface ShopService {
	ShopExecution addShop(Shop shop, InputStream shopImgInputStream, String fileName);
}

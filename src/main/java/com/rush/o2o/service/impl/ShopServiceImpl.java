package com.rush.o2o.service.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rush.o2o.dao.ShopDao;
import com.rush.o2o.dto.ShopExecution;
import com.rush.o2o.entity.Shop;
import com.rush.o2o.enums.ShopStateEnum;
import com.rush.o2o.exceptions.ShopOperationException;
import com.rush.o2o.service.ShopService;
import com.rush.o2o.util.ImageUtil;
import com.rush.o2o.util.PageCalculator;
import com.rush.o2o.util.PathUtil;


@Service
public class ShopServiceImpl  implements ShopService{
	
	@Autowired
	private ShopDao shopDao;

	@Override
	@Transactional  //加了这个注解说明是事物
	public ShopExecution addShop(Shop shop, InputStream shopImgInputStream, String fileName) {
		if (shop == null) {  //空值判断
			return new ShopExecution(ShopStateEnum.NULL_SHOP);
		}
		
		try {  //给店铺信息赋值
			shop.setEnableStatus(0);
			shop.setCreateTime(new Date());
			shop.setLastEditTime(new Date());
			int effectedNum = shopDao.insertShop(shop);
			if(effectedNum <= 0) {
				throw new ShopOperationException("店铺创建失败");  
				//这里之所以要用ShopOperationException，是因为用了事务，普通的exception不一定会回滚
			} else {
				if (shopImgInputStream != null) {
					//存储图片
					try {
						addShopImg(shop, shopImgInputStream, fileName);  //在这个add过程中已经更新了shopImg
					} catch (Exception e) {
						throw new ShopOperationException("addShopImg error:" + e.getMessage());
					}
					//更新店铺的图片地址
					effectedNum = shopDao.updateShop(shop);
					if (effectedNum <= 0) {
						throw new ShopOperationException("更新图片地址失败");
					}
				}
			}
		}	catch (Exception e) {
			throw new ShopOperationException("AddShop error: "+ e.getMessage());
		}
		return new ShopExecution(ShopStateEnum.CHECK, shop);
	}

	private void addShopImg(Shop shop, InputStream shopImgInputStream, String fileName) {
		//获取shop图片目录相对值路径
		String dest = PathUtil.getShopImagePath(shop.getShopId());
		String shopImgAddr = ImageUtil.generateThumbnail(shopImgInputStream, fileName, dest);
		shop.setShopImg(shopImgAddr);
	}

	@Override
	public Shop getByShopId(long shopId) {
		// TODO Auto-generated method stub
		return shopDao.queryByShopId(shopId);
	}

	@Override
	public ShopExecution modifyShop(Shop shop, InputStream shopImgInputStream, String fileName)
			throws ShopOperationException {
		// TODO Auto-generated method stub
		if(shop == null || shop.getShopId() == null) {
			return new ShopExecution(ShopStateEnum.NULL_SHOP);
		} 
		//1.判断要不要处理图片
		try {
			if (shopImgInputStream != null) {
				//tempShop相当于是旧的shop数据
				Shop tempShop = shopDao.queryByShopId(shop.getShopId());
				if (tempShop.getShopImg() != null) {
					ImageUtil.deleteFileOrPath(tempShop.getShopImg());
				}
				addShopImg(shop, shopImgInputStream, fileName);
			}
			//2.更新店铺信息
			shop.setLastEditTime(new Date());
			int effectedNum = shopDao.updateShop(shop);
			if (effectedNum <= 0) {
				return new ShopExecution(ShopStateEnum.INNER_ERROR);
			} else {
				shop = shopDao.queryByShopId(shop.getShopId());
				return new ShopExecution(ShopStateEnum.SUCCESS, shop);
			}
		} catch (Exception e) {
			throw new ShopOperationException("Modify Shop Err.. "+ e.getMessage());
		}
	}

	
	@Override
	public ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize) {
		//需要先将pageIndex转化为rowIndex，因为前端只认页数，后端只认行数
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<Shop> shopList = shopDao.queryShopList(shopCondition, rowIndex, pageSize);
		int count = shopDao.queryShopCount(shopCondition);
		ShopExecution	 se = new ShopExecution();
		if(shopList != null) {
			se.setShopList(shopList);
			se.setCount(count);
		} else {
			se.setState(ShopStateEnum.INNER_ERROR.getState());
		}
		return se;
	}

}

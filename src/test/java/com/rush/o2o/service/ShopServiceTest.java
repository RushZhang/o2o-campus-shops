//package com.rush.o2o.service;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.util.Date;
//
//import org.junit.Ignore;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.rush.o2o.BaseTest;
//import com.rush.o2o.dto.ShopExecution;
//import com.rush.o2o.entity.Area;
//import com.rush.o2o.entity.PersonInfo;
//import com.rush.o2o.entity.Shop;
//import com.rush.o2o.entity.ShopCategory;
//import com.rush.o2o.enums.ShopStateEnum;
//import com.rush.o2o.exceptions.ShopOperationException;
//
//public class ShopServiceTest  extends BaseTest{
//	@Autowired
//	private ShopService shopService;
//	
//	@Test
//	public void testGetShopList() {
//		Shop shopCondition = new Shop();
//		ShopCategory sc = new ShopCategory();
//		sc.setShopCategoryId(2L);
//		shopCondition.setShopCategory(sc);
//		ShopExecution se = shopService.getShopList(shopCondition, 1, 5);
//		System.out.println("店铺列表数为：" + se.getShopList().size());
//		System.out.println("店铺总数为：" + se.getCount());
//	}
//	
//	
//	@Test
//	@Ignore
//	public void testModifyShop() throws ShopOperationException, FileNotFoundException {
//		Shop shop = new Shop();
//		shop.setShopId(1L);
//		shop.setShopName("修改后的店铺名");
//		File shopImg = new File("/Users/zhangweicheng/Desktop/Other/圣诞.jpg");
//		InputStream is = new FileInputStream(shopImg);
//		ShopExecution shopExecution = shopService.modifyShop(shop, is, shopImg.getName());
//		System.out.println("新图的地址："+shopExecution.getShop().getShopImg());
//	}
//	
//	@Ignore
//	@Test
//	public void testAddShop() throws FileNotFoundException {
//		Shop shop = new Shop();
//		PersonInfo owner = new PersonInfo();
//		Area area = new Area();
//		ShopCategory shopCategory = new ShopCategory();
//		owner.setUserId(1L);
//		area.setAreaId(1);
//		shopCategory.setShopCategoryId(1L);
//		shop.setOwner(owner);
//		shop.setArea(area);
//		shop.setShopCategory(shopCategory);
//		shop.setShopName("奇怪测试");
//		shop.setShopDesc("奇怪测试");
//		shop.setShopAddr("奇怪测试");
//		shop.setPhone("奇怪测试");
//		shop.setCreateTime(new Date());
//		shop.setEnableStatus(ShopStateEnum.CHECK.getState());
//		shop.setAdvice("奇怪测试中。。");
//		File shopImg = new File("/Users/zhangweicheng/Desktop/Other/圣诞.jpg");
//		InputStream is = new FileInputStream(shopImg);
//		ShopExecution se = shopService.addShop(shop, is, shopImg.getName());
//		assertEquals(ShopStateEnum.CHECK.getState(), se.getState());
//	}
//
//}

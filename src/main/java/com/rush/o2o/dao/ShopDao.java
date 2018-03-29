package com.rush.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.rush.o2o.entity.Shop;

public interface ShopDao {
	
	
	//@Param得作用。在多个参数得时候必用，单个参数得时候可以不用
	
	
	/**
	 * 分页查询店铺，可输入的条件有：店铺名(模糊),店铺状态，店铺类别，区域Id,owner
	 * 
	 * @param shopCondition
	 * @param rowIndex
	 *            从第几行开始取数据
	 * @param pageSize
	 *            返回的条数
	 * @return
	 */
	List<Shop> queryShopList(@Param("shopCondition") Shop shopCondition, @Param("rowIndex") int rowIndex,
			@Param("pageSize") int pageSize);
	/**
	 * 通过shop id 查询店铺
	 * 
	 * @param shopId
	 * @return shop
	 */
	Shop queryByShopId(long shopId);

	/**
	 * 新增店铺
	 * 
	 * @param shop
	 * @return
	 */
	int insertShop(Shop shop);
	
	
	/**
	 * 更新店铺信息
	 * 
	 * @param shop
	 * @return
	 */
	int updateShop(Shop shop);
	
	
	
	
	int queryShopCount(@Param("shopCondition") Shop shopCondition);
}

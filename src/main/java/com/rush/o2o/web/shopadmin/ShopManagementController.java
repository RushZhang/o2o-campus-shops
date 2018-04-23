package com.rush.o2o.web.shopadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rush.o2o.dto.ImageHolder;
import com.rush.o2o.dto.ShopExecution;
import com.rush.o2o.entity.Area;
import com.rush.o2o.entity.PersonInfo;
import com.rush.o2o.entity.Shop;
import com.rush.o2o.entity.ShopCategory;
import com.rush.o2o.enums.ShopStateEnum;
import com.rush.o2o.exceptions.ShopOperationException;
import com.rush.o2o.service.AreaService;
import com.rush.o2o.service.ShopCategoryService;
import com.rush.o2o.service.ShopService;
import com.rush.o2o.util.CodeUtil;
import com.rush.o2o.util.HttpServletRequestUtil;


@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
	@Autowired
	private ShopService shopService;
	@Autowired
	private ShopCategoryService shopCategoryService;
	@Autowired
	private AreaService areaService;
	

	@RequestMapping(value = "/getshopmanagementinfo", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopManagementInfo(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		if(shopId <= 0) {
			Object currentShopObj = request.getSession().getAttribute("currentShop");
			if (currentShopObj == null) {   //非法登陆的就要强行跳转,让重新选个店铺去做管理
				modelMap.put("redirect", true);
				modelMap.put("url", "/o2o/shopadmin/shoplist");
			} else {    //session里还存在信息，就不用从定向
				Shop currentShop = (Shop)currentShopObj;
				modelMap.put("redirect", false);
				modelMap.put("shopId", currentShop. getShopId());
			}
		} else {  //传入shopId，那就可以操作，不过最终还是拦截器说了算
			Shop currentShop = new Shop();
			currentShop.setShopId(shopId);
			request.getSession().setAttribute("currentShop", currentShop);
			modelMap.put("redirect", false);
		}
		return modelMap;
	}
	
	
//getshoplist*****************************************************************************************************************************************
	@RequestMapping(value = "/getshoplist", method = RequestMethod.GET)
	@ResponseBody
	private Map <String, Object> getShopList(HttpServletRequest request){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		//先这样设置
//		PersonInfo user = new PersonInfo();
//		user.setUserId(1L);
//		user.setName("Dora");
//		request.getSession().setAttribute("user", user);
//		user = (PersonInfo) request.getSession().getAttribute("user");
		try {
			Shop shopCondition = new Shop();
			shopCondition.setOwner(user);
			ShopExecution se = shopService.getShopList(shopCondition, 0, 100);//一个人创建一百个店铺已经极限了
			modelMap.put("shopList", se.getShopList());
			// 列出店铺成功之后，将店铺放入session中作为权限验证依据，即该帐号只能操作它自己的店铺
			request.getSession().setAttribute("shopList", se.getShopList());
			modelMap.put("user", user);
			modelMap.put("success", true);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;
	}
	
	
	
//getshopbyid*****************************************************************************************************************************************			
	@RequestMapping(value = "/getshopbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopById(HttpServletRequest request	) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		if (shopId > -1 ) {
			try {
				Shop shop = shopService.getByShopId(shopId);
				List<Area> areaList = areaService.getAreaList();
				modelMap.put("shop", shop);
				modelMap.put("areaList", areaList);  //更改的时候categoryList就不允许再变更咯
				modelMap.put("success", true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty shopId");
		}
		return modelMap;
	}
	
	
	
	
//getshopinitinfo*****************************************************************************************************************************************		
	@RequestMapping(value = "/getshopinitinfo", method=RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopInitInfo(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
		List<Area> areaList = new ArrayList<Area>();
		try {
			shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory());
			areaList = areaService.getAreaList();
			modelMap.put("shopCategoryList", shopCategoryList);
			modelMap.put("areaList", areaList);
			modelMap.put("success", true);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;
	}
	
	

//registershop****************************************************************************************************************************************
	@RequestMapping(value = "/registershop", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> registerShop(HttpServletRequest request) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//0. 检查验证码
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码!");
			return modelMap;
		}
		//1. 接受并转化相应的参数，包括店铺信息和图片信息*************************************************
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr"); //这里的shopStr要和前端约定好
		ObjectMapper mapper = new ObjectMapper();
		Shop shop = null;
		try {  
			shop = mapper.readValue(shopStr, Shop.class);  //将前端发过来相关的参数转化为相应的实体类
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		CommonsMultipartFile	shopImg = null;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		if (commonsMultipartResolver.isMultipart(request)) {  //判断是不是上传文件流，是的话，就转化
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "上传图片不能是空的");
			return modelMap;
		}
		//2.注册店铺*************************************************
		if (shop != null && shopImg != null) {
//			PersonInfo owner = new PersonInfo();
//			//这里先暂时硬编码，之后会完善，通过session
//			owner.setUserId(1L);
//			shop.setOwner(owner);
		
			PersonInfo owner = (PersonInfo) request.getSession().getAttribute("user"); //登陆时候添加过的session
			shop.setOwner(owner);
		
			
			ShopExecution se;
			try {
				ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(), shopImg.getInputStream());
				se = shopService.addShop(shop, imageHolder);
				if (se.getState() == ShopStateEnum.CHECK.getState()) {
					modelMap.put("success", true);
					//该用户可以操作的店铺列表
					@SuppressWarnings("unchecked")
					List<Shop> shopList = (List<Shop>)request.getSession().getAttribute("shopList");
					if (shopList == null || shopList.size() == 0	 ) {    //如果是第一次操作店铺
						shopList = new ArrayList<Shop>();
					}
					shopList.add(se.getShop());
					request.getSession().setAttribute("shopList", shopList);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			}
			
			//返回结果
			return modelMap;
		}	else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入店铺信息");
			return modelMap;
		}
	}
	
	
	
//modifyshop****************************************************************************************************************************************	
	@RequestMapping(value = "/modifyshop", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//0.验证码
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 1.接收并转化相应的参数，包括店铺信息以及图片信息
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
		ObjectMapper mapper = new ObjectMapper();
		Shop shop = null;
		try {
			shop = mapper.readValue(shopStr, Shop.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
		CommonsMultipartFile shopImg = null;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (commonsMultipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
		}
		// 2.修改店铺信息
		if (shop != null && shop.getShopId() != null) {
			
			ShopExecution se;
			try {
				if (shopImg == null) {
					se = shopService.modifyShop(shop, null);
				} else {
					ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(), shopImg.getInputStream());
					se = shopService.modifyShop(shop, imageHolder);
				}
				if (se.getState() == ShopStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (ShopOperationException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			}
			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入店铺Id");
			return modelMap;
		}
	}
	
	
	

	//	private static void inputStreamToFile(InputStream ins, File file) {
	//		FileOutputStream os = null;
	//		try { 
	//			os = new FileOutputStream(file);
	//			int bytesRead = 0;
	//			byte[] buffer = new byte[1024];
	//			while ((bytesRead = ins.read(buffer)) != -1) {
	//				os.write(buffer, 0, bytesRead);
	//			}
	//		} catch (Exception e) {
	//			throw new RuntimeException("调用inputStreamToFile产生异常："+e.getMessage());
	//		} finally {
	//			try {
	//				if (os != null ) {
	//					os.close();
	//				}
	//				if (ins != null) {
	//					ins.close();
	//				}
	//			} catch (IOException e) {
	//				throw new RuntimeException("inputStreamToFile关闭io产生异常：" + e.getMessage());
	//			}
	//		}
	//	}
}

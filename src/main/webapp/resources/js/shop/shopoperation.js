/**
 * js代码分为两个部分，第一个部分，就是异步获得初始化内容(主要是shopname和shoarea这两个要选单的)。
 * 第二个部分，异步提交表单
 */

$(function(){
	var shopId = getQueryString('shopId');
	var isEdit = shopId ? true : false;  //如果没有shopId就会默认认为这个操作是来注册店铺的
	var initUrl = '/o2o/shopadmin/getshopinitinfo';
	var registerShopUrl = '/o2o/shopadmin/registershop';
	var shopInfoUrl = "/o2o/shopadmin/getshopbyid?shopId=" + shopId;
	var editShopUrl = "/o2o/shopadmin/modifyshop";


	if(!isEdit) {
		getShopInitInfo();
	} else {
		getShopInfo(shopId);
	}


	// 通过店铺Id获取店铺信息
	function getShopInfo(shopId) {
		$.getJSON(shopInfoUrl, function(data) {
			if (data.success) {
				// 若访问成功，则依据后台传递过来的店铺信息为表单元素赋值
				var shop = data.shop;
				$('#shop-name').val(shop.shopName);
				$('#shop-addr').val(shop.shopAddr);
				$('#shop-phone').val(shop.phone);
				$('#shop-desc').val(shop.shopDesc);
				// 给店铺类别选定原先的店铺类别值
				var shopCategory = '<option data-id="'
					+ shop.shopCategory.shopCategoryId + '" selected>'
					+ shop.shopCategory.shopCategoryName + '</option>';
				var tempAreaHtml = '';
				// 初始化区域列表
				data.areaList.map(function(item, index) {
					tempAreaHtml += '<option data-id="' + item.areaId + '">'
					+ item.areaName + '</option>';
				});
				$('#shop-category').html(shopCategory);
				// 不允许选择店铺类别
				$('#shop-category').attr('disabled', 'disabled');
				$('#area').html(tempAreaHtml);
				// 给店铺选定原先的所属的区域
				$("#area option[data-id='" + shop.area.areaId + "']").attr(
						"selected", "selected");
			}
		});
	}




	function getShopInitInfo(){
		$.getJSON(initUrl, function(data) {
			if (data.success) { 
				var tempHtml = '';
				var tempAreaHtml = '';
				//在map函数里循环添加option，最后option的长度就不止一行咯
				data.shopCategoryList.map(function(item, index) { //map是新版jQ的写法
					tempHtml += '<option data-id="' + item.shopCategoryId + '">' + item.shopCategoryName + '</option>';
				});
				data.areaList.map(function(item, index){
					tempAreaHtml += '<option data-id="' + item.areaId + '">' + item.areaName + '</option>';
				});
				$('#shop-category').html(tempHtml);
				$('#area').html(tempAreaHtml);
			}
		});
	}

	
	
	
	//第二步就是跟提交相关的
	$('#submit').click(function(){
		
		var shop = {};
		if (isEdit) {
			shop.shopId = shopId;
		}
		shop.shopName = $('#shop-name').val();
		shop.shopAddr = $('#shop-addr').val();
		shop.phone = $('#shop-phone').val();
		shop.shopDesc = $('#shop-desc').val();
		shop.shopCategory = {
				shopCategoryId : $('#shop-category').find('option').not(function(){
					return !this.selected;
				}).data('id')
		};
		shop.area = {
				areaId : $('#area').find('option').not(function(){
					return !this.selected;
				}).data('id')
		};
		var shopImg = $('#shop-img')[0].files[0];
		// 生成表单对象，用于接收参数并传递给后台
		var formData = new FormData();
		// 添加图片流进表单对象里
		formData.append('shopImg', shopImg);
		// 将shop json对象转成字符流保存至表单对象key为shopStr的的键值对里
		formData.append('shopStr', JSON.stringify(shop));
		// 获取表单里输入的验证码
		var verifyCodeActual = $('#j_captcha').val();
		if (!verifyCodeActual) {
			$.toast('请输入验证码！');
			return;
		}
		formData.append('verifyCodeActual', verifyCodeActual);
		
		
		console.log(editShopUrl + "  " + registerShopUrl);
		console.log(isEdit ? editShopUrl : registerShopUrl);

		// 将数据提交至后台处理相关操作
		$.ajax({
			
			
			url : (isEdit ? editShopUrl : registerShopUrl), //判断是编辑还是注册
			type : 'POST',
			data: formData,
			contentType : false, //既要传文件，又要传文字
			processData : false,
			cache : false,
			success : function(data) {
				if(data.success) {
					$.toast('提交成功!');
				} else {
					$.toast('提交失败!' + data.errMsg);
				}
				$('#captcha_img').click(); //只要提交了，自动更新验证码
			}
		});
	});


})
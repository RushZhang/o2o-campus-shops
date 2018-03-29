/**
 * 
 */


function changeVerifyCode(img) {
	img.src = "../Kaptcha?" + Math.floor(Math.random() * 100);
}


//location.search是从当前URL的?号开始的字符串 
//		如:http://www.51js.com/viewthread.php?tid=22720 
//		它的search就是?tid=22720 
function getQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null) {
		return decodeURIComponent(r[2]);
	}
	return '';
}

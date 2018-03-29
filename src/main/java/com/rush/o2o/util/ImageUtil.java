package com.rush.o2o.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class ImageUtil {
	private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	private static final Random r = new Random();
	private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	
	
	/**
	 * 将CommonsMultipartFile转换成File类
	 */
	public static File transferCommonsMultipartFileToFile(CommonsMultipartFile cFile) {
		File newFile = new File(cFile.getOriginalFilename());
		try {
			cFile.transferTo(newFile);
		} catch (IllegalStateException e) {
			logger.error(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return newFile;
	}
	
	
	//处理缩略图，并返回新生成图片的相对值路径
	public static String generateThumbnail(InputStream thumbnailInputStream, String fileName, String targetAddr) {
		String realFileName = getRandomFileName();
		String extension = getFileExtension(fileName);
		makeDirPath(targetAddr);
		String relativeAddr = targetAddr + realFileName +extension;
		logger.debug("current relativeAddr is :" + relativeAddr);
		// 获取文件要保存到的目标路径
		File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
		logger.debug("current complete addr is :" + PathUtil.getImgBasePath() + relativeAddr);
		logger.debug("basePath is :" + basePath);
		try {
			Thumbnails.of(thumbnailInputStream).size(200, 200).watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/zhaizhai.png")), 0.8f).outputQuality(0.95f)
			.toFile(dest);
		} catch (IOException exception ) {
			logger.error(exception.toString());
			exception.printStackTrace();
		}
		return relativeAddr; 
	}
	
	
	
	
	
	
	private static String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}
	
	
	/**
	 * 创建目标路径所涉及到的目录，即/home/work/xiangze/xxx.jpg, 那么 home work xiangze
	 * 这三个文件夹都得自动创建
	 */
	private static void makeDirPath(String targetAddr) {
		String realFileParentPath = PathUtil.getImgBasePath() + targetAddr;
		File dirPath = new File(realFileParentPath);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
	}

	/**
	 * 生成随机文件名，时间加五位随机数
	 */
	public static String getRandomFileName() {
		// 获取随机的五位数
		int rannum = r.nextInt(89999) + 10000;
		String nowTimeStr = sDateFormat.format(new Date());
		return nowTimeStr + rannum;
	}

	/*
	 * 如果storePath是文件路径就删除文件
	 * 如果storePath是目录路径就删除目录下所有文件
	 */
	public static void deleteFileOrPath(String  storePath ) {
		File fileOrPath = new File(PathUtil.getImgBasePath() + storePath);
		if(fileOrPath.exists()) {
			if(fileOrPath.isDirectory()) {
				File files[] = fileOrPath.listFiles();
				for (int i = 0; i < files.length; i ++) {
					files[i].delete();
				}
			}
			fileOrPath.delete();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Thumbnails.of(new File("/Users/zhangweicheng/Desktop/熊.jpg"))
		.size(200, 200).watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/fox.png")), 0.25f).outputQuality(0.8f)
		.toFile("/Users/zhangweicheng/Desktop/的图.jpg");
	}
}

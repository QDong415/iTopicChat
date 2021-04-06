package com.dq.itopic.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import com.dq.itopic.R;


public class ValueUtil {

	private static final int DAY = 24 * 60 * 60;// 天

	public static List<String> StringToArrayList(String str) {
		ArrayList<String> investorFoucusDirectionList = new ArrayList<String>();
		if (str != null) {
			String[] zu = str.split("\\,");
			for (int i = 0; i < zu.length; i++) {
				if (zu[i].equals("")) {
					continue;
				}
				investorFoucusDirectionList.add(zu[i]);
			}
		}
		return investorFoucusDirectionList;
	}
	
	public static String ArrayListToString(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i != list.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public static long getTimeLong(String sTime) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(sTime);
		return date.getTime();
	}

	/**
	 * 根据时间戳获取描述性时间，如3分钟前，1天前
	 * @param timestamp
	 *            时间戳 单位为毫秒
	 * @return 时间字符串
	 */
	public static String getTimeStringFromNow(long timestamp) {
		return getTimeStringFromNow(timestamp,false);
	}

	/**
	 * 根据时间戳获取描述性时间，如3分钟前，1天前
	 * @param timestamp
	 *            时间戳 单位为毫秒
	 * @return 时间字符串
	 */
	public static String getTimeStringFromNow(long timestamp,boolean isMillisecond) {
		long timeGap;
		if(isMillisecond){
			long currentTime = System.currentTimeMillis();
			timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
		} else {
			timeGap = (System.currentTimeMillis()/1000) - timestamp;
		}
		String timeStr = null;
		if (timeGap > DAY) {// 1天以上
			timeStr = getSimpleDate(new Date(isMillisecond?timestamp:(timestamp * 1000)));
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			timeStr = sdf.format(new Date(isMillisecond?timestamp:(timestamp * 1000)));
		}
		return timeStr;
	}

	public static String getSimpleDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(date);
		return time;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * @param filename 七牛的文件名
	 * @param limitpx 限制的最大or最小边长
	 * @param max YES,是限制最大长，NO=限制最小长
	 * @return
	 */
	public static String getQiniuUrlByFileName(String filename, int limitpx, boolean max){
		if (TextUtils.isEmpty(filename) || filename.startsWith("http")) {
			//为null 或者 “” 或者全路径。直接返回原始图片
			return filename;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(ServiceConstants.QINIU_URL);
		sb.append(filename);
		if (!filename.endsWith(".gif")){
			sb.append("?imageView2/");
			sb.append(max?3:2);
			sb.append("/w/");
			sb.append(limitpx);
			sb.append("/h/");
			sb.append(limitpx);
		}
		return sb.toString();
	}

	/**
	 * @param filename 七牛的文件名
	 * @param isThumbnail 是否返回缩略图
	 * @return
	 */
	public static String getQiniuUrlByFileName(String filename,boolean isThumbnail){
		return getQiniuUrlWithParams(filename,isThumbnail?"?imageView2/1/w/240/h/240":"");
	}

	/**
	 * @param filename 七牛的文件名
	 * @param paramsString 指定后缀 ,模糊效果："?imageMogr2/blur/16x10"
	 * @return
	 */
	public static String getQiniuUrlWithParams(String filename,String paramsString){
		if (filename == null  || filename.equals("") || filename.startsWith("http")) {
			//为null 或者 “” 或者全路径。直接返回原始图片
			return filename;
		} else {
			StringBuilder sb = new StringBuilder(ServiceConstants.QINIU_URL);
			sb.append(filename);
			if (paramsString != null){
				sb.append(paramsString);
			}
			return sb.toString();
		}
	}

	//根据411527 算出 411500 ，如果是110103 算出 110000
	public static String findCityIdByAdCode(String adCode)
	{
		if (TextUtils.isEmpty(adCode)){
			return "110000";
		} else if (adCode.startsWith("11")||adCode.startsWith("12")||adCode.startsWith("31")||adCode.startsWith("50")){
			String cityIdHead = adCode.substring(0,2);
			return cityIdHead+"0000";
		} else {
			String cityIdHead = adCode.substring(0,4);
			return cityIdHead+"00";
		}
	}

	public static String findCompleteCityAdName(String cityname, String adname){
		StringBuilder sb = new StringBuilder();
		if (cityname != null) {
			sb.append(cityname);
			sb.append(" ");
		}
		if (adname != null) {
			sb.append(adname);
		}
		return sb.toString();
	}


	//算出价格String. 参数price单位是分
	public static String findMoneyString(int money)
	{
		float moneyfloat = money/100.0f;
		if (moneyfloat == (int)moneyfloat) {
			//没小数点
			return "" + ((int)moneyfloat);
		}else{
			float num = (float)(Math.round(moneyfloat*100)/100.0);
			return "" + num;
		}
	}

	public static String findVoiceLocalPathWithFileName(Context context,String fileName){
		return context.getFilesDir().getAbsolutePath() + "/" + fileName;
	}


	/**
	 * 版本号比较，第一个参数传当前版本，第2个参数传服务器返回的最新版本。-1表示需要更新
	 *
	 * @param version1
	 * @param version2
	 * @return 0代表相等，1代表version1大于version2，-1代表version1小于version2
	 *
	 */
	public static int compareVersion(String version1, String version2) {
		if (TextUtils.isEmpty(version1) || TextUtils.isEmpty(version2)){
			return 0;
		}
		if (version1.equals(version2)) {
			return 0;
		}
		String[] version1Array = version1.split("\\.");
		String[] version2Array = version2.split("\\.");
		int index = 0;
		// 获取最小长度值
		int minLen = Math.min(version1Array.length, version2Array.length);
		int diff = 0;
		// 循环判断每位的大小
		while (index < minLen
				&& (diff = Integer.parseInt(version1Array[index])
				- Integer.parseInt(version2Array[index])) == 0) {
			index++;
		}
		if (diff == 0) {
			// 如果位数不一致，比较多余位数
			for (int i = index; i < version1Array.length; i++) {
				if (Integer.parseInt(version1Array[i]) > 0) {
					return 1;
				}
			}

			for (int i = index; i < version2Array.length; i++) {
				if (Integer.parseInt(version2Array[i]) > 0) {
					return -1;
				}
			}
			return 0;
		} else {
			return diff > 0 ? 1 : -1;
		}
	}
}

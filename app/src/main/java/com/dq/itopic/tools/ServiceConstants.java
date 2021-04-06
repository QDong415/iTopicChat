package com.dq.itopic.tools;

public class ServiceConstants {

	public static final String IP = "https://api.itopic.com.cn/api/";

	public static final String IP_NOAPI = "https://api.itopic.com.cn/";

//	public static final String IP_NOAPI = "http://itopic.buildbees.cn/";

	// http请求时候的md5加密用的秘钥，开发者（包括android，ios，php）请自行统一修改，以确保app网络安全
	public static final String SIG_KEY = "iTopic2015";

	public static final String SHARE_TOPIC_IP = IP_NOAPI + "home/topic/detail?tid=";

	//七牛地址，在七牛后台生成
	public static final String QINIU_URL = "http://qiniu.itopic.com.cn/";

	//itopic huawei appid
	public static final String HUAWEI_APPID = "101315201";

	//声网appid
	public static final String AGORA_APPID = "eba4da1c39f94e23a06dbf32dbaa3768";

	//友盟一键登录（自动获取手机号）的key
	public static final String UM_VERIFY = "k++kpTgYt0KdYLMD/hd+54Qnym9XWepbf9C/7LDot8CsPRgvWSXdCOOwc6WvuubwNf5SdxifoeQiuk5AJILqvAeNP5Q4CRD345vtLcSLhEpsIMD0hXpkdFzmHIYj24CGFcfVefcQbisaaUzf2qqB6XfZwJ7r072EvEFCvOJEXq7xuUIAt1gfAzYE1ufdUE8JDYGeWmBbyrQ5gJQrhrBD2tHJ0evSiKh/fq7t2qWsJjUG6Ke+zv19SXqbB+2kg6iDFWALBRM5+kvDTYDZ6QqYpQeOY7ID4Owgk9pDjqmpOxwv0DZ6MYm3QA==";

	//友盟统计
	public static final String UM_APPKEY = "55a067dd67e58e715500691f";

}

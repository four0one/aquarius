package com.rpcframework.utils;

/**
 * 服务签名工具类 用于注册服务 查找服务
 * @author wei.chen1
 * @since 2018/1/25
 */
public class ServiceSignUtils {

	public static String sign(String serviceName, String method) {
		StringBuffer tmp = new StringBuffer();
		tmp.append(serviceName).append("#").append(method);
		return tmp.toString();
	}

	public static void main(String[] args) {
		String sign = sign("a", "b");
		System.out.println(sign);
	}
}

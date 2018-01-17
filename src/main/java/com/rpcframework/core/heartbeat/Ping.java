package com.rpcframework.core.heartbeat;

import java.io.Serializable;

/**
 * 客户端心跳
 * @author wei.chen1
 * @since 2018/1/17
 */
public class Ping implements Serializable{

	private String msg;

	public Ping(){
		this.msg = "Ping";
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}

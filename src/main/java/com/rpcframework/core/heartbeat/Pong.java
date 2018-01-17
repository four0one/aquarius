package com.rpcframework.core.heartbeat;

/**
 * 服务端返回
 * @author wei.chen1
 * @since 2018/1/17
 */
public class Pong {

	private String msg;

	public Pong() {
		this.msg = "Pong";
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}

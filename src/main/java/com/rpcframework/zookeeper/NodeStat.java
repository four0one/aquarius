package com.rpcframework.zookeeper;

import org.apache.zookeeper.data.Stat;

import java.io.UnsupportedEncodingException;

/**
 * @author wei.chen1
 * @since 2019/6/4
 */
public class NodeStat {

	private Stat stat;

	private String data;

	public NodeStat() {
		this.stat = new Stat();
	}

	public Stat getStat() {
		return stat;
	}

	public String getData() {
		return data;
	}

	public void setData(byte[] data) throws UnsupportedEncodingException {
		this.data = new String(data);
	}
}

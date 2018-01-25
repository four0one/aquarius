package com.rpcframework.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wei.chen1
 * @since 2018/1/25
 */
public class Person implements Serializable{

	private int id;

	private String name;

	private List<String> address = new ArrayList<>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAddress() {
		return address;
	}

	public void setAddress(List<String> address) {
		this.address = address;
	}

	public void addAddress(String address) {
		this.address.add(address);
	}
}

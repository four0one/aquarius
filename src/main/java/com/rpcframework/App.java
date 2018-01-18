package com.rpcframework;

import com.rpcframework.core.codec.kryo.KryoSerialize;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		KryoSerialize kryoSerialize = new KryoSerialize();
		try {
			byte[] serialize = kryoSerialize.serialize(null);
			Object deserialize = kryoSerialize.deserialize(serialize);
			System.out.println(new String(serialize).length());
			System.out.println(deserialize);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

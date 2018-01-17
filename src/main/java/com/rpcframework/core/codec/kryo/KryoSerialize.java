package com.rpcframework.core.codec.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
public class KryoSerialize {

	/**
	 * 序列化
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public byte[] serialize(Object object) throws IOException {
		Kryo kryo = KryoFactoryPool.getKryo();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Output out = new Output(output);
		kryo.writeClassAndObject(out, object);
		out.close();
		output.close();
		KryoFactoryPool.release(kryo);
		return output.toByteArray();
	}

	public Object deserialize(byte[] bytes) throws IOException {
		Kryo kryo = KryoFactoryPool.getKryo();
		ByteArrayInputStream input = new ByteArrayInputStream(bytes);
		Input in = new Input(input);
		Object object = kryo.readObject(in, Object.class);
		in.close();
		input.close();
		KryoFactoryPool.release(kryo);
		return object;
	}

}

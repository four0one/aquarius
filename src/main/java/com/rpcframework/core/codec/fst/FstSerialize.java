package com.rpcframework.core.codec.fst;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author wei.chen1
 * @since 2018/1/25
 */
public class FstSerialize {

	private FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

	/**
	 * 序列化
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public byte[] serialize(Object object) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		FSTObjectOutput out = conf.getObjectOutput(output);
		out.writeObject(object);
		out.flush();
		return output.toByteArray();
	}

	public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream input = new ByteArrayInputStream(bytes);
		FSTObjectInput in = conf.getObjectInput(input);
		Object object = in.readObject();
		return object;
	}

}

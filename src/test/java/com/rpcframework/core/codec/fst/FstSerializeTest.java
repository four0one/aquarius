package com.rpcframework.core.codec.fst;

import com.rpcframework.service.Person;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author wei.chen1
 * @since 2018/1/25
 */
public class FstSerializeTest {
	@Test
	public void serialize() throws Exception {
		FstSerialize fstSerialize = new FstSerialize();
		Person person = new Person();
		person.setId(18);
		person.setName("FST");
		person.addAddress("江苏 南京");
		person.addAddress("上海 上海");
		byte[] serialize = fstSerialize.serialize(person);

		FstSerialize fstSerialize2 = new FstSerialize();
		Person deserialize = (Person) fstSerialize2.deserialize(serialize);
		System.out.println(deserialize.getAddress());
	}

	@Test
	public void deserialize() throws Exception {
	}

}
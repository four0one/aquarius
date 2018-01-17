package com.rpcframework.core.codec.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.rpcframework.core.RpcRequest;
import com.rpcframework.core.RpcResponse;
import com.rpcframework.core.heartbeat.Ping;
import com.rpcframework.core.heartbeat.Pong;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * 序列化kryo池
 * @author wei.chen1
 * @since 2018/1/15
 */
public class KryoFactoryPool {

	private static KryoFactory factory = new KryoFactory() {
		public Kryo create() {
			Kryo kryo = new Kryo();
			kryo.setReferences(false);
			kryo.register(RpcRequest.class);
			kryo.register(RpcResponse.class);
			kryo.register(Ping.class);
			kryo.register(Pong.class);
			kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
			return kryo;
		}
	};

	public static final KryoPool pool = new KryoPool.Builder(factory).build();

	public static KryoPool getPool() {
		return pool;
	}

	public static Kryo getKryo() {
		return pool.borrow();
	}

	public static void release(Kryo kryo) {
		pool.release(kryo);
	}
}

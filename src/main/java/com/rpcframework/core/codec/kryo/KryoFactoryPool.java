package com.rpcframework.core.codec.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.rpcframework.core.RpcRequest;
import com.rpcframework.core.RpcResponse;
import com.rpcframework.core.heartbeat.Ping;
import com.rpcframework.core.heartbeat.Pong;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
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
			kryo.setRegistrationRequired(false);
			kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(
					new StdInstantiatorStrategy()));
			SynchronizedCollectionsSerializer.registerSerializers(kryo);
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

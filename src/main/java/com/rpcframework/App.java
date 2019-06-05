package com.rpcframework;

import com.rpcframework.zookeeper.CuratorConnect;
import com.rpcframework.zookeeper.NodeStat;
import com.rpcframework.zookeeper.ServiceWatcher;
import org.apache.zookeeper.CreateMode;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {
		/*System.out.println("Hello World!");
		KryoSerialize kryoSerialize = new KryoSerialize();
		try {
			byte[] serialize = kryoSerialize.serialize(null);
			Object deserialize = kryoSerialize.deserialize(serialize);
			System.out.println(new String(serialize).length());
			System.out.println(deserialize);
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		CuratorConnect curatorConnect = new CuratorConnect("127.0.0.1:2181","study");
		curatorConnect.connect();
		List<String> serviceList = curatorConnect.getNodeChildren("/");
		if (!CollectionUtils.isEmpty(serviceList)) {
			for(String serviceName:serviceList){
				List<String> serviceNodeList = curatorConnect.getNodeChildren("/" + serviceName);
				for(String nodeName:serviceNodeList){
					NodeStat node = curatorConnect.getNode("/" + serviceName + "/" + nodeName);
					System.out.println(serviceName+" : "+node.getData());
				}

			}
		}
	}
}

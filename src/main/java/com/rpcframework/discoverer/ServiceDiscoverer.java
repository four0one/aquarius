package com.rpcframework.discoverer;

import com.rpcframework.monitor.ServiceModel;

import java.util.List;
import java.util.Map;

/**
 * @author wei.chen1
 * @since 2019/6/4
 */
public interface ServiceDiscoverer {

	Map<String, List<ServiceModel>> find();

}

package com.rpcframework.core.pool;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wei.chen1
 * @since 2018/2/2
 */
public class PooledState {

	private final List<Channel> idelChannelList = new ArrayList<>();
	private final List<Channel> activeChannelList = new ArrayList<>();

	private final ReentrantLock lock = new ReentrantLock();
	private final Condition activeCondition = lock.newCondition();

	private final AtomicLong reqCounts = new AtomicLong(0);


	public void addIdelChannel(Channel pooledChannel) {
		idelChannelList.add(pooledChannel);
	}

	public void removeIdelChannel(Channel pooledChannel) {
		idelChannelList.remove(pooledChannel);
	}


	public void addActiveChannel(Channel pooledChannel) {
		activeChannelList.add(pooledChannel);
	}

	public void removeActiveChannel(Channel pooledChannel) {
		activeChannelList.remove(pooledChannel);
	}

	public boolean isIdelChannelListEmpty(){
		return idelChannelList.isEmpty();
	}

	public Channel getIdelChannel(){
		return idelChannelList.remove(0);
	}

	public Channel getIdelChannel(int i){
		return idelChannelList.get(i);
	}

	public int activeChannelSize(){
		return activeChannelList.size();
	}

	public int idleChannelSize(){
		return idelChannelList.size();
	}

	public void lock(){
		this.lock.lock();
	}

	public void unlock(){
		this.lock.unlock();
	}

	public void waitIdle() throws InterruptedException {
		this.activeCondition.await();
	}

	public void signIdle(){
		this.activeCondition.signal();
	}

	public void removeAllChannel(){
		this.idelChannelList.removeAll(idelChannelList);
		this.activeChannelList.removeAll(activeChannelList);
	}

	public AtomicLong getReqCounts() {
		return reqCounts;
	}


}

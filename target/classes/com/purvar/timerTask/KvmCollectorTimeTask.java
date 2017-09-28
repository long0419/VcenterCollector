package com.purvar.timerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.jcraft.jsch.JSchException;
import com.purvar.cache.LocalCache;
import com.purvar.collector.kvmCollector.JSchTool;
import com.purvar.collector.kvmCollector.KVMCollect;
import com.purvar.warpper.PropertiesWarpper;

public class KvmCollectorTimeTask extends CollectorTimeTask{

private static Logger log = LoggerFactory.getLogger(KvmCollectorTimeTask.class);
	
	private static final String API_METRIC  = "/metric";
	private static final String API_SERVICE = "/KvmService";
	
	private LocalCache<JSchTool> jSchToolCache;

	private Integer kvmPollingIntervalTime;
	
	private Timer timer;
	

	public KvmCollectorTimeTask(PropertiesWarpper propertiesWarpper) {
		super(propertiesWarpper);
		init(propertiesWarpper);
		this.kvmPollingIntervalTime = propertiesWarpper.getIntProperty("kvmPollingIntervalTime");
	}
	
	private void init(PropertiesWarpper propertiesWarpper){
		//加载jSchToolCache
		this.jSchToolCache = new LocalCache<>();
		List<Map<String,String>> kvmGroup = 
				propertiesWarpper.getGroup("kvm.username","kvm.password","kvm.host","kvm.port");
		for(Map<String,String> kvmMap : kvmGroup){
			
			String userName = kvmMap.get("kvm.username");
			String password = kvmMap.get("kvm.password");
			String host = kvmMap.get("kvm.host");
			Integer port = Integer.valueOf(kvmMap.get("kvm.port"));
			
			JSchTool jSchTool;
			try {
				jSchTool = new JSchTool(userName, password, host, port);
			} catch (JSchException e) {
				throw new RuntimeException(
						String.format("kvm 连接失败:host:%s, port:%s, username:%s, password:%s",host,port,userName,password));
			}
			
			this.jSchToolCache.set(userName, jSchTool);
		}
	}
	
	
	@Override
	public void run() {
		List<Map<String, Object>> list = null;
		
		//采集 
		try {
			list = collecting();
		} catch (Exception e) {
			log.error(e.toString());
		}
		
		//发送
		System.out.println(new Gson().toJson(list));
		
	}
	
	public List<Map<String, Object>> collecting() {
		List<Map<String, Object>> rList = new ArrayList<>();
		
		Object key = null;
		try {
			for(Entry<Object, JSchTool> entry : jSchToolCache.getCacheAll().entrySet()){
				key = entry.getKey();
				JSchTool instance = entry.getValue();
				rList.add(KVMCollect.collecting(instance));
			}
		} catch (Exception e) {
			throw new RuntimeException("vCenter 采集失败, key:"+key.toString(),e);
		}
		
		return rList;
	}

	@Override
	public synchronized void start() {
		this.timer = new Timer();
		timer.schedule(this, 0, kvmPollingIntervalTime*1000);
	}

	@Override
	public synchronized void stop() {
		kvmPollingIntervalTime = null;

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (jSchToolCache != null) {
			for (JSchTool instance : jSchToolCache.getAll()) {
				try {
					instance.close();
				} catch (Exception e) {
				}
			}
			jSchToolCache.clearAll();
			jSchToolCache = null;
		}
	}

}

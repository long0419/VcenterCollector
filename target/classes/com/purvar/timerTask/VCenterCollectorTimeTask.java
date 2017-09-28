package com.purvar.timerTask;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.purvar.cache.LocalCache;
import com.purvar.collector.vCenterCollector.VMStatsCollector;
import com.purvar.collector.vCenterCollector.VmCenter;
import com.purvar.collector.vCenterCollector.dto.MetricDataDto;
import com.purvar.collector.vCenterCollector.dto.ServiceChecks;
import com.purvar.collector.vCenterCollector.dto.ServiceDto;
import com.purvar.collector.vCenterCollector.dto.Tags;
import com.purvar.warpper.PropertiesWarpper;
import com.vmware.vim25.mo.ServiceInstance;

public class VCenterCollectorTimeTask extends CollectorTimeTask {

	private static final Logger logger = LoggerFactory.getLogger(VCenterCollectorTimeTask.class);
	
	private static final String API_METRIC  = "/metrics";
	private static final String API_SERVICE = "/finder";
//	private static final String API_METICTYPE = "/metric_type";
	
	private LocalCache<ServiceInstance> serviceInstanceCache;
	
	private Timer timer;
	
	private Integer ascounts ; //当前指标服务个数 计数器（只传一次）
	
	/** 定时器时间 **/
	private Integer vCenterPollingIntervalTime;
	private String reportUrl ;
	private String apiKey ;
	private VMStatsCollector vmstats ; 
	private String pollerName ;
	private String pollerIp ;
	
	public VCenterCollectorTimeTask(PropertiesWarpper propertiesWarpper) {
		super(propertiesWarpper);//拿到addressUrl
		this.vCenterPollingIntervalTime = propertiesWarpper.getIntProperty("vCenterPollingIntervalTime");
		this.reportUrl = propertiesWarpper.getStringProperty("pi.url");
		this.apiKey = propertiesWarpper.getStringProperty("apiKey");
		this.pollerName = propertiesWarpper.getStringProperty("poller_name");
		try {
			this.pollerIp = propertiesWarpper.getStringProperty("poller_ip") == null 
							? InetAddress.getLocalHost().getHostAddress() 
							: propertiesWarpper.getStringProperty("poller_ip") ;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		init(propertiesWarpper);
	}
	
	public void init(PropertiesWarpper propertiesWarpper){
		//加载ServiceInstance
		this.serviceInstanceCache = new LocalCache<>();
		List<Map<String,String>> mapGroup = 
				propertiesWarpper.getGroup("vcenter.url","vcenter.username","vcenter.password");
		for(Map<String,String> vcenterMap : mapGroup){
			
			String url = vcenterMap.get("vcenter.url");
			String userName = vcenterMap.get("vcenter.username");
			String password = vcenterMap.get("vcenter.password");
			
			ServiceInstance instance = null;
			try {
				instance = new ServiceInstance(new URL(url),userName,password,true);
			} catch (RemoteException | MalformedURLException e) {
				throw new RuntimeException(
						String.format("vCenter连接失败:url:%s, username:%s, password:%s",url,userName,password));
			}
			this.serviceInstanceCache.set(userName, instance);
			vmstats = new VMStatsCollector(this.reportUrl , 
					API_METRIC.concat("?host=" + url)
					.concat("&api_key=" +  apiKey)
					.concat("&type=vcenter")) ;
		}
	}
	

	@Override
	public void run() {
		//采集 
		try {
//			List<ServiceDto> list = 
			collecting();
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
		
	}
	
	public void collecting(){
		
		Object key = null;
		try {
			for(Entry<Object, ServiceInstance> entry :serviceInstanceCache.getCacheAll().entrySet()){
				key = entry.getKey();
				ServiceInstance instance = entry.getValue();
//				rList.add(VmCenter.collecting(instance , this.pollerIp , this.pollerName));
				
				MetricDataDto dto = new MetricDataDto() ;
				dto.setMetric("poller.up");
				dto.setValue("1");
				dto.setTimestamp(String.valueOf(System.currentTimeMillis()/1000));
				Tags tag =new Tags();
				tag.setType("vcenter");
				tag.setPoller(pollerName);
				tag.setPollerIp(pollerIp);
				dto.setTags(tag);
				
				ServiceChecks sc = new ServiceChecks();
				sc.setServiceDto(VmCenter.collecting(instance , this.pollerIp , this.pollerName));
				sc.setAgent_Check(dto);
//				System.out.println(instance.getServerConnection().getUrl().getHost());
				//发送
				try {
					//发送当前指标点metric值
					send(reportUrl , 
							API_SERVICE.concat("?host=" + instance.getServerConnection().getUrl().getHost())
							.concat("&api_key=" +  apiKey)
							.concat("&type=vcenter") , 
							sc) ;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				//发送vm相关指标内容
				vmstats.getPerformaceMetric(instance);
			}
		} catch (Exception e) {
			throw new RuntimeException("vCenter 采集失败, key:"+key.toString() + " error info : " + e);
		}
		
//		return rList;
	}

	@Override
	public synchronized void start() {
		this.timer = new Timer();
		timer.schedule(this, 0, vCenterPollingIntervalTime*1000);
	}

	@Override
	public synchronized void stop() {
		vCenterPollingIntervalTime = null;

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (serviceInstanceCache != null) {
			for (ServiceInstance instance : serviceInstanceCache.getAll()) {
				try {
					instance.getSessionManager().logout();
				} catch (Exception e) {
				}
			}
			serviceInstanceCache.clearAll();
			serviceInstanceCache = null;
		}
	}
	
}

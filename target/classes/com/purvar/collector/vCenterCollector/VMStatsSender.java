package com.purvar.collector.vCenterCollector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPOutputStream;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.purvar.collector.vCenterCollector.dto.MetricDataDto;
import com.purvar.collector.vCenterCollector.dto.Tags;
import com.purvar.util.MathUtil;

public class VMStatsSender implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(VMStatsSender.class);
	private static final String contentType = "application/json";
	private static final int timeout = 3000 ;//3000s
	
	private final BlockingQueue<Object> dumper;
	private volatile boolean cancelled;
	private final Hashtable<String, String> appConfig;
	
	private Gson gson ;
	private String serviceAddress ;
	private String interfaceAddress ;
	
	public VMStatsSender(BlockingQueue<Object> dumper, Hashtable<String, String> appConfig) {
		this.dumper = dumper;
		this.appConfig = appConfig;
		this.gson = new Gson() ;
	}

	@Override
	public void run() {
		String threadName = Thread.currentThread().getName();
		long total_stats = 0;
		int user_sleep_time = Integer.parseInt(appConfig.get("SLEEP_TIME")) * 1000;
		
		try {
			while (!cancelled) {

				// take the first one off the queue. this is a BlockingQueue so
				// it blocks the loop until something comes along on the queue.
				Object value = this.dumper.take();
				String[] values;
				if (value instanceof String[]) {
					values = (String[]) value;
					if (values.length != 0)
					total_stats += values.length;
					if(values.length > 0){
						List<MetricDataDto>  mdlist = getMetricDatas(values) ;
						this.send(serviceAddress, interfaceAddress, mdlist) ;
//						logger.info("mdlist上传数据 :" + gson.toJson(mdlist));
//						OpentsdbReporter.pushMetrics_DefaultRetries(mdlist);
//						logger.info("upload success") ;
						System.gc();
					}
				} else if (value instanceof String) {
					if (value.equals("dump_stats")) {
//						logger.debug(threadName + " sent " + total_stats);
						total_stats = 0;
					} else {
//						graphite.sendOne( (String) value);
						MetricDataDto mdt = processMetricTypeData((String)value);
						this.send(serviceAddress, interfaceAddress, mdt) ;
//						logger.info("mdt上传数据 :" + qgson.toJson(mdt));
//						logger.info("upload success") ;
						System.gc();
					}
				}
			}

		} catch (InterruptedException e) {
			logger.info("Thread: " + Thread.currentThread().getName() + "Interrupted: " + e.getMessage());
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			logger.info("Thread: " + Thread.currentThread().getName() + " +  Interrupted: " + e.getMessage());
			System.exit(0);
		}
	}

	public void cancel() {
		this.cancelled = true;
	}
	
	public List<MetricDataDto> getMetricDatas(String[] graphiteDataArray){
		List<MetricDataDto> data = new ArrayList<MetricDataDto>();
		MetricDataDto mt = null ;
		for (String graphiteData : graphiteDataArray) {
			mt = processMetricTypeData(graphiteData) ;
			data.add(mt);
		}
		return data ;
	}
	
	public MetricDataDto processMetricTypeData(String graphiteData){
		MetricDataDto mt = new MetricDataDto() ;
		Tags tag =  new Tags() ;
		
		String[] metricInfo = graphiteData.split("##");
		
		String[] values = null ;
		if(metricInfo.length>6){
			values = metricInfo[6].split(" ");
			mt.setMetric(metricInfo[5]);
			tag.setInstance(values[0]);
			
		}else{
			values = metricInfo[5].split(" ");
			mt.setMetric(values[0]);
		}

		String[] UUID_UNIT = metricInfo[0].split(" ") ;
		if(metricInfo[3].indexOf("ESX") != -1){
			tag.setvCenter(metricInfo[1]);
			tag.setHostUUID(UUID_UNIT[0]);
			tag.setHostSystem(metricInfo[4]);
		}else{
			tag.setVirtualMachine(metricInfo[4]);
			tag.setVmUUID(UUID_UNIT[0]);
		}
		mt.setValue(processValue(UUID_UNIT[1] , values[1]));
		mt.setTimestamp(values[2].trim());
		mt.setTags(tag);
		
		return mt ; 
	}
	
	public String processValue(String unit ,  String value){
		if(unit.equals("percent")){
			value = MathUtil.div(value, "100", 2) ;
		}else if(unit.equals("megaHertz")){
			value = MathUtil.div(value, "1024", 2) ;
		}else if(unit.equals("millisecond")){
			value = MathUtil.div(value, "1000", 2) ;			
		}else if(unit.equals("kiloBytes")){
			value = MathUtil.div(value, "1000", 2) ;			
		}else if(unit.equals("megaBytes")){
			value = MathUtil.div(value, 1024*1024 + "", 2) ;	
		}else if(unit.equals("kiloBytes")){
			value = MathUtil.div(value, "1024", 2) ;	
		}else if(unit.equals("kiloBytesPerSecond")){
			value = MathUtil.div(value, "1024", 2) ;
		}else if(unit.equals("second")){
			value = MathUtil.div(value, "1000", 2) ;
		}else if(unit.equals("kiloBytesPerSecond")){
			value = MathUtil.div(value, "1024", 2) ;
		}
		
		return value ;
	}
	
	protected int send(String serviceAddress, String interfaceAddress, Object jsonObj) throws IOException {
		String url = createUrl(serviceAddress, interfaceAddress);
		String body = null  ; 
		if(jsonObj.getClass().isInstance(String.class)){
			body = (String) jsonObj ;
		}else{
			body = createBody(jsonObj);
		}
		int code = Request.Post(url).setHeader("Content-Encoding", "gzip, deflate")
				.bodyString(body, ContentType.create(contentType, "utf-8")).connectTimeout(timeout).execute()
				.returnResponse().getStatusLine().getStatusCode();
		
		return code;
	}

	private String createUrl(String serviceAddress, String interfaceAddress) {
		return serviceAddress.concat(interfaceAddress);
	}
	
	private String createBody(Object jsonObj) throws IOException {
		String json = gson.toJson(jsonObj);
		return  json ;
//				compress(json) ;
	}

	public static String compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream(str.length());
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		gzip.write(str.getBytes());
		gzip.close();
		byte[] compressed = bos.toByteArray();
		bos.close();
		
		return compressed.toString() ;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	public String getInterfaceAddress() {
		return interfaceAddress;
	}

	public void setInterfaceAddress(String interfaceAddress) {
		this.interfaceAddress = interfaceAddress;
	}
}

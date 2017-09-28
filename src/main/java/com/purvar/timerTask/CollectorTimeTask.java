package com.purvar.timerTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.TimerTask;
import java.util.zip.GZIPOutputStream;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import com.google.gson.Gson;
import com.purvar.warpper.PropertiesWarpper;

public abstract class CollectorTimeTask extends TimerTask {

	private static final String contentType = "application/json";
	
	private static final int timeout = 3000;//3000s
	
	private String urlAddress;
	
	private Gson gson ;
	
	public CollectorTimeTask(PropertiesWarpper propertiesWarpper){
		this.urlAddress = propertiesWarpper.getStringProperty("pi_url");
		this.gson = new Gson();
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
		return json ;
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
	
	private String getUrlAddress(){
		return urlAddress;
	}
	
	public abstract void start();
	
	public abstract void stop();
}

package com.purvar.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.purvar.timerTask.CollectorTimeTask;
import com.purvar.timerTask.KvmCollectorTimeTask;
import com.purvar.timerTask.VCenterCollectorTimeTask;
import com.purvar.warpper.PropertiesWarpper;

public class RunService {

	private boolean isRun ;
	
	private PropertiesWarpper propertiesWarpper;
	
	private CollectorTimeTask vCenterCollectorTimeTask;
	
	private CollectorTimeTask kvmCollectorTimeTask;
	
	private CollectorTimeTask vCenterRelationShipTimeTask ;
	
	public synchronized void start(String configPath){
		//加载配置
		parseConfig(configPath);
		
		run();
		
		isRun = true;
	}
	
	public synchronized void stop(){
		propertiesWarpper = null;
		
		if (vCenterCollectorTimeTask != null) {
			vCenterCollectorTimeTask.stop();
			vCenterCollectorTimeTask = null;
		}
		if (kvmCollectorTimeTask != null) {
			kvmCollectorTimeTask.stop();
			kvmCollectorTimeTask = null;
		}
		
		if(vCenterRelationShipTimeTask != null){
			vCenterRelationShipTimeTask.stop();
			vCenterRelationShipTimeTask = null ;
		}
		
		isRun = false;
	}
	
	public synchronized boolean checkRun(){
		return isRun;
	}
	
	private void run(){
		final boolean vCenterCollectorEnable = propertiesWarpper.getBooleanProperty("vcenter.collector.enable");
		
		final boolean kvmCollectorEnabel = propertiesWarpper.getBooleanProperty("kvm.collector.enable");
		
		if(vCenterCollectorEnable){
			vCenterCollectorTimeTask = new VCenterCollectorTimeTask(propertiesWarpper);
			vCenterCollectorTimeTask.start();
		}
		if(kvmCollectorEnabel){
			kvmCollectorTimeTask = new KvmCollectorTimeTask(propertiesWarpper);
			kvmCollectorTimeTask.start();
		}
		
		isRun = true;
	}
	
	
	private void parseConfig(String configPath) {
		try {
			File file = new File(configPath);
			
			if(!file.exists()){
				throw new FileNotFoundException("not fount filePath:"+configPath);
			}
			
			FileInputStream fis = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();
			
			propertiesWarpper = new PropertiesWarpper(properties);
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException("not found configuration file,path:\r\n"+configPath);
		} catch (IOException e) {
			throw new RuntimeException("cannot open configuration file,path:\r\n"+configPath);
		}
	}
	
}
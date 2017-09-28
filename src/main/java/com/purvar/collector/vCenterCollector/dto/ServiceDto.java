package com.purvar.collector.vCenterCollector.dto;

import java.util.List;

import com.vmware.vim25.AboutInfo;

public class ServiceDto {

	private String name ;
	
	private List<DataCenterDto> dataCenter;
	
	private AboutInfo aboutInfo;

	private String pollerName ;
	
	private String pollerIp ;
	
	public String getPollerName() {
		return pollerName;
	}

	public void setPollerName(String pollerName) {
		this.pollerName = pollerName;
	}

	public String getPollerIp() {
		return pollerIp;
	}

	public void setPollerIp(String pollerIp) {
		this.pollerIp = pollerIp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DataCenterDto> getDataCenter() {
		return dataCenter;
	}

	public void setDataCenter(List<DataCenterDto> dataCenter) {
		this.dataCenter = dataCenter;
	}

	public AboutInfo getAboutInfo() {
		return aboutInfo;
	}

	public void setAboutInfo(AboutInfo aboutInfo) {
		this.aboutInfo = aboutInfo;
	}
}

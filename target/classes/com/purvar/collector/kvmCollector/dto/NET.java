package com.purvar.collector.kvmCollector.dto;

public class NET {
	
	private String name;
	private String uuid;
	private String active;//活跃
	private String enduring	;//持久
	private String autorun;//自启动
	private String bridge;//桥接
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getEnduring() {
		return enduring;
	}
	public void setEnduring(String enduring) {
		this.enduring = enduring;
	}
	public String getAutorun() {
		return autorun;
	}
	public void setAutorun(String autorun) {
		this.autorun = autorun;
	}
	public String getBridge() {
		return bridge;
	}
	public void setBridge(String bridge) {
		this.bridge = bridge;
	}
	
}

package com.purvar.collector.vCenterCollector.dto;

public class MetricType {
	
	private String id ; 
	private String integration ;
	private String metricName ;
	private String description ;
	private String metricType ;
	private String metricAlias ;
	private String perUnit ;
	private String pluralUnit ;
	private String type ;
	private String createAt ;
	private String updateAt ;
	private String level ;
	
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIntegration() {
		return integration;
	}
	public void setIntegration(String integration) {
		this.integration = integration;
	}
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMetricType() {
		return metricType;
	}
	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}
	public String getMetricAlias() {
		return metricAlias;
	}
	public void setMetricAlias(String metricAlias) {
		this.metricAlias = metricAlias;
	}
	public String getPerUnit() {
		return perUnit;
	}
	public void setPerUnit(String perUnit) {
		this.perUnit = perUnit;
	}
	public String getPluralUnit() {
		return pluralUnit;
	}
	public void setPluralUnit(String pluralUnit) {
		this.pluralUnit = pluralUnit;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCreateAt() {
		return createAt;
	}
	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}
	public String getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(String updateAt) {
		this.updateAt = updateAt;
	}
	
}

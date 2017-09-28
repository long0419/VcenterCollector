package com.purvar.collector.vCenterCollector.dto;

public class ServiceChecks {
	
//	private List<ServiceDto> list ;
	private ServiceDto list ;
	
	private MetricDataDto agent_Check ;

//	public List<ServiceDto> getList() {
//		return list;
//	}
//
//	public void setList(List<ServiceDto> list) {
//		this.list = list;
//	}

	public MetricDataDto getAgent_Check() {
		return agent_Check;
	}

	public ServiceDto getServiceDto() {
		return list;
	}

	public void setServiceDto(ServiceDto serviceDto) {
		this.list = serviceDto;
	}

	public void setAgent_Check(MetricDataDto agent_Check) {
		this.agent_Check = agent_Check;
	}
	
}

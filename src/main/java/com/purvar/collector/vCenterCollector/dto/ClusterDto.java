package com.purvar.collector.vCenterCollector.dto;

import java.util.List;

import com.vmware.vim25.ComputeResourceSummary;

public class ClusterDto {

	private String name;
	private ComputeResourceSummary clusterSummary;
	private List<HostDto> hosts ;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ComputeResourceSummary getClusterSummary() {
		return clusterSummary;
	}
	public void setClusterSummary(ComputeResourceSummary clusterSummary) {
		this.clusterSummary = clusterSummary;
	}
	public List<HostDto> getHosts() {
		return hosts;
	}
	public void setHosts(List<HostDto> hosts) {
		this.hosts = hosts;
	}
	
}

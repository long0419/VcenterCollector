package com.purvar.collector.vCenterCollector.dto;

import java.util.List;

import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.NetworkSummary;

public class DataCenterDto {

	private String name;
	
	private List<ClusterDto> clusters;
	
	private List<DatastoreSummary> datastoreSummarys;
	
	private List<NetworkSummary> networkSummarys;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public List<ClusterDto> getClusters() {
		return clusters;
	}

	public void setClusters(List<ClusterDto> clusters) {
		this.clusters = clusters;
	}

	public List<DatastoreSummary> getDatastoreSummarys() {
		return datastoreSummarys;
	}

	public void setDatastoreSummarys(List<DatastoreSummary> datastoreSummarys) {
		this.datastoreSummarys = datastoreSummarys;
	}

	public List<NetworkSummary> getNetworkSummarys() {
		return networkSummarys;
	}

	public void setNetworkSummarys(List<NetworkSummary> networkSummarys) {
		this.networkSummarys = networkSummarys;
	}

	
}

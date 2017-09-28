package com.purvar.collector.vCenterCollector.dto;

import java.util.List;

import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.HostListSummary;

public class HostDto {

	
	private List<VmDto> vms;
	
	private HostListSummary hostSummary;
	
	private List<DatastoreSummary> datastoresSummarys;
	
	private MetricDataDto agent_Check ;
	
	public MetricDataDto getAgent_Check() {
		return agent_Check;
	}

	public void setAgent_Check(MetricDataDto agent_Check) {
		this.agent_Check = agent_Check;
	}

	public List<VmDto> getVms() {
		return vms;
	}

	public void setVms(List<VmDto> vms) {
		this.vms = vms;
	}

	public HostListSummary getHostSummary() {
		return hostSummary;
	}

	public void setHostSummary(HostListSummary hostSummary) {
		this.hostSummary = hostSummary;
	}

	public List<DatastoreSummary> getDatastoresSummarys() {
		return datastoresSummarys;
	}

	public void setDatastoresSummarys(List<DatastoreSummary> datastoresSummarys) {
		this.datastoresSummarys = datastoresSummarys;
	}
}

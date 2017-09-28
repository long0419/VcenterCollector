package com.purvar.collector.vCenterCollector.dto;

import java.util.List;

import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.VirtualMachineSummary;

public class VmDto {

	private MetricDataDto agent_Check ;
	
	private VirtualMachineSummary vmSummary;

	private List<DatastoreSummary> datastoreSummarys;

	public MetricDataDto getAgent_Check() {
		return agent_Check;
	}

	public void setAgent_Check(MetricDataDto agent_Check) {
		this.agent_Check = agent_Check;
	}

	public VirtualMachineSummary getVmSummary() {
		return vmSummary;
	}

	public void setVmSummary(VirtualMachineSummary vmSummary) {
		this.vmSummary = vmSummary;
	}

	public List<DatastoreSummary> getDatastoreSummarys() {
		return datastoreSummarys;
	}

	public void setDatastoreSummarys(List<DatastoreSummary> datastoreSummarys) {
		this.datastoreSummarys = datastoreSummarys;
	}

}

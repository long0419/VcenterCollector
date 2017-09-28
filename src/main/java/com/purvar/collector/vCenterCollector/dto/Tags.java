package com.purvar.collector.vCenterCollector.dto;

/**
 * 
 * @author long-laptop
 *
 */
public class Tags {

	private String HostSystem; // 当前cluster下主机ip
	private String hostUUID;
	private String vmUUID;
	private String instance;
	private String VirtualMachine;
	private String vCenter; // 标识当前主机vCenter的ip
	private String type ; //vcenter kvm
	private String poller ;
	private String pollerIp ;

	public String getPollerIp() {
		return pollerIp;
	}

	public void setPollerIp(String pollerIp) {
		this.pollerIp = pollerIp;
	}

	public String getPoller() {
		return poller;
	}

	public void setPoller(String poller) {
		this.poller = poller;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getvCenter() {
		return vCenter;
	}

	public void setvCenter(String vCenter) {
		this.vCenter = vCenter;
	}

	public String getHostSystem() {
		return HostSystem;
	}

	public void setHostSystem(String hostSystem) {
		if (hostSystem.contains("、")) {
			hostSystem = hostSystem.replaceAll("、", ".");
		}
		HostSystem = hostSystem;
	}

	public String getHostUUID() {
		return hostUUID;
	}

	public void setHostUUID(String hostUUID) {
		this.hostUUID = hostUUID;
	}

	public String getVmUUID() {
		return vmUUID;
	}

	public void setVmUUID(String vmUUID) {
		this.vmUUID = vmUUID;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getVirtualMachine() {
		return VirtualMachine;
	}

	public void setVirtualMachine(String virtualMachine) {
		if (virtualMachine.contains("、")) {
			virtualMachine = virtualMachine.replaceAll("、", ".");
		}
		VirtualMachine = virtualMachine;
	}

}

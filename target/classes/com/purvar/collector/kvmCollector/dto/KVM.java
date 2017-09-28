package com.purvar.collector.kvmCollector.dto;

public class KVM {

	String uuid;
    String InstanceRunningId;//运行时ID
    String InstanceName;//虚拟机实例名称
    String InstanceState;//虚拟机状态
    String OSType;//系统类型
    String CPUNumber;//CPU数量(个)
    String CPUTime;//CPU时间(s)
    String MAXMemory;//最大使用内存(MB)
    String MemoryUsed;//已用内存(MB)
//  String DiskNumber;//硬盘数量(个)
    
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getInstanceRunningId() {
		return InstanceRunningId;
	}
	public void setInstanceRunningId(String instanceRunningId) {
		InstanceRunningId = instanceRunningId;
	}
	public String getInstanceName() {
		return InstanceName;
	}
	public void setInstanceName(String instanceName) {
		InstanceName = instanceName;
	}
	public String getInstanceState() {
		return InstanceState;
	}
	public void setInstanceState(String instanceState) {
		InstanceState = instanceState;
	}
	public String getOSType() {
		return OSType;
	}
	public void setOSType(String oSType) {
		OSType = oSType;
	}
	public String getCPUNumber() {
		return CPUNumber;
	}
	public void setCPUNumber(String cPUNumber) {
		CPUNumber = cPUNumber;
	}
	public String getCPUTime() {
		return CPUTime;
	}
	public void setCPUTime(String cPUTime) {
		CPUTime = cPUTime;
	}
	public String getMAXMemory() {
		return MAXMemory;
	}
	public void setMAXMemory(String mAXMemory) {
		MAXMemory = mAXMemory;
	}
	public String getMemoryUsed() {
		return MemoryUsed;
	}
	public void setMemoryUsed(String memoryUsed) {
		MemoryUsed = memoryUsed;
	}
    
    
	
}

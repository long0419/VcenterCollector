package com.purvar.collector.vCenterCollector;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.purvar.collector.vCenterCollector.dto.ClusterDto;
import com.purvar.collector.vCenterCollector.dto.DataCenterDto;
import com.purvar.collector.vCenterCollector.dto.HostDto;
import com.purvar.collector.vCenterCollector.dto.MetricDataDto;
import com.purvar.collector.vCenterCollector.dto.ServiceDto;
import com.purvar.collector.vCenterCollector.dto.Tags;
import com.purvar.collector.vCenterCollector.dto.VmDto;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.NetworkSummary;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.mo.ClusterComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class VmCenter {

	public static void main(String[] args) throws RemoteException, MalformedURLException {

		ServiceInstance si = new ServiceInstance(new URL("https://172.29.230.50/sdk"), "administrator@vsphere.local",
				"taizhang123Purvar.", true);
		while (true) {
			System.out.println(new Gson().toJson(VmCenter.collecting(si , "" , "")));
		}
	}

	public static ServiceDto collecting(ServiceInstance si , String pollerIp , String pollerName) throws InvalidProperty, RuntimeFault, RemoteException {

		ServiceDto serviceDto = new ServiceDto();
		serviceDto.setAboutInfo(si.getAboutInfo());
		serviceDto.setName(si.getRootFolder().getName());
		serviceDto.setPollerIp(pollerIp);
		serviceDto.setPollerName(pollerName);
		
		// 拿到 dataCenter
		List<DataCenterDto> dataCenterList = new ArrayList<>();
		for (ManagedEntity ds : si.getRootFolder().getChildEntity()) {
			Datacenter dataCenter = (Datacenter) ds;

			DataCenterDto dataCenterDto = new DataCenterDto();
			dataCenterDto.setName(dataCenter.getName());

			List<DatastoreSummary> dataCenter_datastore_summarys = new ArrayList<>();
			for (ManagedEntity datastoreManagedEntity : dataCenter.getDatastoreFolder().getChildEntity()) {
				Datastore datastore = (Datastore) datastoreManagedEntity;
				dataCenter_datastore_summarys.add(datastore.getSummary());
			}
			dataCenterDto.setDatastoreSummarys(dataCenter_datastore_summarys);

			List<NetworkSummary> dataCenter_network_summaryList = new ArrayList<>();
			for (Network network : dataCenter.getNetworks()) {
				dataCenter_network_summaryList.add(network.getSummary());
			}

			List<ClusterDto> clusters = new ArrayList<>();
			// 拿到 ClusterComputeResource
			for (ManagedEntity clusterManagedEntity : dataCenter.getHostFolder().getChildEntity()) {
				ClusterComputeResource cluster = (ClusterComputeResource) clusterManagedEntity;

				ClusterDto clusterDto = new ClusterDto();
				clusterDto.setName(cluster.getName());
				clusterDto.setClusterSummary(cluster.getSummary());

				List<HostDto> hostDtos = new ArrayList<>();
				HostSystem[] hosts = cluster.getHosts();

				// 拿到 host
				for (HostSystem host : hosts) {
					HostDto hostDto = new HostDto();
					HostListSummary summary = host.getSummary();
					summary.getHardware().setOtherIdentifyingInfo(null);
					summary.getRuntime().setHealthSystemRuntime(null);
					summary.getRuntime().setTpmPcrValues(null);

					if (!summary.getOverallStatus().equals("gray")) {
						MetricDataDto dto = new MetricDataDto();
						dto.setMetric("paasinsight.agent.up");
						dto.setValue("1");
						dto.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000));
						Tags tag = new Tags();
						tag.setHostSystem(summary.getConfig().getName());
						dto.setTags(tag);

						hostDto.setAgent_Check(dto);
					}

					hostDto.setHostSummary(summary);

					List<DatastoreSummary> datastoresSummarys = new ArrayList<>();
					for (Datastore datastore : host.getDatastores()) {
						datastoresSummarys.add(datastore.getSummary());
					}
					hostDto.setDatastoresSummarys(datastoresSummarys);

					// 拿到vm
					List<VmDto> vms = new ArrayList<>();
					for (VirtualMachine vm : host.getVms()) {
						VmDto vmDto = new VmDto();

						VirtualMachineSummary vmSummary = vm.getSummary();
						// 针对每台虚拟设备添加预警监测
						if (!vmSummary.getOverallStatus().equals("gray")) {
							MetricDataDto dto = new MetricDataDto();
							dto.setMetric("paasinsight.agent.up");
							dto.setValue("1");
							dto.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000));
							Tags tag = new Tags();
							tag.setVirtualMachine(vm.getConfig().getName());
							dto.setTags(tag);
							vmDto.setAgent_Check(dto);
						}

						VirtualMachineRuntimeInfo vmRuntime = vmSummary.getRuntime();
						vmRuntime.setOfflineFeatureRequirement(null);
						vmRuntime.setFeatureRequirement(null);
						vmDto.setVmSummary(vmSummary);

						List<DatastoreSummary> vm_datastore_summary = new ArrayList<>();
						for (Datastore datastore : vm.getDatastores()) {
							vm_datastore_summary.add(datastore.getSummary());
						}
						vmDto.setDatastoreSummarys(vm_datastore_summary);
						vms.add(vmDto);
					}

					hostDto.setVms(vms);
					hostDtos.add(hostDto);
				}

				clusterDto.setHosts(hostDtos);
				clusters.add(clusterDto);
			}

			dataCenterDto.setClusters(clusters);
			dataCenterList.add(dataCenterDto);
		}
		serviceDto.setDataCenter(dataCenterList);
		return serviceDto;
	}

}

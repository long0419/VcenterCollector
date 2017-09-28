package com.purvar.collector.kvmCollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.jcraft.jsch.JSchException;
import com.purvar.collector.kvmCollector.dto.KVM;
import com.purvar.collector.kvmCollector.dto.NET;

public class KVMCollect {
	
	public static void main(String[] args) throws JSchException {
		JSchTool instance = new JSchTool("root","Founder_12345","172.29.231.45",22);
		while(true){
			System.out.println(new Gson().toJson(KVMCollect.collecting(instance)));
		}
	}
	
	public static Map<String, Object> collecting(JSchTool instance) throws JSchException {
		
		// 2.执行相关的命令,获取主机名称list
		String hostsStr = instance.execCmd("virsh list");
		List<String> hostNameList = getHostNameList(hostsStr);

		// 3.执行相关的命令,获取每台主机基本信息
		Map<String, KVM> hostInfoMap = getHostInfo(hostNameList,instance);

		// 4.执行相关的命令,获取存储信息
		String storeCommand = "virsh pool-list";
		String storeStr = instance.execCmd(storeCommand);
		List<String> storeList = getStoreInfo(storeStr);

		// 5.执行相关的命令,获取每个存储池下面的主机信息
		Map<String, List<Map<String, KVM>>> mStoreMap = getStoreDetailInfo(storeList,instance);
		Map<String, Object> resMap = new HashMap<String, Object>();

		// 6.执行相关的命令,获取网络信息
		String networkCommand = "virsh net-list";
		String networkStr = instance.execCmd(networkCommand);
		HashMap<String, NET> netMapInfo = getNetworkInfo(networkStr,instance);

		resMap.put("kvm", hostInfoMap);
		resMap.put("store", mStoreMap);
		resMap.put("network", netMapInfo);
		
		return resMap;
	}
	
	/**
	 * 返回主机名的list
	 * 
	 * @param str
	 * @return
	 */
	private static List<String> getHostNameList(String str) {
		String[] strArr = str.split("\\n");
		List<String> hostList = new ArrayList<String>();
		List<String> mList;
		for (int j = 3; j < strArr.length; j++) { // 字符串换行分割后，下标0-2的数值是表格名称和分割线
			mList = new ArrayList<String>();
			String[] arr = strArr[j].split(" ");
			for (int i = 0; i < arr.length; i++) {
				if (!arr[i].equals("")) {
					mList.add(arr[i]);
				}
			}
			hostList.add(mList.get(1));
		}
		return hostList;
	}

	/**
	 * 返回主机的基本信息
	 * 
	 * @param str
	 * @return
	 */
	private static KVM getHostBaseInfo(String str) {
		KVM kvm = new KVM();
		String[] strArr = str.split("\\n");
		kvm.setUuid(formatStr(strArr[3]));
		kvm.setInstanceRunningId(formatStr(strArr[1]));// 运行时ID
		kvm.setInstanceName(formatStr(strArr[2]));// 虚拟机实例名称
		kvm.setInstanceState(formatStr(strArr[5]));// 虚拟机状态
		kvm.setOSType(formatStr(strArr[4]));// 系统类型
		kvm.setCPUNumber(formatStr(strArr[6]));// CPU数量(个)
		kvm.setCPUTime(formatStr(strArr[7]));// CPU时间(s)
		kvm.setMAXMemory(formatStr(strArr[8]));// 最大使用内存
		kvm.setMemoryUsed(formatStr(strArr[9]));// 已用内存
		// String DiskNumber = "";//硬盘数量(个) 返回结果里面没有硬盘个数的显示
		return kvm;
	}

	/**
	 * 获取每台主机的基本信息
	 * 
	 * @param list
	 * @return
	 * @throws JSchException
	 */
	private static Map<String, KVM> getHostInfo(List<String> list,JSchTool instance) throws JSchException {
		Map<String, KVM> map = new HashMap<String, KVM>();
		for (int i = 0; i < list.size(); i++) {
			String commandStr = "virsh dominfo" + " " + list.get(i);
			String hostInfoStr = instance.execCmd(commandStr);
			map.put(list.get(i), getHostBaseInfo(hostInfoStr));
		}
		return map;
	}

	/**
	 * 获取存储池列表
	 * 
	 * @return
	 */
	private static List<String> getStoreInfo(String str) {
		String[] strArr = str.split("\\n");
		List<String> storeList = new ArrayList<String>();
		List<String> mList;
		for (int j = 3; j < strArr.length; j++) { // 字符串换行分割后，下标0-2的数值是表格名称和分割线
			mList = new ArrayList<String>();
			String[] arr = strArr[j].split(" ");
			for (int i = 0; i < arr.length; i++) {
				if (!arr[i].equals("")) {
					mList.add(arr[i]);
				}
			}
			storeList.add(mList.get(0));
		}
		return storeList;
	}

	/**
	 * 获取所有存储池下的每个池中的主机信息
	 * 
	 * @param storeList
	 * @return
	 * @throws JSchException
	 */

	private static Map<String, List<Map<String, KVM>>> getStoreDetailInfo(List<String> storeList,JSchTool instance) throws JSchException {
		Map<String, List<Map<String, KVM>>> map = new HashMap<String, List<Map<String, KVM>>>();
		;
		for (String str : storeList) {
			String storeDetail = instance.execCmd("virsh vol-list --details" + " " + str);
			List<Map<String, KVM>> list = getStoreHostDetail(storeDetail,instance);
			map.put(str, list);
		}
		return map;
	}

	/**
	 * 每个存储池下面包含的主机及主机信息
	 * 
	 * @param str
	 * @return
	 * @throws JSchException
	 */
	private static List<Map<String, KVM>> getStoreHostDetail(String str,JSchTool instance) throws JSchException {
		String[] strArr = str.split("\\n");
		Map<String, KVM> map = null;
		List<Map<String, KVM>> list = new ArrayList<Map<String, KVM>>();
		List<String> mList;
		for (int j = 3; j < strArr.length; j++) { // 字符串换行分割后，下标0-2的数值是表格名称和分割线
			mList = new ArrayList<String>();
			String[] arr = strArr[j].split(" ");
			for (int i = 0; i < arr.length; i++) {
				if (!arr[i].equals("")) {
					mList.add(arr[i]);
				}
			}

			if (mList.get(0).indexOf(".") > 0 && mList.get(0).split("\\.")[1].equals("img")) {
				map = new HashMap<String, KVM>();
				String hostName = mList.get(0).split("\\.")[0];
				String commandStr = "virsh dominfo" + " " + hostName;
				String hostInfoStr = instance.execCmd(commandStr);
				map.put(hostName, getHostBaseInfo(hostInfoStr));
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 获取网络信息
	 * 
	 * @param str
	 * @return
	 * @throws JSchException
	 */
	private static HashMap<String, NET> getNetworkInfo(String str,JSchTool instance) throws JSchException {
		String[] strArr = str.split("\\n");
		List<String> netList = new ArrayList<String>();
		List<String> mList;
		for (int j = 3; j < strArr.length; j++) { // 字符串换行分割后，下标0-2的数值是表格名称和分割线
			mList = new ArrayList<String>();
			String[] arr = strArr[j].split(" ");
			for (int i = 0; i < arr.length; i++) {
				if (!arr[i].equals("")) {
					mList.add(arr[i]);
				}
			}
			netList.add(mList.get(0));
		}

		HashMap<String, NET> map = null;
		for (String mStr : netList) {
			String netCommand = "virsh net-info" + " " + mStr;
			String networkStr = instance.execCmd(netCommand);
			NET net = operNetInfo(networkStr);
			map = new HashMap<String, NET>();
			map.put(mStr, net);
		}
		return map;
	}

	private static NET operNetInfo(String str) {
		NET net = new NET();
		String[] strArr = str.split("\\n");
		net.setActive(formatStr(strArr[3]));
		net.setUuid(formatStr(strArr[2]));
		net.setAutorun(formatStr(strArr[5]));
		net.setBridge(formatStr(strArr[6]));
		net.setEnduring(formatStr(strArr[4]));
		net.setName(formatStr(strArr[1]));
		return net;
	}

	/**
	 * 去除字符串中间的空格 将中文：替换为英文:
	 * 
	 * @param str
	 * @return
	 */
	private static String formatStr(String str) {
		String result;
		if (str.indexOf("：") > 0) {
			result = str.replaceAll(" ", "").replaceAll("：", ":");
		} else {
			result = str.replaceAll(" ", "");
		}

		return result.split("\\:")[1];
	}
}

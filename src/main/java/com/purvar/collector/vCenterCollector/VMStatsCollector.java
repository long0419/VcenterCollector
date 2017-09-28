package com.purvar.collector.vCenterCollector;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ServiceInstance;

//this is the consumer in the arrangement
//this goes and gets the stats for a particular VM
public class VMStatsCollector {
	
	private String serviceAddress ;
	private String interfaceAddress ;
	
	public VMStatsCollector(String serviceAddress , String interfaceAddress){
		this.serviceAddress =  serviceAddress ;
		this.interfaceAddress = interfaceAddress ;
	}
	

	public void getPerformaceMetric(ServiceInstance si) {

		Properties config = new Properties();
		Path resourceDirectory = Paths.get("src/main/resources/config.conf");
		String filePath = resourceDirectory.toString() ;
		
//		String filePath = "config.conf" ;
        InputStream fis;
		try {
			fis = new FileInputStream(filePath);
			config.load(fis);  
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		String tmp_max_vmstat = config.getProperty("MAX_VMSTAT_THREADS")==null ? "8" : config.getProperty("MAX_VMSTAT_THREADS") ;
		int MAX_VMSTAT_THREADS = Integer.parseInt(tmp_max_vmstat);
		String tmp_esxstat = config.getProperty("MAX_ESXSTAT_THREADS")==null ? "4" : config.getProperty("MAX_ESXSTAT_THREADS") ;
		int MAX_ESXSTAT_THREADS = Integer.parseInt(tmp_esxstat);
		
		List<String> statIncludes = new ArrayList<String>(){{add("cpu");}}; //默认开启一个服务
		statIncludes.addAll(Arrays.asList(config.getProperty("STAT_INCLUDES").split(","))) ;
		
		Hashtable<String, String> appConfig = new Hashtable<String, String>();
		
		// use a hashtable to store performance id information
		Hashtable<String, Hashtable<String, String>> perfKeys = new Hashtable<String, Hashtable<String, String>>();
		// BlockingQueue to store managed objects - basically anything that
		// vmware knows about
		BlockingQueue<Object> vm_mob_queue = new ArrayBlockingQueue<Object>(20000);
		BlockingQueue<Object> esx_mob_queue = new ArrayBlockingQueue<Object>(10000);
		// BlockingQueue to store arrays of stats - each managed object
		// generates a bunch of strings that are stored in
		BlockingQueue<Object> sender = new ArrayBlockingQueue<Object>(60000);

		// Initialize these vmware types as nulls so we can see if things work
		// properly
		PerformanceManager perfMgr = si.getPerformanceManager();
		PerfCounterInfo[] counters = perfMgr.getPerfCounter();
		// build a hash lookup to turn the counter 23 into
		// 'disk.this.that.the.other'
		// These are not sequential.
		for (int i = 0; i < counters.length; i++) {
			String group = counters[i].getGroupInfo().getKey();
			String unit = counters[i].getUnitInfo().getKey() ;
			
			//排除采集指标
//			if (statExcludes.contains(group)) continue;
			
			// create a temp hash to push onto the big hash
			Hashtable<String, String> temp_hash = new Hashtable<String, String>();
			String path = group + "." + counters[i].getNameInfo().getKey() ;
//					+ unitInfo;
			// this is a key like cpu.run.0.summation
			temp_hash.put("key", path);
			temp_hash.put("unit" , unit) ;
			// one of average, latest, maximum, minimum, none, summation
			temp_hash.put("rollup", counters[i].getRollupType().toString());
			// one of absolute, delta, rate
			temp_hash.put("statstype", counters[i].getStatsType().toString());
			// it's important to understand that the counters aren't sequential,
			// so they have their own id.
			perfKeys.put("" + counters[i].getKey(), temp_hash);
		}

		Hashtable<String, Hashtable<String, String>> inperfKeys = new Hashtable<String, Hashtable<String, String>>();
		if(!statIncludes.isEmpty()){
			for(int i = 0 ; i < counters.length ; i++){
				String group = counters[i].getGroupInfo().getKey();
				if (statIncludes.contains(group)){
					inperfKeys.put("" + counters[i].getKey(), perfKeys.get("" + counters[i].getKey())) ;
				}
			}
		}else{
			inperfKeys = perfKeys ;
		}
		
		String graphEsx = config.getProperty("ESX_STATS") == null ? "true" : config.getProperty("ESX_STATS");
		appConfig.put("graphEsx", graphEsx);
		appConfig.put("MAX_VMSTAT_THREADS", String.valueOf(MAX_VMSTAT_THREADS));
	    appConfig.put("MAX_ESXSTAT_THREADS", String.valueOf(MAX_ESXSTAT_THREADS));
		
	    
	    String SLEEP_TIME = config.getProperty("SLEEP_TIME") == null ? "300" : config.getProperty("SLEEP_TIME") ;
        appConfig.put("SLEEP_TIME",SLEEP_TIME);
        String CACHED_LOOP_CYCLES = config.getProperty("CACHED_LOOP_CYCLES") == null ? "4800" : config.getProperty("CACHED_LOOP_CYCLES");
        appConfig.put("CACHED_LOOP_CYCLES", CACHED_LOOP_CYCLES);
        
        String SEND_ALL_PERIODS = config.getProperty("SEND_ALL_PERIODS") == null ? "true" : config.getProperty("SEND_ALL_PERIODS");
        appConfig.put("SEND_ALL_PERIODS",SEND_ALL_PERIODS);
        String USE_FQDN = config.getProperty("USE_FQDN") == null ? "true" : config.getProperty("USE_FQDN") ;
        appConfig.put("USE_FQDN",USE_FQDN);
        String SEND_ALL_ABSOLUTE = config.getProperty("SEND_ALL_ABSOLUTE") == null ? "true" :config.getProperty("SEND_ALL_ABSOLUTE") ;
        appConfig.put("SEND_ALL_ABSOLUTE", SEND_ALL_ABSOLUTE);
        String SEND_ALL_DELTA = config.getProperty("SEND_ALL_DELTA") == null ? "true" : config.getProperty("SEND_ALL_DELTA") ;
        appConfig.put("SEND_ALL_DELTA", SEND_ALL_DELTA);
        
        String vcsTag = "east" ;
//        		config.getProperty("VCS_TAG");
        appConfig.put("vcsTag", vcsTag);
        
        int sleep_time = Integer.parseInt(SLEEP_TIME);
        if((sleep_time % 10) == 0) {
            int periods =  sleep_time / 10;
            appConfig.put("PERIODS", String.valueOf(periods));
        }
	    
		Enumeration<String> keys = inperfKeys.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
//			System.out.println(key + "|" + perfKeys.get(key).get("key") + "." +perfKeys.get(key).get("rollup"));
		}
		
		MeGrabber me_grabber = new MeGrabber(si, vm_mob_queue, esx_mob_queue, appConfig, sender);
		ExecutorService grab_exe = Executors.newCachedThreadPool();
		grab_exe.execute(me_grabber);
		
		VMStatsSender graphite = new VMStatsSender(sender, appConfig);
		graphite.setInterfaceAddress(interfaceAddress);
		graphite.setServiceAddress(serviceAddress);
        ExecutorService graph_exe = Executors.newCachedThreadPool();
        graph_exe.execute(graphite);
		
		for (int i = 1; i <= MAX_VMSTAT_THREADS; i++) {
			StatsGrabber vm_stats_grabber = new StatsGrabber(perfMgr, inperfKeys, vm_mob_queue, sender, appConfig, "vm");
	        ExecutorService vm_stat_exe = Executors.newCachedThreadPool();
			vm_stat_exe.execute(vm_stats_grabber);
		}

		for (int i = 1; i <= MAX_ESXSTAT_THREADS; i++) {
			StatsGrabber esx_stats_grabber = new StatsGrabber(perfMgr, inperfKeys, esx_mob_queue, sender, appConfig,
					"ESX");
			ExecutorService esx_stat_exe = Executors.newCachedThreadPool();
			esx_stat_exe.execute(esx_stats_grabber);
		}
	}
	
	public static void main(String[] args) throws RemoteException, MalformedURLException {
		ServiceInstance si = new ServiceInstance(new URL("https://172.29.230.50/sdk"), "administrator@vsphere.local",
				"taizhang123Purvar.", true);
//		getPerformaceMetric(si);
	}

}

package com.purvar.collector.vCenterCollector;

// this is a Producer in the arrangement
// this goes and gets a list of managed entities to send to statsGrabber

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

// TODO: Refactor to remove the code duplication in this class.
class MeGrabber implements Runnable{
	
	private final BlockingQueue<Object> vm_mob_queue;
	private final BlockingQueue<Object> esx_mob_queue;
	private final List<Object> vm_cache;
	private final List<Object> esx_cache;
    private final BlockingQueue<Object> sender;
    private HashMap<String, String> clusterMap;
	private final Hashtable<String, String> appConfig;
	private final ServiceInstance si;
	private static final Logger logger = LoggerFactory.getLogger(MeGrabber.class);
    private volatile boolean cancelled;

	public MeGrabber(ServiceInstance si, 
			BlockingQueue<Object> vm_mob_queue,
            BlockingQueue<Object> esx_mob_queue, 
            Hashtable<String, String> appConfig, 
            BlockingQueue<Object> sender) {
		this.vm_mob_queue = vm_mob_queue;
		this.esx_mob_queue = esx_mob_queue;
		this.appConfig = appConfig;
		this.si = si;
        this.sender = sender;
        this.clusterMap = new HashMap<String, String>();
        this.vm_cache = new ArrayList<Object>();
        this.esx_cache = new ArrayList<Object>();
	}
	
    public void cancel() {
        this.cancelled = true;
    }
    
    public void copyCacheToQueue(BlockingQueue<Object> queue, List<Object> cache, String name) {
    	// Do not fill up the statsQueue too much!
		if (vm_mob_queue.size() > 3*vm_cache.size()) {
			logger.info("Did not copy {} cache to queue because it is too full. Is vCenter down, or do you need more workers?", name);
			return;
		}
		
    	try {
    		for (Object item : cache) {
	    		queue.put(item);
	    	}
    	
	    	logger.info("Copied over {} cached {} objects into queue that has a length of {}", new Object[]{cache.size(), name, queue.size()});
	    	
    	} catch (InterruptedException e) {
    		Thread.currentThread().interrupt();
			logger.info("Interrupted Thread: " + Thread.currentThread().getName() + " +  Interrupted: " + e.getMessage());
            System.exit(202);
		}
    }
    
    public void refreshVMCache() {
    	vm_cache.clear();
    	   	
    	long start = System.currentTimeMillis();
		ManagedEntity[] vms = null;
		ManagedEntity[] clusters = null;
		clusterMap = new HashMap<String, String>();
        for(int i = 0; i < Integer.parseInt(appConfig.get("MAX_VMSTAT_THREADS")); i++) {
            String stats = "start_stats";
            vm_cache.add(stats);
        }
		try {
			// VirtualMachine NOT VirtualMachines
			// get a list of virtual machines
			vms = new InventoryNavigator(this.si.getRootFolder()).searchManagedEntities("VirtualMachine");
			clusters = new InventoryNavigator(si.getRootFolder()).searchManagedEntities(new String[][] { new String[] { "ClusterComputeResource", "host", "name"}, }, true);
		} catch(RemoteException e) {
			e.getStackTrace();
			logger.info("vm grab exception: " + e);
            System.exit(200);
		}
		
		
		for (ManagedEntity cluster : clusters) {
			String name = cluster.getName();
			ManagedObjectReference[] hosts = (ManagedObjectReference[]) cluster.getPropertyByPath("host");
			if (hosts == null) continue;
			for (ManagedObjectReference host : hosts) {
				clusterMap.put(host.val, name.replace(" ", "_").replace(".", "_"));
			}
		}
		
		if (vms != null) {
            logger.info("Found " + vms.length + " Virtual Machines");
			// if they're not null, loop through them and send them to the statsGrabber thread to get stats for.
            for (ManagedEntity vm : vms) {
                if (vm != null) {
                    String cluster = "none";
                    ManagedObjectReference host = (ManagedObjectReference) vm.getPropertyByPath("runtime.host");
                    if (clusterMap.containsKey(host.val)) {
                    	cluster = clusterMap.get(host.val);
                    }
                    vm_cache.add(new Object[] { vm, cluster });
                }
            }
		}else{
            logger.info("Found null virtual machines. Something's probably wrong.");
        }
		long vm_stop = System.currentTimeMillis();
		long vm_loop_took = vm_stop - start;
		logger.debug("meGrabber VM loop took " + vm_loop_took + "ms.");

        for(int i = 0; i < Integer.parseInt(appConfig.get("MAX_VMSTAT_THREADS")); i++) {
            String stats = "stop_stats";
            vm_cache.add(stats);
        }
    }
    
    public void refreshESXCache() {
    	long start = System.currentTimeMillis();
        long esx_loop_took = 0;
		String graphEsx = this.appConfig.get("graphEsx");
		if (graphEsx.contains("true")) {
            for(int i = 0; i < Integer.parseInt(appConfig.get("MAX_ESXSTAT_THREADS")); i++) {
                String stats = "start_stats";
                esx_cache.add(stats);
            }
			ManagedEntity[] esx = null;
			// get the esx nodes, aka HostSystem
			try {
				esx = new InventoryNavigator(this.si.getRootFolder()).searchManagedEntities("HostSystem");
			} catch(RemoteException e) {
				e.getStackTrace();
				logger.info("vm grab exception: " + e);
                System.exit(201);
			}
			
			logger.info("Found " + esx.length + " ESX Hosts");
			if (esx != null) {
				// if they're not null, loop through them and send them to the statsGrabber thread to get stats for.
                for (ManagedEntity anEsx : esx) {
                    if (anEsx != null) {
                    	String cluster = "none";
                    	
                    	String id = anEsx.getMOR().val;
                    	if (clusterMap.containsKey(id)) {
                        	cluster = clusterMap.get(id);
                        }
                    	
                        esx_cache.add(new Object[]{ anEsx, cluster});
                    }
                }
			}			
			
			esx_loop_took = System.currentTimeMillis() - start;
			logger.debug("meGrabber ESX loop took " + esx_loop_took + "ms.");
            for(int i = 0; i < Integer.parseInt(appConfig.get("MAX_ESXSTAT_THREADS")); i++) {
                String stats = "stop_stats";
                esx_cache.add(stats);
            }
		}
    }

	public void run() {
		int cachedLoopCounter;
		int cachedLoopCycles = Integer.parseInt(appConfig.get("CACHED_LOOP_CYCLES"));
		int user_sleep_time = Integer.parseInt(appConfig.get("SLEEP_TIME")) * 1000;
		String dump = "dump_stats";
		
		try {
			while(!cancelled) {
				refreshVMCache();
				refreshESXCache();
				cachedLoopCounter = 0;
				
				while (cachedLoopCounter < cachedLoopCycles) {
					copyCacheToQueue(vm_mob_queue, vm_cache, "vm");
					copyCacheToQueue(esx_mob_queue, esx_cache, "esx");
					sender.put(dump);
					cachedLoopCounter++;
					System.gc();
					Thread.sleep(user_sleep_time);
				}
			}
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.info("Interrupted Thread: " + Thread.currentThread().getName() + " +  Interrupted: " + e.getMessage());
            System.exit(202);
		} catch(Exception e) {
			System.out.println(Arrays.toString(e.getStackTrace()));
			logger.info("Other Exception Thread: " + Thread.currentThread().getName() + " +  Interrupted: " + e.getMessage());
            System.exit(203);
		}
		
	}

}

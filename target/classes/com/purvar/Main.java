package com.purvar;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.purvar.service.RunService;

public class Main {

	private static final RunService runService = new RunService();
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		if(args.length == 1){
			if("stop".equals(args[0])){
				logger.info("stop ......");
				runService.stop();
				logger.info("success ......");
				return ;
			} 
//			else if("restart".equals(args[0])){ //没有start的 直接restart
//			}
		}
		else if(args.length == 2){
			String configPath = args[1];
			if("start".equals(args[0])){
				logger.info("Collector is Starting ......");
				
				if(!runService.checkRun()){
					runService.start(configPath);
					logger.info("Collector is Running ......");
				}else{
					logger.error("Collector is Running ......");
				}
				return ;
			}
			else if("restart".equals(args[0])){
				logger.info("restart ......");
				runService.start(configPath);
				logger.info("success ......");
				return ;
			}
		}
		throw new IllegalArgumentException(Arrays.toString(args));
	}
}

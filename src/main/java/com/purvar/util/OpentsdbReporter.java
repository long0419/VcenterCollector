//package com.purvar.util;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.founder.apmsys_opentsdb.TsdbRequest;
//import com.founder.apmsys_opentsdb.api.PutDataRequest;
//import com.founder.apmsys_opentsdb.param.DataPointReq;
//import com.founder.apmsys_opentsdb.reponse.PutResDto;
//import com.purvar.collector.vCenterCollector.dto.MetricDataDto;
//import com.purvar.collector.vCenterCollector.dto.Tags;
//
//public class OpentsdbReporter {
//
//	public static void main(String[] args) {
//        String test1 = "152-WEBサーバ、Batch-centos6-4cpu-8g-100g";
//        char[] chars_test1 = test1.toCharArray();
//        for (int i = 0; i < chars_test1.length; i++) {
//            String temp = String.valueOf(chars_test1[i]);
//            // 判断是全角字符
//            if (temp.matches("[^\\x00-\\xff]")) {
//               System.out.println("全角   " + temp);
//            }
//            // 判断是半角字符
//            else {
//               System.out.println("半角    " + temp);
//            }
//        }
//	}
//
//	
//	public static void pushMetrics_DefaultRetries(List<MetricDataDto> metrics) {
//		for(MetricDataDto mdd : metrics){
//			Tags tag = mdd.getTags() ;
//
//			DataPointReq dp = new DataPointReq();
//			dp.setMetric(mdd.getMetric());
//			dp.setTimestamp(mdd.getTimestamp());
//			dp.setValue(mdd.getValue());
//			Map<String , String> tags =  new HashMap<String , String>();
//			tags.put("host", tag.getVirtualMachine()) ;
//			tags.put("name", tag.getHostSystem()) ;
//			dp.setTags(tags);
//
//			TsdbRequest<DataPointReq, PutResDto> tsdb = new PutDataRequest() ;
//			tsdb.setHttpParam(dp);
//			
//			try {
//				if(tag.getVirtualMachine().equals("84-粮网04-centos5_8-4cpu-8g-200g") 
//						|| tag.getVirtualMachine().equals("日本事业部Linux216")){
//					PutResDto putDto = tsdb.sendHttpRequest() ;
//					System.out.println("success " + putDto.getSuccess() + " name " + tag.getVirtualMachine());
//					Thread.sleep(300);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		
//	}
//	
//}
package com.purvar.collector.vCenterCollector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.purvar.collector.vCenterCollector.dto.MetricType;

/**
 * 获取vm 指标数据内容 并解释
 * @author long-laptop
 * 2017.6.16
 */
public class MetricTypeUtil {

	public static void main(String[] args) {
		try {
			MetricType metricType = null ;
			List<MetricType> mtList = new ArrayList<MetricType>() ;
			URL url = new URL("http://www.doublecloud.org/2010/12/vsphere-performance-counters-for-monitoring-esx-and-vcenter/");
			Document doc = Jsoup.parse(url, 3 * 1000); // 解析获取Document对象
			Elements test = doc.select("tbody"); // 获取页面上所有的a元素
			for (Element element : test) {
				for(int i = 0 ; i < element.select("tr").size() ; i++){
					metricType = new MetricType() ;
					metricType.setIntegration("vm");
					metricType.setMetricType("gauge");
					for(int k = 0 ; k < element.select("tr").get(i).select("td").size() ; k++){
						switch (k) {
						case 0:
							metricType.setId(element.select("tr").get(i).select("td").get(k).text());
							break;
						case 1:
							metricType.setMetricName(element.select("tr").get(i).select("td").get(k).text());
							break;
						case 2:
							metricType.setLevel(element.select("tr").get(i).select("td").get(k).text());
							break;
						case 3:
							metricType.setPerUnit(element.select("tr").get(i).select("td").get(k).text());
							break;
						case 4:
							metricType.setPluralUnit(element.select("tr").get(i).select("td").get(k).text());
							break;
						case 5:
							metricType.setDescription(element.select("tr").get(i).select("td").get(k).text());
							break;
						}
					}
					mtList.add(metricType) ;
				}
			}
			
	        Properties prop = new Properties();  
	        try {  
//	        	Path resourceDirectory = Paths.get("src/main/resources/metricType.properties");
//				String filePath = resourceDirectory.toString() ;
	        	
	        	String filePath = "config" ;
	            InputStream fis = new FileInputStream(filePath);
	            prop.load(fis);  
	            OutputStream fos = new FileOutputStream(filePath);  
	            prop.setProperty("MTJson", new Gson().toJson(mtList));  
	            prop.store(fos, new Gson().toJson(mtList));  
	        } catch (IOException e) {
	        	e.printStackTrace(); 
	        }  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		getMeticTypeList() ;
	}
	
	public static String getMeticTypeList(){
		Properties prop = new Properties();
//		Path resourceDirectory = Paths.get("src/main/resources/metricType.properties");
//		String filePath = resourceDirectory.toString() ;
		
		String filePath = "config" ;
        InputStream fis;
		try {
			fis = new FileInputStream(filePath);
			prop.load(fis);  
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return prop.getProperty("MTJson") ;
	}

}

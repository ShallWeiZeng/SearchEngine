package com.zsw.spring.springmvc.IndexBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class LoadDataToRedis {

	public static void main(String[] args){
		
		Jedis jedis = new Jedis("127.0.0.1",6379);
		
		Pipeline pipeline = jedis.pipelined();
		File[] list = new File("F:\\Zhihu").listFiles();
		Map<String,String>  map = new HashMap();
		Date start = new Date();
		int count = 0;
		try{
			for (File file : list) {
				File[] f = file.listFiles();
				
				for (File file2 : f) {
					String[] t= readTextFile(file2);
					map.clear();
					map.put("filename", file2.getName().substring(0, file2.getName().length()-4));
					map.put("content", t[1]);
					count++;
					pipeline.hmset(t[0], map);
					if(count%2000 ==0){
						pipeline.sync();
						System.out.println("成功插入2000条数据");
					}
					
				}
			}
			if(count%2000!=0){
				System.out.println("count:"+count);
				pipeline.sync();
				System.out.println("成功插入"+(count%2000)+"条数据");
			}
			Date end = new Date();
			
//			System.out.println("start-end:"+(end.getSeconds()-start.getSeconds())+"s");
			System.out.println("start-end:"+(end.getTime()-start.getTime())+"ms");
		}catch(Exception e){
			
		}finally{
			if(jedis!=null){
				jedis.disconnect();
			}
		}
	}
	
	
	public static synchronized String[] readTextFile(File file){
		String[] t = new String[2];
		try {
			String txt="";
			String answer_url="";
			String encoding = "utf-8";
			if(file.isFile()&&file.exists()){
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file),encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				
				
				answer_url = bufferedReader.readLine();
				//System.out.println("answer_url:"+bufferedReader.readLine());
				//Thread.sleep(50000);
				String lineText = null;
				while((lineText = bufferedReader.readLine())!=null){
					if(txt.length()>100){
						break;
					}
					if(lineText.length()>100){
						lineText = lineText.substring(0, 70);
						txt+=lineText;
						break;
					}
					txt+=lineText;
				}
				//System.out.println(txt);
				read.close();
				t[0]=answer_url;
				t[1]=txt;
				
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		return t;
	}
}

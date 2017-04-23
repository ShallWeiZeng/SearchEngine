package com.zsw.spring.springmvc.IndexBuilder;

import java.io.File;



public class Producer implements Runnable{
	
	private final File[] files = new File("E:\\Zhihu").listFiles();
	
	
	
	@Override
	public void run(){
		for(File file:files)
		{
//			System.out.println("file:"+file.getName());
			try {
				
				IndexBuilder.pools.offer(file);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	}

}
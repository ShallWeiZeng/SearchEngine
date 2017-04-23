package com.zsw.spring.springmvc.IndexBuilder;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class IndexBuilder {

	public static ResourcePool<File> pools = new ResourcePool<File>(5000);
	
	public static void main(String[] args){
		
		ExecutorService executor = Executors.newFixedThreadPool(6);
		for(int i=0;i<5;i++)
			executor.execute(new Consumer());
        
        executor.execute(new Producer());
	}

}
